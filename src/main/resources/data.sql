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

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Superman Funko Pop', 19.99, 50, 'http://localhost:8080/api/storage/superman.jpg', 3);

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Batman Funko Pop', 15.99, 40, 'http://localhost:8080/api/storage/batman.jpg', 3);

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Darth Vader Funko Pop', 21.99, 30, 'http://localhost:8080/api/storage/darth_vader.jpg', 5);

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Harry Potter Funko Pop', 14.99, 60, 'http://localhost:8080/api/storage/harry_potter.jpg', 4);