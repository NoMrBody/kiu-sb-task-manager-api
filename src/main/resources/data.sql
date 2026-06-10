-- Seed data for the dev (H2) profile.
-- All passwords are the BCrypt hash of the literal "password".

INSERT INTO users (user_id, username, password, role, email) VALUES
    (1, 'admin', '$2a$10$b4oPGwd1dUc2zWS49KaHSeyRXesBAxHHVudO4e70aGoMnWQc/jtFC', 'ADMIN', 'admin@example.com'),
    (2, 'alice', '$2a$10$b4oPGwd1dUc2zWS49KaHSeyRXesBAxHHVudO4e70aGoMnWQc/jtFC', 'USER',  'alice@example.com'),
    (3, 'bob',   '$2a$10$b4oPGwd1dUc2zWS49KaHSeyRXesBAxHHVudO4e70aGoMnWQc/jtFC', 'USER',  'bob@example.com');

INSERT INTO tasks (task_id, title, description, task_status, due_date, user_id) VALUES
    (1, 'Set up project',        'Bootstrap the Spring Boot task manager API', 'DONE',        DATE '2026-06-01', 1),
    (2, 'Write documentation',   'Document profiles, i18n and logging',         'IN_PROGRESS', DATE '2026-06-15', 1),
    (3, 'Buy groceries',         'Milk, eggs and bread',                        'TODO',        DATE '2026-06-12', 2),
    (4, 'Finish lab assignment', 'Implement the remaining endpoints',           'IN_PROGRESS', DATE '2026-06-20', 2),
    (5, 'Plan vacation',         'Pick dates and book flights',                 'TODO',        DATE '2026-07-01', 3);

-- The explicit ids above bypass Hibernate's id generators, which still start at 1.
-- Advance them past the seeded rows so generated ids don't collide with the seed data.
ALTER SEQUENCE USERS_SEQ RESTART WITH 100;
ALTER TABLE tasks ALTER COLUMN task_id RESTART WITH 100;
