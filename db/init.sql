CREATE DATABASE IF NOT EXISTS restordb;
USE restordb;

CREATE TABLE usuario (
        username VARCHAR(16) PRIMARY KEY,
        password VARCHAR(32) NOT NULL
);

INSERT INTO usuario (username, password) VALUES
("admin", "admin"),
("evelyn", "evelyn"),
("antoine", "antoine");

