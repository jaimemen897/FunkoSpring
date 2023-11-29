INSERT into category (name, created_at, updated_at)
VALUES ('Serie', now(), now());
INSERT into category (name, created_at, updated_at)
VALUES ('Disney', now(), now());
INSERT into category (name, created_at, updated_at)
VALUES ('Superheroes', now(), now());
INSERT into category (name, created_at, updated_at)
VALUES ('Película', now(), now());
INSERT into category (name, created_at, updated_at)
VALUES ('Otros', now(), now());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Superman Funko Pop', 19.99, 50, 'https://localhost:8080/api/storage/superman.jpg', 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Batman Funko Pop', 15.99, 40, 'https://localhost:8080/api/storage/batman.jpg', 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Darth Vader Funko Pop', 21.99, 30, 'https://localhost:8080/api/storage/darth_vader.jpg', 5, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Harry Potter Funko Pop', 14.99, 60, 'https://localhost:8080/api/storage/harry_potter.jpg', 4, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Stranger Things Funko Pop', 18.99, 35, 'https://localhost:8080/api/storage/stranger_things.jpg', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Mickey Mouse Funko Pop', 12.99, 55, 'https://localhost:8080/api/storage/mickey_mouse.jpg', 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id, fecha_creacion, fecha_actualizacion)
VALUES ('Wonder Woman Funko Pop', 17.99, 45, 'https://localhost:8080/api/storage/wonder_woman.jpg', 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Contraseña: Admin1
INSERT INTO USERS (name, surnames, username, email, password)
VALUES ('admin', 'admin', 'admin', 'admin@gmail.com', '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2');
INSERT INTO USER_ROLES (user_id, roles)
VALUES (1, 'ADMIN');

-- Contraseña: User1
insert into USERS (name, surnames, username, email, password)
values ('User', 'User User', 'user', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.');
INSERT INTO USER_ROLES (user_id, roles)
VALUES (2, 'USER');

-- Contraseña: Test1
insert into USERS (name, surnames, username, email, password)
values ('Test', 'Test Test', 'test', 'test@prueba.net',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu');
INSERT INTO USER_ROLES (user_id, roles)
VALUES (3, 'USER');