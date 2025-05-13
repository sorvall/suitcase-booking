package ru.sorokin.booking.util;

public class NotificationTemplates {
    private NotificationTemplates() {
        // Приватный конструктор, чтобы не создавать экземпляры
    }

    public static final String BOOKING_CREATED_TEMPLATE =
            "Оформлен новый заказ %s с %s по %s %s %s Доставка %s";
}