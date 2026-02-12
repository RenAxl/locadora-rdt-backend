INSERT INTO tb_user
(name, email, password, active, telephone, address, photo, date)
VALUES
    ('Renan Duarte', 'renan.duarte@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990001', 'Rua Alfa, 100 - Belo Horizonte - MG', NULL, '2025-01-10T09:00:00Z'),
    ('Ana Silva', 'ana.silva@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990002', 'Rua Beta, 200 - Belo Horizonte - MG', NULL, '2025-01-11T10:15:00Z'),
    ('Bruno Costa', 'bruno.costa@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990003', 'Av. Amazonas, 300 - Belo Horizonte - MG', NULL, '2025-01-12T11:30:00Z'),
    ('Carla Mendes', 'carla.mendes@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990004', 'Rua Gama, 400 - Belo Horizonte - MG', NULL, '2025-01-13T14:45:00Z'),
    ('Daniel Rocha', 'daniel.rocha@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990005', 'Rua Delta, 500 - Belo Horizonte - MG', NULL, '2025-01-14T16:00:00Z'),
    ('Eduarda Lima', 'eduarda.lima@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', FALSE, '31999990006', 'Av. Brasil, 600 - Belo Horizonte - MG', NULL, '2025-01-15T09:20:00Z'),
    ('Felipe Nogueira', 'felipe.nogueira@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990007', 'Rua Goiás, 700 - Belo Horizonte - MG', NULL, '2025-01-16T13:10:00Z'),
    ('Gabriela Torres', 'gabriela.torres@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990008', 'Av. Contorno, 800 - Belo Horizonte - MG', NULL, '2025-01-17T15:35:00Z'),
    ('Henrique Alves', 'henrique.alves@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', FALSE, '31999990009', 'Rua da Bahia, 900 - Belo Horizonte - MG', NULL, '2025-01-18T17:50:00Z'),
    ('Isabela Pires', 'isabela.pires@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', TRUE,  '31999990010', 'Av. Cristiano Machado, 1000 - Belo Horizonte - MG', NULL, '2025-01-19T18:30:00Z');


INSERT INTO tb_role (authority) VALUES
                                    ('ROLE_ADMINISTRADOR'),
                                    ('ROLE_GERENTE'),
                                    ('ROLE_ATENDENTE'),
                                    ('ROLE_FINANCEIRO'),
                                    ('ROLE_CLIENTE');


INSERT INTO tb_permission (name, group_name) VALUES
('USER_READ',   'USERS'),
('USER_WRITE',  'USERS'),
('USER_DELETE', 'USERS'),
('USER_STATUS_CHANGE', 'USERS'),

('ROLE_READ',   'ROLES'),
('ROLE_WRITE',  'ROLES'),
('ROLE_DELETE', 'ROLES'),

('PERMISSION_READ', 'PERMISSIONS'),

('CATEGORY_READ',   'CATEGORIES'),
('CATEGORY_WRITE',  'CATEGORIES'),
('CATEGORY_DELETE', 'CATEGORIES'),


('CHARACTER_READ',   'CHARACTERS'),
('CHARACTER_WRITE',  'CHARACTERS'),
('CHARACTER_DELETE', 'CHARACTERS');


INSERT INTO tb_user_role (user_id, role_id) VALUES
                                                (1, 1),
                                                (2, 2),
                                                (3, 3),
                                                (4, 4),
                                                (5, 5),
                                                (6, 5),
                                                (7, 3),
                                                (8, 2),
                                                (9, 4),
                                                (10, 5);



INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (1, 1),(1, 2),(1, 3),(1, 4),
                                                            (1, 5),(1, 6),(1, 7),
                                                            (1, 8),
                                                            (1, 9),(1,10),(1,11),
                                                            (1,12),(1,13),(1,14);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (2, 1),
                                                            (2, 9),(2,10),
                                                            (2,12),(2,13),
                                                            (2, 5);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (3, 9),(3,10),
                                                            (3,12),(3,13);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (4, 1),
                                                            (4, 9),
                                                            (4,12);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (5, 9),
                                                            (5,12);
