DELETE
from driver_entity
WHERE user_id = 1;
DELETE
from user_entity
WHERE id = 1;
INSERT INTO user_entity (id, activated, birth_date, email, first_name, is_veteran, last_name, password, phone, points,
                         email_option, phone_option, dark, user_role_id)
VALUES (1, true, current_date(), 'email@example.com', 'ezra', false, 'mitchell', 'password',
        'phone', 0, false, false, false, (SELECT (id) FROM user_role_entity WHERE role = 'driver'));

INSERT INTO driver_entity (car, user_id, checked_in)
VALUES ('blue car', 1, false);