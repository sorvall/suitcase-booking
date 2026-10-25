-- Статистика запросов проверки доступности (сущность Statistic).
-- В старой базе таблицу создали вручную, поэтому IF NOT EXISTS: на ней миграция ничего не меняет,
-- а новая пустая база получает таблицу сразу.
CREATE TABLE IF NOT EXISTS statistics (
    id         UUID PRIMARY KEY,
    amount     INTEGER,
    updated    TIMESTAMP WITH TIME ZONE,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL
);
