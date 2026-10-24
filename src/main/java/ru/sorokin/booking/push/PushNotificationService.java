package ru.sorokin.booking.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import nl.martijndwars.webpush.Encoding;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Utils;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Web Push для админки: хранит подписки устройств и рассылает уведомления.
 * Ключи VAPID генерируются при первом запуске и хранятся в БД, поэтому не меняются между деплоями.
 */
@Service
public class PushNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationService.class);

    public record SubscriptionDto(String endpoint, Keys keys) {
        public record Keys(String p256dh, String auth) {
        }
    }

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    // Отправка не должна задерживать оформление заказа
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "web-push"));
    private final String publicKey;
    private final PushService pushService;

    public PushNotificationService(JdbcTemplate jdbcTemplate,
                                   @Value("${push.subject:https://bikeboxgo.ru}") String subject) throws GeneralSecurityException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        this.jdbcTemplate = jdbcTemplate;
        String[] keys = loadOrCreateVapidKeys();
        this.publicKey = keys[0];
        this.pushService = new PushService(keys[0], keys[1], subject);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void subscribe(SubscriptionDto dto, String userAgent) {
        if (dto == null || dto.endpoint() == null || dto.keys() == null
                || dto.keys().p256dh() == null || dto.keys().auth() == null) {
            throw new IllegalArgumentException("Некорректная подписка");
        }
        jdbcTemplate.update("""
                        INSERT INTO push_subscriptions (id, endpoint, p256dh, auth, user_agent)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT (endpoint) DO UPDATE
                            SET p256dh = EXCLUDED.p256dh, auth = EXCLUDED.auth, user_agent = EXCLUDED.user_agent
                        """,
                UUID.randomUUID(), dto.endpoint(), dto.keys().p256dh(), dto.keys().auth(), userAgent);
        LOGGER.info("Push subscription saved, total {}", countSubscriptions());
    }

    public void unsubscribe(String endpoint) {
        jdbcTemplate.update("DELETE FROM push_subscriptions WHERE endpoint = ?", endpoint);
    }

    public int countSubscriptions() {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM push_subscriptions", Integer.class);
        return count == null ? 0 : count;
    }

    /** Асинхронно отправляет уведомление на все подписанные устройства. */
    public void sendToAll(String title, String body, String url) {
        byte[] payload;
        try {
            payload = mapper.writeValueAsBytes(Map.of("title", title, "body", body, "url", url));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        executor.submit(() -> deliver(payload));
    }

    private void deliver(byte[] payload) {
        List<Subscription> subscriptions = jdbcTemplate.query(
                "SELECT endpoint, p256dh, auth FROM push_subscriptions",
                (rs, i) -> new Subscription(rs.getString("endpoint"),
                        new Subscription.Keys(rs.getString("p256dh"), rs.getString("auth"))));
        for (Subscription subscription : subscriptions) {
            try {
                // Apple и современные браузеры ждут шифрование aes128gcm (RFC 8291)
                HttpResponse response = pushService.send(
                        new Notification(subscription, new String(payload, StandardCharsets.UTF_8)), Encoding.AES128GCM);
                int status = response.getStatusLine().getStatusCode();
                if (status == 404 || status == 410) {
                    // Устройство отписалось или удалило приложение — подписка больше не действует
                    unsubscribe(subscription.endpoint);
                    LOGGER.info("Push subscription expired ({}), removed", status);
                } else if (status >= 300) {
                    LOGGER.warn("Push service answered {} for {}", status, host(subscription.endpoint));
                }
            } catch (Exception e) {
                LOGGER.warn("Push to {} failed: {}", host(subscription.endpoint), e.toString());
            }
        }
    }

    private String[] loadOrCreateVapidKeys() throws GeneralSecurityException {
        List<String[]> rows = jdbcTemplate.query("SELECT public_key, private_key FROM push_vapid_keys WHERE id = 1",
                (rs, i) -> new String[]{rs.getString(1), rs.getString(2)});
        if (!rows.isEmpty()) {
            return rows.get(0);
        }
        KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
        generator.initialize(ECNamedCurveTable.getParameterSpec("prime256v1"), new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String publicKey = encoder.encodeToString(Utils.encode((ECPublicKey) keyPair.getPublic()));
        String privateKey = encoder.encodeToString(Utils.encode((ECPrivateKey) keyPair.getPrivate()));
        jdbcTemplate.update("INSERT INTO push_vapid_keys (id, public_key, private_key) VALUES (1, ?, ?) ON CONFLICT (id) DO NOTHING",
                publicKey, privateKey);
        LOGGER.info("Generated new VAPID keys for Web Push");
        // Перечитываем: при одновременном старте двух экземпляров выиграет первая запись
        return jdbcTemplate.queryForObject("SELECT public_key, private_key FROM push_vapid_keys WHERE id = 1",
                (rs, i) -> new String[]{rs.getString(1), rs.getString(2)});
    }

    private static String host(String endpoint) {
        try {
            return URI.create(endpoint).getHost();
        } catch (IllegalArgumentException e) {
            return "?";
        }
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }
}
