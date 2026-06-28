-- test-data.sql
-- Datos de prueba para la base de datos H2 durante tests
-- Este archivo se ejecuta automáticamente según la configuración en application-test.yml

-- NOTAS:
-- 1. Los IDs se generan automáticamente (AUTO_INCREMENT)
-- 2. Use este archivo para pre-popular datos que los tests necesiten
-- 3. Los tests con @Transactional hacen rollback automático

-- ===== ORGANIZACIONES =====
INSERT INTO org_organizations (name, created_at) VALUES 
('EcoTrack Dev Org', NOW()),
('QA Organization', NOW());

-- ===== USUARIOS =====
-- Contraseña hasheada con bcrypt (en un setup real, usar PasswordEncoder)
-- Para los tests, usar credenciales simples o no validar contraseña
INSERT INTO usr_users (email, password_hash, first_name, last_name, role, organization_id, is_active, consent_given, created_at) VALUES
('admin@ecotrack.com', '$2a$10$example_hash_admin', 'Admin', 'User', 'ADMIN', 1, true, true, NOW()),
('user1@ecotrack.com', '$2a$10$example_hash_user1', 'Juan', 'García', 'USER', 1, true, true, NOW()),
('user2@ecotrack.com', '$2a$10$example_hash_user2', 'María', 'López', 'USER', 1, true, true, NOW()),
('qa@ecotrack.com', '$2a$10$example_hash_qa', 'QA', 'Tester', 'ADMIN', 2, true, true, NOW());

-- ===== PISOS =====
INSERT INTO ast_floors (level, is_active, organization_id, name, created_at) VALUES
(0, true, 1, 'Planta Baja', NOW()),
(1, true, 1, 'Piso 1 - Desarrollo', NOW()),
(2, true, 1, 'Piso 2 - Gestión', NOW()),
(1, true, 2, 'Piso 1 - QA', NOW());

-- ===== SALAS =====
INSERT INTO ast_rooms (name, capacity, is_active, floor_id, room_type, created_at) VALUES
('Sala A1', 6, true, 1, 'OPEN_SPACE', NOW()),
('Sala B1', 8, true, 1, 'OPEN_SPACE', NOW()),
('Reuniones 101', 4, true, 1, 'MEETING_ROOM', NOW()),
('Reuniones 102', 8, true, 2, 'MEETING_ROOM', NOW()),
('Lab QA 201', 10, true, 4, 'OPEN_SPACE', NOW());

-- ===== DESKS (Escritorios) =====
INSERT INTO ast_resources (name, status, is_active, equipment_list, resource_type, room_id, created_at) VALUES
('Desk A-101', 'AVAILABLE', true, 'Pantalla dual,Teclado mecánico,Ratón', 'DESK', 1, NOW()),
('Desk A-102', 'AVAILABLE', true, 'Pantalla dual,Teclado,Ratón', 'DESK', 1, NOW()),
('Desk A-103', 'AVAILABLE', true, 'Monitor 27in,Ergonomic chair', 'DESK', 1, NOW()),
('Desk B-101', 'AVAILABLE', true, 'Pantalla,Teclado', 'DESK', 2, NOW()),
('Desk B-102', 'AVAILABLE', true, 'Monitor 27in', 'DESK', 2, NOW()),
('Proyector 101', 'AVAILABLE', true, 'HD,Sonido incorporado', 'EQUIPMENT', 3, NOW()),
('Videoconferencia 102', 'AVAILABLE', true, 'HD,Micrófono array', 'EQUIPMENT', 4, NOW()),
('Lab Desk 201', 'AVAILABLE', true, 'Doble monitor,Workstation', 'DESK', 5, NOW());

-- ===== RESERVACIONES (Ejemplos) =====
-- Algunas reservaciones para las pruebas
INSERT INTO rsv_reservations (date, status, user_id, resource_id, created_at) VALUES
(CURRENT_DATE + 1, 'CONFIRMED', 1, 1, NOW()),
(CURRENT_DATE + 2, 'CONFIRMED', 1, 2, NOW()),
(CURRENT_DATE + 3, 'CONFIRMED', 2, 3, NOW()),
(CURRENT_DATE + 5, 'CANCELLED', 3, 1, NOW()),
(CURRENT_DATE + 7, 'CONFIRMED', 2, 4, NOW());

-- ===== REPORTES (Opcional) =====
-- Ejemplos de reportes analíticos
INSERT INTO ana_analyticsreports (report_type, date_from, date_to, organization_id, report_data, created_at) VALUES
('DESK_USAGE', CURRENT_DATE - 30, CURRENT_DATE, 1, '{"occupancy":"75%","totalDesks":5}', NOW()),
('ROOM_USAGE', CURRENT_DATE - 30, CURRENT_DATE, 1, '{"meetingRooms":2,"usage":"60%"}', NOW());
