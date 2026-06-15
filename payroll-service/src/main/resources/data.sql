-- Departments
INSERT INTO department (id, address, department_name, mail, phone, description) VALUES
    (10, 'Main St 1',  'Engineering', 'eng@company.com',   '111-111', 'R&D and development'),
    (20, 'Park Ave 5', 'Sales',       'sales@company.com', '222-222', 'Customer acquisition'),
    (30, 'Side Rd 9',  'HR',          'hr@company.com',    '333-333', NULL);

-- Users (linked to departments via the user_department join table, many-to-many)
INSERT INTO users (id, first_name, last_name, user_name, password, description) VALUES
    (1, 'Anna',  'Kowalska', 'annak',  'pass1', 'Senior engineer'),
    (2, 'Bob',   'Smith',    'bobs',   'pass2', 'Sales rep'),
    (3, 'Carol', 'Nowak',    'caroln', 'pass3', 'HR specialist'),
    (4, 'Dawid', 'Lis',      'dawidl', 'pass4', 'Junior engineer');

-- User <-> Department (many-to-many)
INSERT INTO user_department (user_id, department_id) VALUES
    (1, 10),  -- Anna in Engineering
    (1, 20),  -- Anna also in Sales
    (2, 20),  -- Bob in Sales
    (3, 30),  -- Carol in HR
    (4, 10);  -- Dawid in Engineering

-- Salaries (spanning two years to test the queries)
-- 2024 salaries
INSERT INTO salary (id, bonus, date_of_salary, salary, user_id) VALUES
    (100, NULL,    DATE '2024-01-31', 5000.00, 1),
    (101, NULL,    DATE '2024-02-29', 5000.00, 1),
    (102, 1000.00, DATE '2024-12-31', 7000.00, 1),
    (103, NULL,    DATE '2024-06-30', 4500.00, 2),
    (104, NULL,    DATE '2024-12-31', 4500.00, 2),
    (105, NULL,    DATE '2024-03-31', 3800.00, 3),
    (106, NULL,    DATE '2024-11-30', 3800.00, 3);

-- 2025 salaries
INSERT INTO salary (id, bonus, date_of_salary, salary, user_id) VALUES
    (200, NULL,    DATE '2025-01-31', 5500.00, 1),
    (201, NULL,    DATE '2025-02-28', 5500.00, 1),
    (202, NULL,    DATE '2025-03-31', 5500.00, 1),
    (203, NULL,    DATE '2025-01-31', 4800.00, 2),
    (204, NULL,    DATE '2025-02-28', 4800.00, 2),
    (205, NULL,    DATE '2025-01-31', 4000.00, 3),
    (206, NULL,    DATE '2025-02-28', 4000.00, 3),
    (207, NULL,    DATE '2025-01-31', 3500.00, 4),
    (208, NULL,    DATE '2025-02-28', 3500.00, 4);

-- Advance identity counters past the explicit seed ids so generated ids (POST) never collide
ALTER TABLE department ALTER COLUMN id RESTART WITH 100;
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE salary ALTER COLUMN id RESTART WITH 1000;
