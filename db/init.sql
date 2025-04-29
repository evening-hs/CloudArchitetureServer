CREATE DATABASE IF NOT EXISTS restordb;
USE restordb;

CREATE TABLE usuario (
        username VARCHAR(16) PRIMARY KEY,
        password VARCHAR(32) NOT NULL
);

INSERT INTO usuario (username, password) VALUES
("admin", "admin"),
("evelyn", "hola"),
("antoine", "pepepicapapas"),
("dodani", "rocky"),
("santiago", "dzn"),
("emi", "12345"),
("uriel", "1234"),
("palmi", "5678"),
("eric", "6728"),
("noe", "1402"),
("alex", "3804");
