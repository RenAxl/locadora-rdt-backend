INSERT INTO tb_user
(name, email, password, active, telephone, address, photo_data, photo_content_type, created_at, updated_at, created_by, updated_by)
VALUES
    ('Renan Duarte', 'renandt30@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990001', 'Rua Alfa, 100 - Belo Horizonte - MG', NULL, NULL, '2025-01-10T09:00:00Z', NULL, 'SYSTEM', NULL),

    ('Ana Silva', 'ana.silva@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990002', 'Rua Beta, 200 - Belo Horizonte - MG', NULL, NULL, '2025-01-11T10:15:00Z', NULL, 'SYSTEM', NULL),

    ('Bruno Costa', 'bruno.costa@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990003', 'Av. Amazonas, 300 - Belo Horizonte - MG', NULL, NULL, '2025-01-12T11:30:00Z', NULL, 'SYSTEM', NULL),

    ('Carla Mendes', 'carla.mendes@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990004', 'Rua Gama, 400 - Belo Horizonte - MG', NULL, NULL, '2025-01-13T14:45:00Z', NULL, 'SYSTEM', NULL),

    ('Daniel Rocha', 'daniel.rocha@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990005', 'Rua Delta, 500 - Belo Horizonte - MG', NULL, NULL, '2025-01-14T16:00:00Z', NULL, 'SYSTEM', NULL),

    ('Eduarda Lima', 'eduarda.lima@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', false, '31999990006', 'Av. Brasil, 600 - Belo Horizonte - MG', NULL, NULL, '2025-01-15T09:20:00Z', NULL, 'SYSTEM', NULL),

    ('Felipe Nogueira', 'felipe.nogueira@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990007', 'Rua Goiás, 700 - Belo Horizonte - MG', NULL, NULL, '2025-01-16T13:10:00Z', NULL, 'SYSTEM', NULL),

    ('Gabriela Torres', 'gabriela.torres@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990008', 'Av. Contorno, 800 - Belo Horizonte - MG', NULL, NULL, '2025-01-17T15:35:00Z', NULL, 'SYSTEM', NULL),

    ('Henrique Alves', 'henrique.alves@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', false, '31999990009', 'Rua da Bahia, 900 - Belo Horizonte - MG', NULL, NULL, '2025-01-18T17:50:00Z', NULL, 'SYSTEM', NULL),

    ('Isabela Pires', 'isabela.pires@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990010', 'Av. Cristiano Machado, 1000 - Belo Horizonte - MG', NULL, NULL, '2025-01-19T18:30:00Z', NULL, 'SYSTEM', NULL);


