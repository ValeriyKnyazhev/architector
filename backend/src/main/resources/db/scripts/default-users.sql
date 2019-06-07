INSERT INTO architector_roles (id, name)
VALUES
    (1, 'USER'),
    (2, 'ADMIN');

INSERT INTO architectors (id, email, password)
VALUES
    (1, 'user@architector.ru', '$2a$10$pII2FVcW/dPQmzfzHIssfOKjDdDoHKkuuKNvCjU/MEKAAMlL/GTSe'),
    (2, 'admin@architector.ru', '$2a$10$flQh3VxKBWioY4qY9ho5NeZuvIXVhNp0Iuo7ZyX4oA5awr7cfqurC');

INSERT INTO architector_role_relations (architector_id, role_id)
VALUES
    (1, 1),
    (2, 1),
    (2, 2);