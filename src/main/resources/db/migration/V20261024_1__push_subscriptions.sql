-- Ключи VAPID для Web Push: генерируются сервисом при первом запуске (одна строка)
CREATE TABLE push_vapid_keys (
    id          INT PRIMARY KEY,
    public_key  TEXT NOT NULL,
    private_key TEXT NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Подписки устройств админки (iPhone на экране Домой и т.п.)
CREATE TABLE push_subscriptions (
    id         UUID PRIMARY KEY,
    endpoint   TEXT NOT NULL UNIQUE,
    p256dh     TEXT NOT NULL,
    auth       TEXT NOT NULL,
    user_agent TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
