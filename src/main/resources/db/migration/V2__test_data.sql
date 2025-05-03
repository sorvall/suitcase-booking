-- Тестовые пользователи
INSERT INTO users (id, email, full_name, phone, keycloak_id)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'test1@example.com', 'Test User 1','89654080754', 'keycloak-uid-1'),
  ('22222222-2222-2222-2222-222222222222', 'test2@example.com', 'Test User 2','89654080754', 'keycloak-uid-2');

-- Тестовые чемоданы
INSERT INTO suitcases (id, serial_number, status, location, notes)
VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'SN-001', 'AVAILABLE', 'Москва', 'Без дефектов'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'SN-002', 'AVAILABLE', 'Москва', 'Сломана ручка'),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'SN-003', 'REPAIR', 'Склад', 'На ремонте');