INSERT into category (name)
VALUES ('Serie');
INSERT into category (name)
VALUES ('Disney');
INSERT into category (name)
VALUES ('Superheroes');
INSERT into category (name)
VALUES ('Película');
INSERT into category (name)
VALUES ('Otros');

INSERT INTO funko (nombre, precio, cantidad, imagen, fecha_creacion, fecha_actualizacion, category_id)
VALUES ('Superman Funko Pop', 19.99, 50, 'superman.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3);

INSERT INTO funko (nombre, precio, cantidad, imagen, fecha_creacion, fecha_actualizacion, category_id)
VALUES ('Batman Funko Pop', 17.99, 40, 'batman.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3);

INSERT INTO funko (nombre, precio, cantidad, imagen, fecha_creacion, fecha_actualizacion, category_id)
VALUES ('Darth Vader Funko Pop', 21.99, 30, 'darth_vader.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5);

INSERT INTO funko (nombre, precio, cantidad, imagen, fecha_creacion, fecha_actualizacion, category_id)
VALUES ('Harry Potter Funko Pop', 14.99, 60, 'harry_potter.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,4);