-- Wypełnienie tabeli _user
INSERT INTO _user (id, first_name, last_name, email, password, birth_date, hired_at, terminated_at, status, note, efficiency, user_role, address, phone_number, allow_notifications, is_archived, created_at, updated_at) VALUES
(1, 'Kamil', 'Bolerioza', 'kamil.bolerioza@example.com', '$2a$10$jEz9q8fjapLmAJtuDGUB5eiCWkbb.T4j5ztxFrjxA/4tZvsaOXfeC', '1990-01-01', '2020-01-01', NULL, 'ACTIVE', 'Brak uwag', 0.95, 'OWNER', 'Warszawa', '123456789', true, false, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(2, 'Aneta', 'Różyczka', 'aneta.rozyczka@example.com', '$2a$10$XOcSUQDpSg/B8NdYlFYFBOhhI0jdIwuM8yXyLcCxHYczD5JEqKBIe', '1988-02-02', '2019-03-15', NULL, 'ACTIVE', NULL, 0.87, 'MANAGER', 'Kraków', '987654321', true, false, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(3, 'Tomasz', 'Gruźlica', 'tomasz.gruzlica@example.com', '$2a$10$X5EZXoTjZXrBfd0N7vtCfeKHZIePn366fkbGGRlAQU.0ZLcgU42ve', '1992-03-03', '2021-06-01', NULL, 'SUSPENDED', NULL, 0.75, 'WORKER', 'Gdańsk', '456789123', false, false, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(4, 'Natalia', 'Odra', 'natalia.odra@example.com', '$2a$10$1wNLRx.ymH5XBIX7UfBIPu5YXe901vxcjVgqBbAHpaxcOnjLO/ZCu', '1995-04-04', '2022-08-01', NULL, 'ON_LEAVE', NULL, 0.6, 'WORKER', 'Poznań', '321654987', true, false, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(5, 'Marek', 'Ospa', 'marek.ospa@example.com', '$2a$10$DIkrF7TxqBKq9Z..YmgMbuvqynqguFIFJvbAUvcqf3TzZMrQb4erq', '1993-05-05', '2018-05-10', '2023-01-01', 'INACTIVE', 'Zwolniony za porozumieniem stron', 0.5, 'WORKER', 'Łódź', '654321789', false, true, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588');

-- Tabela team
INSERT INTO team (id, name, leader_id, created_at, updated_at) VALUES
(1, 'Zespół Alfa', 1, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(2, 'Zespół Beta', 2, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(3, 'Zespół Gamma', 1, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(4, 'Zespół Delta', 3, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(5, 'Zespół Epsilon', 4, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588');

-- Tabela team_members
INSERT INTO team_members (id, team_id, user_id, created_at) VALUES
(1, 1, 2, '2025-03-18 12:55:55.946588'),
(2, 1, 3, '2025-03-18 12:55:55.946588'),
(3, 2, 4, '2025-03-18 12:55:55.946588'),
(4, 2, 5, '2025-03-18 12:55:55.946588'),
(5, 3, 3, '2025-03-18 12:55:55.946588');

-- Tabela task
INSERT INTO task (id, task_progress, name, description, note, priority, start_date, end_date, is_archived, team_id, created_at, updated_at) VALUES
(1, 'NOT_STARTED', 'Zadanie A', 'Opis A', NULL, 'HIGH', '2025-04-01', '2025-04-10', false, 1, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(2, 'MIDWAY', 'Zadanie B', 'Opis B', NULL, 'MEDIUM', '2025-04-02', '2025-04-15', false, 2, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(3, 'COMPLETED_ACCEPTED', 'Zadanie C', 'Opis C', 'Zakończono', 'LOW', '2025-03-01', '2025-03-05', false, 3, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(4, 'EARLY_PROGRESS', 'Zadanie D', 'Opis D', NULL, 'HIGH', '2025-04-03', '2025-04-20', false, 1, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588'),
(5, 'CANCELLED', 'Zadanie E', 'Opis E', 'Anulowane', 'LOW', '2025-04-04', '2025-04-25', false, 2, '2025-03-18 12:55:55.946588', '2025-03-18 12:55:55.946588');

-- Tabela resource
INSERT INTO resource (id, name, quantity, resource_type, created_at) VALUES
(1, 'A', 10, 'FOR_SELL', '2025-03-18 12:55:55.946588'),
(2, 'B', 50, 'FOR_USE', '2025-03-18 12:55:55.946588'),
(3, 'C', 2, 'FOR_USE', '2025-03-18 12:55:55.946588'),
(4, 'D', 1, 'FOR_SELL', '2025-03-18 12:55:55.946588'),
(5, 'E', 5, 'FOR_SELL', '2025-03-18 12:55:55.946588');

-- Tabela task_resource
INSERT INTO task_resource (id, task_id, resource_id, task_resource_type, quantity, created_at) VALUES
(1, 1, 1, 'ASSIGNED', 2, '2025-03-18 12:55:55.946588'),
(2, 2, 2, 'ASSIGNED', 20, '2025-03-18 12:55:55.946588'),
(3, 3, 3, 'RETURNED', 1, '2025-03-18 12:55:55.946588'),
(4, 4, 4, 'ASSIGNED', 1, '2025-03-18 12:55:55.946588'),
(5, 5, 5, 'RETURNED', 1, '2025-03-18 12:55:55.946588');

-- Tabela user_task
INSERT INTO user_task (id, task_id, user_id, created_at) VALUES
(1, 1, 2, '2025-03-18 12:55:55.946588'),
(2, 2, 3, '2025-03-18 12:55:55.946588'),
(3, 3, 4, '2025-03-18 12:55:55.946588'),
(4, 4, 5, '2025-03-18 12:55:55.946588'),
(5, 5, 2, '2025-03-18 12:55:55.946588');

-- Tabela expense
INSERT INTO expense (id, name, description, resource_id, quantity, cost, created_at) VALUES
(1, 'Wydatek 1', 'Zakup materiału B', 2, 10, 500, '2025-03-18 12:55:55.946588'),
(2, 'Wydatek 2', 'Zakup materialu A', 3, 1, 2000, '2025-03-18 12:55:55.946588'),
(3, 'Wydatek 3', 'Zakup materialu C', 4, 1, 1500, '2025-03-18 12:55:55.946588'),
(4, 'Wydatek 4', 'Zakup Zasobu A', 1, 3, 300, '2025-03-18 12:55:55.946588'),
(5, 'Wydatek 5', 'Zakup Zasobu B', 5, 2, 250, '2025-03-18 12:55:55.946588');

-- Tabela token
INSERT INTO token (id, token, email, expired_at, created_at) VALUES
(1, '5829038475', 'kamil.bolerioza@example.com', '2025-04-06', '2025-03-18 12:55:55.946588'),
(2, '9283746501', 'aneta.rozyczka@example.com', '2025-04-06', '2025-03-18 12:55:55.946588'),
(3, '7349201856', 'tomasz.gruzlica@example.com', '2025-04-06', '2025-03-18 12:55:55.946588'),
(4, '1029384756', 'natalia.odra@example.com', '2025-04-06', '2025-03-18 12:55:55.946588'),
(5, '3847561920', 'marek.ospa@example.com', '2025-04-06', '2025-03-18 12:55:55.946588');
