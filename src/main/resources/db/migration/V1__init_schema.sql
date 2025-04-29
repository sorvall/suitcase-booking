-- Создание таблицы users
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    phone VARCHAR(255),
    keycloak_id VARCHAR(255) NOT NULL UNIQUE
);

-- Создание таблицы suitcases
CREATE TABLE suitcases (
    id UUID PRIMARY KEY,
    serial_number VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    location VARCHAR(255),
    notes TEXT
);

-- Создание таблицы bookings
CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    suitcase_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_suitcase FOREIGN KEY (suitcase_id) REFERENCES suitcases (id)
);