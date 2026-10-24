package ru.sorokin.booking.push;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Подписка админки на Web Push. Доступ только после входа — проверяет gateway. */
@RestController
@RequestMapping("/push")
public class PushController {
    private final PushNotificationService pushNotificationService;

    public PushController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @GetMapping("/public-key")
    public Map<String, String> publicKey() {
        return Map.of("publicKey", pushNotificationService.getPublicKey());
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Map<String, Integer>> subscribe(@RequestBody PushNotificationService.SubscriptionDto subscription,
                                                          @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        pushNotificationService.subscribe(subscription, userAgent);
        return ResponseEntity.ok(Map.of("subscriptions", pushNotificationService.countSubscriptions()));
    }

    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unsubscribe(@RequestBody PushNotificationService.SubscriptionDto subscription) {
        pushNotificationService.unsubscribe(subscription.endpoint());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Integer>> test() {
        pushNotificationService.sendToAll("BikeBoxGo", "Тестовое уведомление: push работает 👍", "/admin");
        return ResponseEntity.accepted().body(Map.of("subscriptions", pushNotificationService.countSubscriptions()));
    }
}
