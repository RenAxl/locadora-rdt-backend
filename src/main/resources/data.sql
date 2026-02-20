INSERT INTO tb_user
(name, email, password, active, telephone, address, photo_data, photo_content_type, date)
VALUES
    ('Renan Duarte', 'renan.duarte@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990001', 'Rua Alfa, 100 - Belo Horizonte - MG', NULL, NULL, '2025-01-10 09:00:00'),
    ('Ana Silva', 'ana.silva@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990002', 'Rua Beta, 200 - Belo Horizonte - MG', NULL, NULL, '2025-01-11 10:15:00'),
    ('Bruno Costa', 'bruno.costa@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990003', 'Av. Amazonas, 300 - Belo Horizonte - MG', NULL, NULL, '2025-01-12 11:30:00'),
    ('Carla Mendes', 'carla.mendes@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990004', 'Rua Gama, 400 - Belo Horizonte - MG', NULL, NULL, '2025-01-13 14:45:00'),
    ('Daniel Rocha', 'daniel.rocha@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990005', 'Rua Delta, 500 - Belo Horizonte - MG', NULL, NULL, '2025-01-14 16:00:00'),
    ('Eduarda Lima', 'eduarda.lima@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', false, '31999990006', 'Av. Brasil, 600 - Belo Horizonte - MG', NULL, NULL, '2025-01-15 09:20:00'),
    ('Felipe Nogueira', 'felipe.nogueira@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990007', 'Rua Goiás, 700 - Belo Horizonte - MG', NULL, NULL, '2025-01-16 13:10:00'),
    ('Gabriela Torres', 'gabriela.torres@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990008', 'Av. Contorno, 800 - Belo Horizonte - MG', NULL, NULL, '2025-01-17 15:35:00'),
    ('Henrique Alves', 'henrique.alves@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', false, '31999990009', 'Rua da Bahia, 900 - Belo Horizonte - MG', NULL, NULL, '2025-01-18 17:50:00'),
    ('Isabela Pires', 'isabela.pires@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990010', 'Av. Cristiano Machado, 1000 - Belo Horizonte - MG', NULL, NULL, '2025-01-19 18:30:00');


INSERT INTO tb_role (authority) VALUES
                                    ('ROLE_ADMINISTRADOR'),
                                    ('ROLE_GERENTE'),
                                    ('ROLE_ATENDENTE'),
                                    ('ROLE_FINANCEIRO'),
                                    ('ROLE_CLIENTE');


INSERT INTO tb_permission (name, group_name) VALUES
                                                 ('USER_READ',          'USERS'),
                                                 ('USER_WRITE',         'USERS'),
                                                 ('USER_DELETE',        'USERS'),
                                                 ('USER_STATUS_CHANGE', 'USERS'),

                                                 ('ROLE_READ',          'ROLES'),
                                                 ('ROLE_WRITE',         'ROLES'),
                                                 ('ROLE_DELETE',        'ROLES');


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
                                                            (1, 5),(1, 6),(1, 7);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (2, 1),
                                                            (2, 5);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
    (4, 1);


