DELETE FROM user_entity;
DELETE FROM user_role_entity WHERE role = 'user';
INSERT INTO user_role_entity (id, role)
VALUES (1, 'user');
INSERT INTO user_entity
(id, activated, birth_date, email, first_name, is_veteran, last_name, password, phone, points, email_option,
 phone_option, dark, user_role_id)
VALUES
(1, true, CURRENT_DATE(), 'email', 'first',false,  'last', 'password', 'phone', 0, false, false, false, 1);