INSERT INTO tb_role (authority, created_at, updated_at, created_by, updated_by) VALUES
                                                                                    ('ROLE_ADMINISTRADOR', NOW(), NULL, 'SYSTEM', NULL),
                                                                                    ('ROLE_GERENTE', NOW(), NULL, 'SYSTEM', NULL),
                                                                                    ('ROLE_ATENDENTE', NOW(), NULL, 'SYSTEM', NULL),
                                                                                    ('ROLE_FINANCEIRO', NOW(), NULL, 'SYSTEM', NULL),
                                                                                    ('ROLE_CLIENTE', NOW(), NULL, 'SYSTEM', NULL);


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
                                                 ('CUSTOMER_STATUS_CHANGE', 'CUSTOMERS'),
                                                 ('CUSTOMER_FILE_READ',     'CUSTOMERS'),
                                                 ('CUSTOMER_FILE_WRITE',     'CUSTOMERS'),
                                                 ('CUSTOMER_FILE_DELETE',     'CUSTOMERS'),

                                                 ('POSITION_READ',          'POSITIONS'),
                                                 ('POSITION_WRITE',         'POSITIONS'),
                                                 ('POSITION_DELETE',        'POSITIONS'),

                                                 ('DEPARTMENT_READ',          'DEPARTMENTS'),
                                                 ('DEPARTMENT_WRITE',         'DEPARTMENTS'),
                                                 ('DEPARTMENT_DELETE',        'DEPARTMENTS'),

                                                 ('EMPLOYEE_READ',          'EMPLOYEES'),
                                                 ('EMPLOYEE_WRITE',         'EMPLOYEES'),
                                                 ('EMPLOYEE_DELETE',        'EMPLOYEES'),
                                                 ('EMPLOYEE_STATUS_CHANGE', 'EMPLOYEES'),
                                                 ('EMPLOYEE_FILE_READ',     'EMPLOYEES'),
                                                 ('EMPLOYEE_FILE_WRITE',    'EMPLOYEES'),
                                                 ('EMPLOYEE_FILE_DELETE',   'EMPLOYEES'),

                                                 ('SUPPLIER_READ',          'SUPPLIERS'),
                                                 ('SUPPLIER_WRITE',         'SUPPLIERS'),
                                                 ('SUPPLIER_DELETE',        'SUPPLIERS'),
                                                 ('SUPPLIER_IMAGE_WRITE',   'SUPPLIERS'),
                                                 ('SUPPLIER_FILE_READ',     'SUPPLIERS'),
                                                 ('SUPPLIER_FILE_WRITE',    'SUPPLIERS'),
                                                 ('SUPPLIER_FILE_DELETE',   'SUPPLIERS'),

                                                 ('RECEIVABLE_READ',        'RECEIVABLES'),
                                                 ('RECEIVABLE_WRITE',       'RECEIVABLES'),
                                                 ('RECEIVABLE_DELETE',      'RECEIVABLES');







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
                                                            (1, 9),(1, 10),(1, 11), (1, 12),
                                                            (1, 13),(1, 14),(1, 15),(1, 16),
                                                            (1, 17),(1, 18),(1, 19), (1, 20),
                                                            (1, 21),(1, 22),(1, 23), (1, 24),
                                                            (1, 25),(1, 26),(1, 27), (1, 28),
                                                            (1, 29),(1, 30),(1, 31), (1, 32),
                                                            (1, 33),(1, 34),(1, 35), (1, 36),
                                                            (1, 37),(1, 38);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (2, 1),
                                                            (2, 5);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
    (4, 1),
    (4, 36),
    (4, 37);


INSERT INTO tb_customer (name, cpf, email, phone, address, active, photo_data, photo_content_type, created_at, updated_at, created_by, updated_by) VALUES
    ('João Silva', '11111111111', 'joao.silva@email.com', '31999990001', 'Rua A, 100 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Maria Oliveira', '22222222222', 'maria.oliveira@email.com', '31999990002', 'Rua B, 200 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Carlos Souza', '33333333333', 'carlos.souza@email.com', '31999990003', 'Rua C, 300 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Ana Costa', '44444444444', 'ana.costa@email.com', '31999990004', 'Rua D, 400 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Pedro Santos', '55555555555', 'pedro.santos@email.com', '31999990005', 'Rua E, 500 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Juliana Rocha', '66666666666', 'juliana.rocha@email.com', '31999990006', 'Rua F, 600 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Lucas Ferreira', '77777777777', 'lucas.ferreira@email.com', '31999990007', 'Rua G, 700 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Fernanda Alves', '88888888888', 'fernanda.alves@email.com', '31999990008', 'Rua H, 800 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Rafael Martins', '99999999999', 'rafael.martins@email.com', '31999990009', 'Rua I, 900 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Patricia Gomes', '10101010101', 'patricia.gomes@email.com', '31999990010', 'Rua J, 1000 - Belo Horizonte', true, NULL, NULL, NOW(), NULL, 'system', NULL);


