INSERT INTO tb_user
(name, email, password, active, telephone, address, photo_data, photo_content_type, date)
VALUES
    ('Renan Duarte', 'renandt30@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990001', 'Rua Alfa, 100 - Belo Horizonte - MG', NULL, NULL, '2025-01-10 09:00:00'),
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
                                                 ('ROLE_DELETE',        'ROLES'),

                                                 ('PERMISSION_READ',        'PERMISSIONS'),

                                                 ('CUSTOMER_READ',          'CUSTOMERS'),
                                                 ('CUSTOMER_WRITE',         'CUSTOMERS'),
                                                 ('CUSTOMER_DELETE',        'CUSTOMERS'),
                                                 ('CUSTOMER_STATUS_CHANGE', 'CUSTOMERS');







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
                                                            (1, 5),(1, 6),(1, 7), (1, 8),
                                                            (1, 9),(1, 10),(1, 11), (1, 12);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (2, 1),
                                                            (2, 5);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
    (4, 1);


INSERT INTO tb_customer (name, cpf, email, phone, address, active, photo_data, photo_content_type, created_at, updated_at)
VALUES
    ('João Silva', '11111111111', 'joao.silva@email.com', '31999990001', 'Rua A, 100 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Maria Oliveira', '22222222222', 'maria.oliveira@email.com', '31999990002', 'Rua B, 200 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Carlos Souza', '33333333333', 'carlos.souza@email.com', '31999990003', 'Rua C, 300 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Ana Costa', '44444444444', 'ana.costa@email.com', '31999990004', 'Rua D, 400 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Pedro Santos', '55555555555', 'pedro.santos@email.com', '31999990005', 'Rua E, 500 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Juliana Rocha', '66666666666', 'juliana.rocha@email.com', '31999990006', 'Rua F, 600 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Lucas Ferreira', '77777777777', 'lucas.ferreira@email.com', '31999990007', 'Rua G, 700 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Fernanda Alves', '88888888888', 'fernanda.alves@email.com', '31999990008', 'Rua H, 800 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Rafael Martins', '99999999999', 'rafael.martins@email.com', '31999990009', 'Rua I, 900 - Belo Horizonte', true, NULL, NULL, NOW(), NULL),

    ('Patricia Gomes', '10101010101', 'patricia.gomes@email.com', '31999990010', 'Rua J, 1000 - Belo Horizonte', true, NULL, NULL, NOW(), NULL);



INSERT INTO tb_position (name, created_at) VALUES
                                               ('Atendente', NOW()),
                                               ('Gerente', NOW()),
                                               ('Auxiliar Administrativo', NOW()),
                                               ('Assistente Financeiro', NOW()),
                                               ('Analista Financeiro', NOW()),
                                               ('Motorista', NOW()),
                                               ('Lavador de Veículos', NOW()),
                                               ('Supervisor de Operações', NOW());


