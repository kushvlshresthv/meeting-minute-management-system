-- create database mmms_db;
-- use mmms_db;

CREATE TABLE app_users (
       uid INT AUTO_INCREMENT PRIMARY KEY,
       firstname VARCHAR(50),
       lastname VARCHAR(50),
       username VARCHAR(50) UNIQUE,
       email VARCHAR(100),
       password VARCHAR(100)
);