INSERT INTO tb_position
(name, version, created_at, updated_at, created_by, updated_by)
VALUES
    ('Atendente', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Gerente', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Auxiliar Administrativo', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Assistente Financeiro', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Analista Financeiro', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Motorista', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Lavador de Veículos', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

    ('Supervisor de Operações', 0, NOW(), NOW(), 'SYSTEM', 'SYSTEM');


INSERT INTO tb_department
(name, description, created_at, updated_at, created_by, updated_by)
VALUES
    ('Financeiro', 'Responsável pelas finanças da empresa', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('RH', 'Gestão de pessoas e recrutamento', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('TI', 'Tecnologia da informação e sistemas', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Operações', 'Controle operacional da empresa', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Comercial', 'Área de vendas e atendimento ao cliente', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Marketing', 'Estratégias de marketing e publicidade', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Jurídico', 'Assuntos legais e contratos', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Logística', 'Controle de transporte e estoque', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Suporte', 'Atendimento e suporte técnico', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('Diretoria', 'Gestão estratégica da empresa', NOW(), NOW(), 'SYSTEM', 'SYSTEM');


INSERT INTO tb_employee (
    name, employee_code, email, phone, address, salary,
    hire_date, termination_date, employment_type, active,
    created_at, updated_at, created_by, updated_by,
    position_id, department_id
) VALUES
      ('João Silva', 'EMP001', 'joao@empresa.com', '31999990001', 'BH - MG', 5000.00,
       '2022-01-10', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 1, 1),

      ('Maria Souza', 'EMP002', 'maria@empresa.com', '31999990002', 'BH - MG', 4500.00,
       '2021-03-15', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 2, 2),

      ('Carlos Lima', 'EMP003', 'carlos@empresa.com', '31999990003', 'BH - MG', 7000.00,
       '2020-07-20', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 3, 3),

      ('Ana Paula', 'EMP004', 'ana@empresa.com', '31999990004', 'BH - MG', 6000.00,
       '2023-02-01', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 2, 1),

      ('Lucas Mendes', 'EMP005', 'lucas@empresa.com', '31999990005', 'BH - MG', 3500.00,
       '2022-05-10', NULL, 'PJ', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 4, 4),

      ('Fernanda Alves', 'EMP006', 'fernanda@empresa.com', '31999990006', 'BH - MG', 8000.00,
       '2019-11-25', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 1, 10),

      ('Bruno Rocha', 'EMP007', 'bruno@empresa.com', '31999990007', 'BH - MG', 4200.00,
       '2021-08-30', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 5, 5),

      ('Patrícia Gomes', 'EMP008', 'patricia@empresa.com', '31999990008', 'BH - MG', 3900.00,
       '2023-01-12', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 4, 6),

      ('Ricardo Dias', 'EMP009', 'ricardo@empresa.com', '31999990009', 'BH - MG', 5200.00,
       '2020-09-18', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 3, 7),

      ('Juliana Castro', 'EMP010', 'juliana@empresa.com', '31999990010', 'BH - MG', 6100.00,
       '2022-06-05', NULL, 'CLT', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 2, 8);

INSERT INTO tb_supplier (version, name, trade_name, company_name, cnpj, address, email, phone_number, image_data, image_content_type, created_at, updated_at, created_by, updated_by)
VALUES
    (0, 'Fornecedor de Veículos Premium', 'Premium Motors', 'Premium Motors LTDA', '12345678000101', 'Av. Amazonas, 1000 - Belo Horizonte/MG', '[contato@premiummotors.com.br](mailto:contato@premiummotors.com.br)', '3133331001', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Auto Peças Brasil', 'AP Brasil', 'Auto Peças Brasil LTDA', '12345678000102', 'Rua dos Andradas, 250 - Belo Horizonte/MG', '[vendas@apbrasil.com.br](mailto:vendas@apbrasil.com.br)', '3133331002', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Loca Pneus', 'Loca Pneus', 'Loca Pneus Comércio LTDA', '12345678000103', 'Av. Cristiano Machado, 4500 - Belo Horizonte/MG', '[contato@locapneus.com.br](mailto:contato@locapneus.com.br)', '3133331003', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Lubrificantes Gerais', 'Lub Gerais', 'Lubrificantes Gerais LTDA', '12345678000104', 'Rua Tupis, 800 - Belo Horizonte/MG', '[comercial@lubgerais.com.br](mailto:comercial@lubgerais.com.br)', '3133331004', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Baterias Energia', 'Energia Baterias', 'Energia Baterias S.A.', '12345678000105', 'Av. Antônio Carlos, 1500 - Belo Horizonte/MG', '[vendas@energiabaterias.com.br](mailto:vendas@energiabaterias.com.br)', '3133331005', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Oficina Mecânica Central', 'Mecânica Central', 'Oficina Mecânica Central LTDA', '12345678000106', 'Rua Espírito Santo, 900 - Belo Horizonte/MG', '[contato@mecanicacentral.com.br](mailto:contato@mecanicacentral.com.br)', '3133331006', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Vidros Automotivos BH', 'Vidros BH', 'Vidros Automotivos BH LTDA', '12345678000107', 'Av. Tereza Cristina, 2100 - Belo Horizonte/MG', '[atendimento@vidrosbh.com.br](mailto:atendimento@vidrosbh.com.br)', '3133331007', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Seguradora Protege', 'Protege Seguros', 'Protege Seguros S.A.', '12345678000108', 'Av. Afonso Pena, 3500 - Belo Horizonte/MG', '[contato@protegeseguros.com.br](mailto:contato@protegeseguros.com.br)', '3133331008', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Higienização Express', 'Express Clean', 'Express Clean Serviços LTDA', '12345678000109', 'Rua Goiás, 1200 - Belo Horizonte/MG', '[comercial@expressclean.com.br](mailto:comercial@expressclean.com.br)', '3133331009', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Tecnologia Veicular', 'Tech Car', 'Tech Car Tecnologia LTDA', '12345678000110', 'Av. Raja Gabaglia, 5000 - Belo Horizonte/MG', '[contato@techcar.com.br](mailto:contato@techcar.com.br)', '3133331010', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL);


INSERT INTO tb_payment_method
(name, fee, created_at, updated_at, created_by, updated_by)
VALUES
    ('Dinheiro', 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('PIX', 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Cartão de Débito', 1.49, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Cartão de Crédito', 3.49, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Transferência Bancária', 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Boleto Bancário', 2.99, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Carteira Digital', 2.49, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Crédito na Loja', 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Cheque', 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Outro', 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL);

INSERT INTO tb_payment_frequency
(frequency, days, created_at, updated_at, created_by, updated_by)
VALUES
    ('À vista', 0, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Diário', 1, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Semanal', 7, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Quinzenal', 14, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Mensal', 30, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Bimestral', 60, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Trimestral', 90, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Semestral', 180, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Anual', 365, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),

    ('Personalizado', 0, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL);


INSERT INTO tb_receivable
(description, customer_id, amount, due_date, payment_date, created_date, created_at, updated_at,
 payment_method_id, payment_frequency_id, note, file_name, reference, reference_id,
 late_fee, late_interest, discount, fee, subtotal, created_by, updated_by, paid_by, paid, remaining_balance)
VALUES
    ('Locação Acessório 01', 1, 45.90, '2026-07-05', '2026-07-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 1, 1, 'Pago antes do vencimento.', 'receipt_001.pdf', 'RENTAL', 1001, 0.00, 0.00, 5.00, 0.00, 40.90, 1, NULL, 2, TRUE, 0.00),

    ('Locação Game 01', 2, 59.90, '2026-07-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 2, 1, 'Aguardando pagamento.', NULL, 'RENTAL', 1002, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 59.90),

    ('Locação Game 02', 3, 99.90, '2026-07-15', '2026-07-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 3, 2, 'Pago com cartão de débito.', 'receipt_003.pdf', 'MEMBERSHIP', 1003, 0.00, 0.00, 0.00, 1.49, 101.39, 1, NULL, 2, TRUE, 0.00),

    ('Locação Game 03', 4, 35.00, '2026-07-08', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 1, 1, 'Cliente notificado.', NULL, 'LATE_FEE', 1004, 5.00, 1.50, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 35.00),

    ('Locação Acessório 02', 5, 25.00, '2026-07-12', '2026-07-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 4, 1, 'Pagamento com cartão de crédito.', 'receipt_005.pdf', 'RESERVATION', 1005, 0.00, 0.00, 2.00, 0.87, 23.87, 1, NULL, 2, TRUE, 0.00),

    ('Locação Console 01', 6, 18.50, '2026-07-20', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 5, 1, 'Transferência bancária pendente.', NULL, 'ACCESSORY', 1006, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 18.50),

    ('Locação Console 02', 7, 120.00, '2026-07-25', '2026-07-24', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 6, 3, 'Assinatura mensal.', 'receipt_007.pdf', 'SUBSCRIPTION', 1007, 0.00, 0.00, 10.00, 2.99, 112.99, 1, NULL, 2, TRUE, 0.00),

    ('Locação Acessório 03', 8, 150.00, '2026-07-30', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 7, 2, 'Pagamento por carteira digital pendente.', NULL, 'PACKAGE', 1008, 0.00, 0.00, 15.00, 3.00, 0.00, 1, NULL, NULL, FALSE, 150.00),

    ('Locação Game 04', 9, 80.00, '2026-08-02', '2026-08-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 8, 1, 'Crédito da loja utilizado.', 'receipt_009.pdf', 'REPLACEMENT', 1009, 0.00, 0.00, 20.00, 0.00, 60.00, 1, NULL, 2, TRUE, 0.00),

    ('Locação Game 05', 10, 65.00, '2026-08-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 9, 1, 'Aguardando confirmação.', NULL, 'SPECIAL_ORDER', 1010, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 65.00);
