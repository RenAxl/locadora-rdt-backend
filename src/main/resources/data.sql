INSERT INTO tb_user
(name, email, password, active, telephone, street, number, complement, neighborhood, city, state, zip_code, photo_data, photo_content_type, created_at, updated_at, created_by, updated_by)
VALUES
    ('Renan Duarte', 'renandt30@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990001', 'Rua Carlos Pinto Coelho', '510', NULL, 'Vale do Jatobá', 'Belo Horizonte', 'MG', '30664-790', NULL, NULL, '2025-01-10T09:00:00Z', NULL, 'SYSTEM', NULL),

    ('Ana Silva', 'ana.silva@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990002', 'Rua Beta', '200', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-11T10:15:00Z', NULL, 'SYSTEM', NULL),

    ('Bruno Costa', 'bruno.costa@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990003', 'Av. Amazonas', '300', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-12T11:30:00Z', NULL, 'SYSTEM', NULL),

    ('Carla Mendes', 'carla.mendes@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990004', 'Rua Gama', '400', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-13T14:45:00Z', NULL, 'SYSTEM', NULL),

    ('Daniel Rocha', 'daniel.rocha@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990005', 'Rua Delta', '500', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-14T16:00:00Z', NULL, 'SYSTEM', NULL),

    ('Eduarda Lima', 'eduarda.lima@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', false, '31999990006', 'Av. Brasil', '600', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-15T09:20:00Z', NULL, 'SYSTEM', NULL),

    ('Felipe Nogueira', 'felipe.nogueira@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990007', 'Rua Goiás', '700', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-16T13:10:00Z', NULL, 'SYSTEM', NULL),

    ('Gabriela Torres', 'gabriela.torres@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990008', 'Av. Contorno', '800', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-17T15:35:00Z', NULL, 'SYSTEM', NULL),

    ('Henrique Alves', 'henrique.alves@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', false, '31999990009', 'Rua da Bahia', '900', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-18T17:50:00Z', NULL, 'SYSTEM', NULL),

    ('Isabela Pires', 'isabela.pires@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', true,  '31999990010', 'Av. Cristiano Machado', '1000', NULL, NULL, 'Belo Horizonte', 'MG', NULL, NULL, NULL, '2025-01-19T18:30:00Z', NULL, 'SYSTEM', NULL);


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
                                                 ('RECEIVABLE_DELETE',      'RECEIVABLES'),

                                                 ('RENTAL_PRICE_CHANGE',   'RENTALS');







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
                                                            (1, 37),(1, 38),(1, 39);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
                                                            (2, 1),
                                                            (2, 5);


INSERT INTO tb_role_permission (role_id, permission_id) VALUES
    (4, 1),
    (4, 36),
    (4, 37);


INSERT INTO tb_customer (name, cpf, email, phone, street, number, complement, neighborhood, city, state, zip_code, active, photo_data, photo_content_type, created_at, updated_at, created_by, updated_by) VALUES
    ('Renan Duarte', '11111111111', 'renandt30@gmail.com', '31999990001', 'Rua A', '100', NULL, 'Centro', 'Belo Horizonte', 'MG', '30100-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Maria Oliveira', '22222222222', 'maria.oliveira@email.com', '31999990002', 'Rua B', '200', NULL, 'Centro', 'Belo Horizonte', 'MG', '30110-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Carlos Souza', '33333333333', 'carlos.souza@email.com', '31999990003', 'Rua C', '300', NULL, 'Centro', 'Belo Horizonte', 'MG', '30120-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Ana Costa', '44444444444', 'ana.costa@email.com', '31999990004', 'Rua D', '400', NULL, 'Centro', 'Belo Horizonte', 'MG', '30130-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Pedro Santos', '55555555555', 'pedro.santos@email.com', '31999990005', 'Rua E', '500', NULL, 'Centro', 'Belo Horizonte', 'MG', '30140-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Juliana Rocha', '66666666666', 'juliana.rocha@email.com', '31999990006', 'Rua F', '600', NULL, 'Centro', 'Belo Horizonte', 'MG', '30150-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Lucas Ferreira', '77777777777', 'lucas.ferreira@email.com', '31999990007', 'Rua G', '700', NULL, 'Centro', 'Belo Horizonte', 'MG', '30160-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Fernanda Alves', '88888888888', 'fernanda.alves@email.com', '31999990008', 'Rua H', '800', NULL, 'Centro', 'Belo Horizonte', 'MG', '30170-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Rafael Martins', '99999999999', 'rafael.martins@email.com', '31999990009', 'Rua I', '900', NULL, 'Centro', 'Belo Horizonte', 'MG', '30180-000', true, NULL, NULL, NOW(), NULL, 'system', NULL),
    ('Patricia Gomes', '10101010101', 'patricia.gomes@email.com', '31999990010', 'Rua J', '1000', NULL, 'Centro', 'Belo Horizonte', 'MG', '30190-000', true, NULL, NULL, NOW(), NULL, 'system', NULL);


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

INSERT INTO tb_supplier (version, name, trade_name, company_name, cnpj, street, number, complement, neighborhood, city, state, zip_code, email, phone_number, image_data, image_content_type, created_at, updated_at, created_by, updated_by)
VALUES
    (0, 'Fornecedor de Veículos Premium', 'Premium Motors', 'Premium Motors LTDA', '12345678000101', 'Av. Amazonas', '1000', NULL, 'Centro', 'Belo Horizonte', 'MG', '30100-000', '[contato@premiummotors.com.br](mailto:contato@premiummotors.com.br)', '3133331001', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Auto Peças Brasil', 'AP Brasil', 'Auto Peças Brasil LTDA', '12345678000102', 'Rua dos Andradas', '250', NULL, 'Centro', 'Belo Horizonte', 'MG', '30120-000', '[vendas@apbrasil.com.br](mailto:vendas@apbrasil.com.br)', '3133331002', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Loca Pneus', 'Loca Pneus', 'Loca Pneus Comércio LTDA', '12345678000103', 'Av. Cristiano Machado', '4500', NULL, 'União', 'Belo Horizonte', 'MG', '31160-000', '[contato@locapneus.com.br](mailto:contato@locapneus.com.br)', '3133331003', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Lubrificantes Gerais', 'Lub Gerais', 'Lubrificantes Gerais LTDA', '12345678000104', 'Rua Tupis', '800', NULL, 'Centro', 'Belo Horizonte', 'MG', '30190-000', '[comercial@lubgerais.com.br](mailto:comercial@lubgerais.com.br)', '3133331004', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Baterias Energia', 'Energia Baterias', 'Energia Baterias S.A.', '12345678000105', 'Av. Antônio Carlos', '1500', NULL, 'Cachoeirinha', 'Belo Horizonte', 'MG', '31210-000', '[vendas@energiabaterias.com.br](mailto:vendas@energiabaterias.com.br)', '3133331005', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Oficina Mecânica Central', 'Mecânica Central', 'Oficina Mecânica Central LTDA', '12345678000106', 'Rua Espírito Santo', '900', NULL, 'Centro', 'Belo Horizonte', 'MG', '30160-000', '[contato@mecanicacentral.com.br](mailto:contato@mecanicacentral.com.br)', '3133331006', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Vidros Automotivos BH', 'Vidros BH', 'Vidros Automotivos BH LTDA', '12345678000107', 'Av. Tereza Cristina', '2100', NULL, 'Calafate', 'Belo Horizonte', 'MG', '30411-000', '[atendimento@vidrosbh.com.br](mailto:atendimento@vidrosbh.com.br)', '3133331007', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Seguradora Protege', 'Protege Seguros', 'Protege Seguros S.A.', '12345678000108', 'Av. Afonso Pena', '3500', NULL, 'Funcionários', 'Belo Horizonte', 'MG', '30130-000', '[contato@protegeseguros.com.br](mailto:contato@protegeseguros.com.br)', '3133331008', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Higienização Express', 'Express Clean', 'Express Clean Serviços LTDA', '12345678000109', 'Rua Goiás', '1200', NULL, 'Savassi', 'Belo Horizonte', 'MG', '30190-000', '[comercial@expressclean.com.br](mailto:comercial@expressclean.com.br)', '3133331009', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL),
    (0, 'Tecnologia Veicular', 'Tech Car', 'Tech Car Tecnologia LTDA', '12345678000110', 'Av. Raja Gabaglia', '5000', NULL, 'Santa Lúcia', 'Belo Horizonte', 'MG', '30360-000', '[contato@techcar.com.br](mailto:contato@techcar.com.br)', '3133331010', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL);


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

INSERT INTO tb_financial_setting
(singleton_key, default_late_fee_percent, default_late_interest_percent, created_at, updated_at, created_by, updated_by)
SELECT 'DEFAULT', 0.00, 0.00, CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM tb_financial_setting WHERE singleton_key = 'DEFAULT'
);


INSERT INTO tb_receivable
(description, customer_id, amount, due_date, payment_date, created_date, created_at, updated_at,
 payment_method_id, payment_frequency_id, note, file_name, status, reference, reference_id,
 late_fee, late_interest, discount, fee, subtotal, created_by, updated_by, paid_by, paid, remaining_balance)
VALUES
    ('Locação Acessório 01', 1, 45.90, '2026-07-05', '2026-07-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 1, 1, 'Pago antes do vencimento.', 'receipt_001.pdf', 'PAID', 'RENTAL', 1001, 0.00, 0.00, 5.00, 0.00, 40.90, 1, NULL, 2, TRUE, 0.00),

    ('Locação Game 01', 2, 59.90, '2026-07-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 2, 1, 'Aguardando pagamento.', NULL, 'PENDING', 'RENTAL', 1002, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 59.90),

    ('Locação Game 02', 3, 99.90, '2026-07-15', '2026-07-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 3, 2, 'Pago com cartão de débito.', 'receipt_003.pdf', 'PAID', 'MEMBERSHIP', 1003, 0.00, 0.00, 0.00, 1.49, 101.39, 1, NULL, 2, TRUE, 0.00),

    ('Locação Game 03', 4, 35.00, '2026-07-08', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 1, 1, 'Cliente notificado.', NULL, 'PENDING', 'LATE_FEE', 1004, 5.00, 1.50, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 35.00),

    ('Locação Acessório 02', 5, 25.00, '2026-07-12', '2026-07-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 4, 1, 'Pagamento com cartão de crédito.', 'receipt_005.pdf', 'PAID', 'RESERVATION', 1005, 0.00, 0.00, 2.00, 0.87, 23.87, 1, NULL, 2, TRUE, 0.00),

    ('Locação Console 01', 6, 18.50, '2026-07-20', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 5, 1, 'Transferência bancária pendente.', NULL, 'PENDING', 'ACCESSORY', 1006, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 18.50),

    ('Locação Console 02', 7, 120.00, '2026-07-25', '2026-07-24', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 6, 3, 'Assinatura mensal.', 'receipt_007.pdf', 'PAID', 'SUBSCRIPTION', 1007, 0.00, 0.00, 10.00, 2.99, 112.99, 1, NULL, 2, TRUE, 0.00),

    ('Locação Acessório 03', 8, 150.00, '2026-07-30', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 7, 2, 'Pagamento por carteira digital pendente.', NULL, 'PENDING', 'PACKAGE', 1008, 0.00, 0.00, 15.00, 3.00, 0.00, 1, NULL, NULL, FALSE, 150.00),

    ('Locação Game 04', 9, 80.00, '2026-08-02', '2026-08-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 8, 1, 'Crédito da loja utilizado.', 'receipt_009.pdf', 'PAID', 'REPLACEMENT', 1009, 0.00, 0.00, 20.00, 0.00, 60.00, 1, NULL, 2, TRUE, 0.00),

    ('Locação Game 05', 10, 65.00, '2026-08-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 9, 1, 'Aguardando confirmação.', NULL, 'PENDING', 'SPECIAL_ORDER', 1010, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, NULL, FALSE, 65.00);

INSERT INTO tb_payable (description, amount, due_date, payment_date, created_date, created_at, updated_at, note, file_name, status, reference, reference_id, late_fee, late_interest, discount, fee, subtotal, paid, remaining_balance, residual, canceled, supplier_id, employee_id, payment_method_id, payment_frequency_id, frequency_id, created_by, updated_by, paid_by, parent_payable_id) VALUES
                                                                                                                                                                                                                                                                                                                                                                                                     ('Compra de jogos para estoque', 850.00, '2026-07-15', '2026-07-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Pagamento antecipado.', 'nota_fiscal_001.pdf', 'PAID', 'PURCHASE', 1001, 0.00, 0.00, 20.00, 0.00, 830.00, TRUE, 0.00, FALSE, FALSE, 1, 1, 2, 1, 1, 1, NULL, 2, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Compra de acessórios', 420.00, '2026-07-20', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Aguardando vencimento.', NULL, 'PENDING', 'PURCHASE', 1002, 0.00, 0.00, 0.00, 0.00, 420.00, FALSE, 420.00, FALSE, FALSE, 2, 2, 1, 1, 1, 1, NULL, NULL, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Pagamento de energia elétrica', 315.80, '2026-07-08', '2026-07-08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Conta mensal.', 'energia_julho.pdf', 'PAID', 'UTILITY', 1003, 0.00, 0.00, 0.00, 0.00, 315.80, TRUE, 0.00, FALSE, FALSE, NULL, NULL, 5, 1, 1, 1, NULL, 1, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Pagamento de internet', 149.90, '2026-07-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Plano empresarial.', NULL, 'PENDING', 'UTILITY', 1004, 0.00, 0.00, 0.00, 0.00, 149.90, FALSE, 149.90, FALSE, FALSE, NULL, NULL, 2, 1, 1, 1, NULL, NULL, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Compra de controles', 1200.00, '2026-07-25', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Fornecedor principal.', NULL, 'PENDING', 'PURCHASE', 1005, 0.00, 0.00, 50.00, 0.00, 1150.00, FALSE, 1150.00, FALSE, FALSE, 3, 3, 4, 1, 1, 1, NULL, NULL, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Serviço de manutenção', 680.00, '2026-07-05', '2026-07-09', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Pago com atraso.', 'os_001.pdf', 'PAID', 'SERVICE', 1006, 15.00, 6.80, 0.00, 0.00, 701.80, TRUE, 0.00, FALSE, FALSE, 4, 2, 3, 1, 1, 1, NULL, 2, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Licença de software', 999.90, '2026-08-01', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Renovação anual.', 'licenca.pdf', 'PENDING', 'LICENSE', 1007, 0.00, 0.00, 0.00, 0.00, 999.90, FALSE, 999.90, FALSE, FALSE, 5, 1, 6, 2, 2, 1, NULL, NULL, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Compra de cadeiras', 1800.00, '2026-07-18', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Entrega agendada.', NULL, 'CANCELED', 'FURNITURE', 1008, 0.00, 0.00, 0.00, 0.00, 1800.00, FALSE, 1800.00, FALSE, TRUE, 6, 3, 2, 1, 1, 1, 2, NULL, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Compra de cabos HDMI', 275.50, '2026-07-22', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Pagamento parcial realizado.', NULL, 'PENDING', 'PURCHASE', 1009, 0.00, 0.00, 0.00, 0.00, 100.00, FALSE, 175.50, FALSE, FALSE, 2, 2, 1, 1, 1, 1, NULL, NULL, NULL),
                                                                                                                                                                                                                                                                                                                                                                                                     ('Compra de notebook', 4200.00, '2026-08-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Equipamento administrativo.', 'nf_notebook.pdf', 'PENDING', 'ASSET', 1010, 0.00, 0.00, 100.00, 0.00, 4100.00, FALSE, 4100.00, FALSE, FALSE, 7, 1, 4, 3, 3, 1, NULL, NULL, NULL);


INSERT INTO tb_category
(version, name, active, image_data, created_at, created_by)
VALUES
    (0, 'Consoles', true, NULL, NOW(), 'Administrador'),
    (0, 'Jogos PlayStation 5', true, NULL, NOW(), 'Administrador'),
    (0, 'Jogos PlayStation 4', true, NULL, NOW(), 'Administrador'),
    (0, 'Jogos Xbox Series X/S', true, NULL, NOW(), 'Administrador'),
    (0, 'Jogos Xbox One', true, NULL, NOW(), 'Administrador'),
    (0, 'Jogos Nintendo Switch', true, NULL, NOW(), 'Administrador'),
    (0, 'Controles', true, NULL, NOW(), 'Administrador'),
    (0, 'Headsets Gamer', true, NULL, NOW(), 'Administrador'),
    (0, 'Volantes e Simuladores', true, NULL, NOW(), 'Administrador'),
    (0, 'Acessórios para Games', true, NULL, NOW(), 'Administrador');

INSERT INTO tb_rental_type (
    version,
    name,
    active,
    type,
    days,
    created_at,
    updated_at,
    created_by,
    updated_by
) VALUES
      (0, 'Diária', TRUE, 'DAY', 1, '2026-07-15T10:00:00Z', NULL, 'admin', NULL),
      (0, 'Final de Semana', TRUE, 'WEEKEND', 3, '2026-07-15T10:00:00Z', NULL, 'admin', NULL),
      (0, 'Semanal', TRUE, 'WEEK', 7, '2026-07-15T10:00:00Z', NULL, 'admin', NULL),
      (0, 'Quinzenal', TRUE, 'FORTNIGHT', 15, '2026-07-15T10:00:00Z', NULL, 'admin', NULL),
      (0, 'Mensal', TRUE, 'MONTH', 30, '2026-07-15T10:00:00Z', NULL, 'admin', NULL);


INSERT INTO tb_item (version, name, description, category_id, price, image_data, active, created_at, created_by) VALUES
                                                                                                                     (0, 'PlayStation 5 Slim',          'Console PlayStation 5 Slim para locação.',             1, 150.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'Xbox Series X',               'Console Xbox Series X para locação.',                  1, 150.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'Nintendo Switch OLED',        'Console Nintendo Switch OLED para locação.',           1, 120.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'EA Sports FC 26 - PS5',       'Jogo EA Sports FC 26 para PlayStation 5.',             2,  20.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'God of War Ragnarök - PS5',   'Jogo God of War Ragnarök para PlayStation 5.',         2,  18.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'Halo Infinite - Xbox',        'Jogo Halo Infinite para Xbox Series X/S.',             4,  18.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'The Legend of Zelda TOTK',    'Jogo The Legend of Zelda TOTK para Nintendo Switch.',  6,  22.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'Controle DualSense',          'Controle DualSense para PlayStation 5.',               7,  15.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'Headset HyperX Cloud II',     'Headset HyperX Cloud II para experiência gamer.',      8,  18.00, NULL, true, NOW(), 'Administrador'),
                                                                                                                     (0, 'Volante Logitech G29',        'Volante Logitech G29 para simuladores de corrida.',    9,  80.00, NULL, true, NOW(), 'Administrador');

INSERT INTO tb_stock_balance (version, item_id, total_quantity, reserved_quantity, unavailable_quantity, minimum_quantity, created_at, created_by) VALUES
                                                                                                                                                       (0, 1,  5, 0, 0, 1, NOW(), 'Administrador'),
                                                                                                                                                       (0, 2,  3, 0, 0, 1, NOW(), 'Administrador'),
                                                                                                                                                       (0, 3,  4, 0, 0, 1, NOW(), 'Administrador'),
                                                                                                                                                       (0, 4, 10, 0, 0, 2, NOW(), 'Administrador'),
                                                                                                                                                       (0, 5,  8, 0, 0, 2, NOW(), 'Administrador'),
                                                                                                                                                       (0, 6,  6, 0, 0, 2, NOW(), 'Administrador'),
                                                                                                                                                       (0, 7,  7, 0, 0, 2, NOW(), 'Administrador'),
                                                                                                                                                       (0, 8, 12, 0, 0, 3, NOW(), 'Administrador'),
                                                                                                                                                       (0, 9,  8, 0, 0, 2, NOW(), 'Administrador'),
                                                                                                                                                       (0, 10, 2, 0, 0, 1, NOW(), 'Administrador');


INSERT INTO tb_rental (
    version,
    rental_number,
    customer_id,
    rental_type_id,
    status,
    rental_date,
    start_date,
    expected_return_date,
    actual_return_date,
    delivery_date,
    subtotal,
    discount,
    shipping_fee,
    additional_fee,
    late_fee,
    damage_fee,
    total_amount,
    down_payment,
    remaining_amount,
    payment_method_id,
    delivery_address,
    notes,
    contract_generated,
    whatsapp_sent,
    active,
    created_at,
    updated_at,
    created_by,
    updated_by
) VALUES
      (0,'LOC-20260001',1,1,'PENDING','2026-07-01T09:00:00Z','2026-07-05T08:00:00Z','2026-07-08T18:00:00Z',NULL,'2026-07-05T09:00:00Z',500.00,20.00,30.00,0.00,0.00,0.00,510.00,100.00,410.00,1,'Rua A, 100 - Belo Horizonte/MG','Primeira locação do cliente.',TRUE,TRUE,TRUE,'2026-07-01T09:00:00Z',NULL,'admin',NULL),

      (0,'LOC-20260002',2,2,'CONFIRMED','2026-07-02T10:00:00Z','2026-07-06T08:00:00Z','2026-07-09T18:00:00Z',NULL,NULL,750.00,50.00,40.00,10.00,0.00,0.00,750.00,250.00,500.00,2,'Av. Amazonas, 1500 - Belo Horizonte/MG','Retirada na loja.',TRUE,TRUE,TRUE,'2026-07-02T10:00:00Z',NULL,'admin',NULL),

      (0,'LOC-20260003',3,1,'RENTED','2026-07-03T11:00:00Z','2026-07-07T08:00:00Z','2026-07-10T18:00:00Z',NULL,'2026-07-07T09:00:00Z',1200.00,100.00,50.00,20.00,0.00,0.00,1170.00,300.00,870.00,3,'Rua das Flores, 45 - Contagem/MG','Entrega pela manhã.',TRUE,TRUE,TRUE,'2026-07-03T11:00:00Z',NULL,'admin',NULL),

      (0,'LOC-20260004',4,3,'RETURNED','2026-06-20T08:00:00Z','2026-06-21T08:00:00Z','2026-06-25T18:00:00Z','2026-06-25T17:00:00Z','2026-06-21T09:00:00Z',950.00,50.00,30.00,0.00,0.00,0.00,930.00,300.00,630.00,1,'Rua Goiás, 320 - Betim/MG','Locação encerrada sem ocorrências.',TRUE,TRUE,TRUE,'2026-06-20T08:00:00Z','2026-06-25T17:00:00Z','admin','admin'),

      (0,'LOC-20260005',5,2,'RETURNED','2026-06-10T09:00:00Z','2026-06-12T08:00:00Z','2026-06-15T18:00:00Z','2026-06-17T10:00:00Z','2026-06-12T09:00:00Z',800.00,0.00,20.00,0.00,50.00,0.00,870.00,200.00,670.00,2,'Rua Bahia, 800 - Belo Horizonte/MG','Devolução com atraso.',TRUE,TRUE,TRUE,'2026-06-10T09:00:00Z','2026-06-17T10:00:00Z','admin','admin'),

      (0,'LOC-20260006',6,1,'RETURNED','2026-06-05T09:00:00Z','2026-06-06T08:00:00Z','2026-06-09T18:00:00Z','2026-06-09T17:30:00Z',NULL,650.00,30.00,0.00,0.00,0.00,80.00,700.00,150.00,550.00,3,'Retirada na loja','Item retornou com pequeno dano.',TRUE,FALSE,TRUE,'2026-06-05T09:00:00Z','2026-06-09T17:30:00Z','admin','admin'),

      (0,'LOC-20260007',7,3,'CANCELLED','2026-07-04T08:30:00Z','2026-07-08T08:00:00Z','2026-07-12T18:00:00Z',NULL,NULL,900.00,100.00,20.00,0.00,0.00,0.00,820.00,0.00,820.00,NULL,'','Cliente cancelou antes da retirada.',FALSE,FALSE,TRUE,'2026-07-04T08:30:00Z','2026-07-04T12:00:00Z','admin','admin'),

      (0,'LOC-20260008',8,2,'RENTED','2026-07-06T14:00:00Z','2026-07-08T08:00:00Z','2026-07-11T18:00:00Z',NULL,'2026-07-08T09:00:00Z',1450.00,150.00,60.00,40.00,0.00,0.00,1400.00,500.00,900.00,4,'Av. João César, 1000 - Contagem/MG','Montagem incluída.',TRUE,TRUE,TRUE,'2026-07-06T14:00:00Z',NULL,'admin',NULL),

      (0,'LOC-20260009',9,1,'CONFIRMED','2026-07-07T15:30:00Z','2026-07-10T08:00:00Z','2026-07-13T18:00:00Z',NULL,NULL,550.00,0.00,25.00,15.00,0.00,0.00,590.00,90.00,500.00,1,'Rua Espírito Santo, 500 - Belo Horizonte/MG','Cliente confirmou horário.',TRUE,TRUE,TRUE,'2026-07-07T15:30:00Z',NULL,'admin',NULL),

      (0,'LOC-20260010',10,2,'PENDING','2026-07-08T16:00:00Z','2026-07-12T08:00:00Z','2026-07-15T18:00:00Z',NULL,NULL,2000.00,200.00,100.00,50.00,0.00,0.00,1950.00,500.00,1450.00,2,'Av. Cristiano Machado, 2000 - Belo Horizonte/MG','Cliente solicitou confirmação.',FALSE,FALSE,TRUE,'2026-07-08T16:00:00Z',NULL,'admin',NULL);


INSERT INTO tb_system_setting (singleton_key, company_name, street, number, complement, neighborhood, city, state, zip_code, created_at, updated_at, created_by, updated_by) VALUES
                                                                                                                                                                                 ('DEFAULT', 'Locadora RDT', 'Rua Edmon de Souza Melo', '33', 'Sala 501', 'Diamante', 'Belo Horizonte', 'MG', '30660-585', CURRENT_TIMESTAMP, NULL, 'SYSTEM', NULL);

INSERT INTO tb_item_unit (
    version, item_id, asset_code, serial_number, status, condition_status,
    purchase_date, notes, active, created_at, updated_at, created_by, updated_by
) VALUES
      (0, (SELECT id FROM tb_item WHERE name = 'PlayStation 5 Slim'), 'PS5-001', 'SN-PS5-2026-001', 'AVAILABLE', 'GOOD', '2026-01-10', 'Console em boas condições.', TRUE, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_item WHERE name = 'Xbox Series X'), 'XBOX-001', 'SN-XBOX-2026-001', 'RENTED', 'GOOD', '2026-01-15', 'Console entregue ao cliente.', TRUE, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_item WHERE name = 'Nintendo Switch OLED'), 'SWITCH-001', 'SN-SWITCH-2026-001', 'RENTED', 'GOOD', '2026-02-05', 'Acompanha base e carregador.', TRUE, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_item WHERE name = 'Controle DualSense'), 'DUALSENSE-001', 'SN-DUALSENSE-2026-001', 'AVAILABLE', 'NEW', '2026-03-20', 'Controle branco.', TRUE, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_item WHERE name = 'Volante Logitech G29'), 'G29-001', 'SN-G29-2026-001', 'MAINTENANCE', 'DAMAGED', '2026-04-12', 'Pedal do freio em manutenção.', TRUE, CURRENT_TIMESTAMP, NULL, 'admin', NULL);

INSERT INTO tb_rental_item (
    rental_id, item_id, quantity, unit_price, discount, additional_fee, subtotal
) VALUES
      ((SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260001'), (SELECT id FROM tb_item WHERE name = 'PlayStation 5 Slim'), 1, 150.00, 0.00, 0.00, 150.00),
      ((SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260002'), (SELECT id FROM tb_item WHERE name = 'Xbox Series X'), 1, 150.00, 0.00, 0.00, 150.00),
      ((SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260003'), (SELECT id FROM tb_item WHERE name = 'Nintendo Switch OLED'), 1, 120.00, 0.00, 0.00, 120.00),
      ((SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260004'), (SELECT id FROM tb_item WHERE name = 'Controle DualSense'), 1, 15.00, 0.00, 0.00, 15.00),
      ((SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260005'), (SELECT id FROM tb_item WHERE name = 'Volante Logitech G29'), 1, 80.00, 0.00, 0.00, 80.00);

INSERT INTO tb_rental_item_unit (
    version, rental_item_id, item_unit_id, status, delivery_condition,
    return_condition, reserved_at, delivered_at, returned_at,
    created_at, updated_at, created_by, updated_by
) VALUES
      (0, (SELECT ri.id FROM tb_rental_item ri JOIN tb_rental r ON r.id = ri.rental_id WHERE r.rental_number = 'LOC-20260001'), (SELECT id FROM tb_item_unit WHERE asset_code = 'PS5-001'), 'RESERVED', 'Console sem avarias.', NULL, '2026-07-01T10:00:00Z', NULL, NULL, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT ri.id FROM tb_rental_item ri JOIN tb_rental r ON r.id = ri.rental_id WHERE r.rental_number = 'LOC-20260002'), (SELECT id FROM tb_item_unit WHERE asset_code = 'XBOX-001'), 'DELIVERED', 'Console e cabos conferidos.', NULL, '2026-07-02T11:00:00Z', '2026-07-06T08:30:00Z', NULL, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT ri.id FROM tb_rental_item ri JOIN tb_rental r ON r.id = ri.rental_id WHERE r.rental_number = 'LOC-20260003'), (SELECT id FROM tb_item_unit WHERE asset_code = 'SWITCH-001'), 'DELIVERED', 'Equipamento entregue em boas condições.', NULL, '2026-07-03T12:00:00Z', '2026-07-07T08:30:00Z', NULL, CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT ri.id FROM tb_rental_item ri JOIN tb_rental r ON r.id = ri.rental_id WHERE r.rental_number = 'LOC-20260004'), (SELECT id FROM tb_item_unit WHERE asset_code = 'DUALSENSE-001'), 'RETURNED', 'Controle sem avarias.', 'Controle devolvido em boas condições.', '2026-06-20T09:00:00Z', '2026-06-21T08:30:00Z', '2026-06-25T17:00:00Z', CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT ri.id FROM tb_rental_item ri JOIN tb_rental r ON r.id = ri.rental_id WHERE r.rental_number = 'LOC-20260005'), (SELECT id FROM tb_item_unit WHERE asset_code = 'G29-001'), 'DAMAGED', 'Volante entregue funcionando.', 'Pedal do freio apresentou defeito.', '2026-06-10T10:00:00Z', '2026-06-12T08:30:00Z', '2026-06-17T10:00:00Z', CURRENT_TIMESTAMP, NULL, 'admin', NULL);

INSERT INTO tb_rental_status_history (
    version, rental_id, previous_status, new_status, reason, changed_at,
    changed_by, created_at, updated_at, created_by, updated_by
) VALUES
      (0, (SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260001'), NULL, 'PENDING', 'Locação criada e aguardando confirmação.', '2026-07-01T09:00:00Z', 'admin', CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260002'), 'PENDING', 'CONFIRMED', 'Pagamento confirmado pelo cliente.', '2026-07-02T12:00:00Z', 'admin', CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260003'), 'CONFIRMED', 'RENTED', 'Itens entregues ao cliente.', '2026-07-07T09:00:00Z', 'admin', CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260004'), 'RENTED', 'RETURNED', 'Itens devolvidos sem ocorrências.', '2026-06-25T17:00:00Z', 'admin', CURRENT_TIMESTAMP, NULL, 'admin', NULL),
      (0, (SELECT id FROM tb_rental WHERE rental_number = 'LOC-20260007'), 'PENDING', 'CANCELLED', 'Cancelamento solicitado pelo cliente.', '2026-07-04T12:00:00Z', 'admin', CURRENT_TIMESTAMP, NULL, 'admin', NULL);



