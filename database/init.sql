SELECT 'CREATE DATABASE springfunko'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'funko');

drop table if exists funko;
drop sequence if exists funko_id_seq;
drop table if exists user_roles;
drop table if exists "USERS";
drop sequence if exists users_id_seq;
drop table if exists category;

create sequence categorias_id_seq increment 1 minvalue 1 maxvalue 2223372836854725607 start 5 cache 1;

CREATE TABLE "public"."category"
(
    id        bigint    DEFAULT NEXTVAL('categorias_id_seq') NOT NULL,
    name      CHARACTER VARYING(255)                         NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    is_deleted BOOLEAN   DEFAULT FALSE                        NOT NULL,
    CONSTRAINT "categorias_name_key" UNIQUE (name),
    CONSTRAINT "categorias_pkey" PRIMARY KEY (id)
) with (OIDS = FALSE);

INSERT INTO category (name, created_at, updated_at, is_deleted)
VALUES ('Categoria 1', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE),
       ('Categoria 2', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE),
       ('Categoria 3', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE),
       ('Categoria 4', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE),
       ('Categoria 5', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE);

create sequence funko_id_seq increment 1 minvalue 1 maxvalue 2223372836854725607 start 5 cache 1;

CREATE TABLE "public"."funko"
(
    id                  bigint    DEFAULT NEXTVAL('funko_id_seq') NOT NULL,
    nombre              VARCHAR(255)                              NOT NULL,
    precio              DECIMAL(10, 2)                            NOT NULL,
    cantidad            INT       DEFAULT 0,
    imagen              TEXT      DEFAULT 'imagen.png',
    fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    category_id         BIGINT                                    NOT NULL,
    CONSTRAINT funkos_pkey PRIMARY KEY (id),
    CONSTRAINT fk_categoria FOREIGN KEY (category_id) REFERENCES "category" (id)
) WITH (
      OIDS = FALSE
    );

INSERT INTO "funko" (nombre, precio, cantidad, imagen, fecha_creacion, fecha_actualizacion, category_id)
VALUES ('Funko 1', 10.0, 10, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6),
       ('Funko 2', 20.0, 20, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7),
       ('Funko 3', 30.0, 30, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 8),
       ('Funko 4', 40.0, 40, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6),
       ('Funko 5', 50.0, 50, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6);

CREATE TABLE "public"."user_roles"
(
    "user_id" bigint NOT NULL,
    "roles"   character varying(255)
) WITH (oids = false);

INSERT INTO "user_roles" ("user_id", "roles")
VALUES (1, 'USER'),
       (1, 'ADMIN'),
       (2, 'USER');

CREATE SEQUENCE users_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 3 CACHE 1;


CREATE TABLE "public"."users"
(
    id        BIGINT    DEFAULT NEXTVAL('users_id_seq') NOT NULL,
    name      CHARACTER VARYING(255)                    NOT NULL,
    surnames  CHARACTER VARYING(255)                    NOT NULL,
    username  CHARACTER VARYING(255)                    NOT NULL,
    email     CHARACTER VARYING(255)                    NOT NULL,
    password  CHARACTER VARYING(255)                    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    updateAt  TIMESTAMP DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    is_deleted BOOLEAN   DEFAULT FALSE                   NOT NULL,
    CONSTRAINT usuarios_pkey PRIMARY KEY (id)
) with (
      OIDS = FALSE
    );
-- adminPassword123
-- user1234
INSERT INTO "users" (id, name, surnames, username, email, password, created_at, updateAt, is_deleted)
VALUES (1, 'Admin', 'Admin', 'admin', 'admin@email.org', '$2a$12$QqfyMm21yjwpckdXpCFWgeQyCncwBzUiayvqRD.Gf7c8cv/UuQe0C',
        '2023-11-02 11:43:24.724871',
        '2023-11-02 11:43:24.724871', FALSE),
       (2, 'User', 'User', 'user', 'user@email.org', '$2a$12$8aoihlH0nLNeape3JU/AVOUtnjs8zlv0wEJQmYdYcutpbqdVljPZa',
        '2023-11-22 11:43:34.724871',
        '2023-11-12 12:43:24.724871', FALSE);

ALTER TABLE ONLY "public"."funko"
    ADD CONSTRAINT "fk2fwq10nwymfv7fumctxt9vpgb" FOREIGN KEY (category_id) REFERENCES "category" (id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "fk2chxp26bnpqjibydrikgq4t9e" FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE;