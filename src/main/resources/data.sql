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

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Stranger Things Funko Pop', 18.99, 35, 'http://localhost:8080/api/storage/stranger_things.jpg', 1);

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Mickey Mouse Funko Pop', 12.99, 55, 'http://localhost:8080/api/storage/mickey_mouse.jpg', 2);

INSERT INTO funko (nombre, precio, cantidad, imagen, category_id)
VALUES ('Wonder Woman Funko Pop', 17.99, 45, 'http://localhost:8080/api/storage/wonder_woman.jpg', 3);
