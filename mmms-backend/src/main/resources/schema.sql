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


CREATE TABLE app_meetings (
          meeting_id INT AUTO_INCREMENT PRIMARY KEY,
          meeting_name VARCHAR(255) NOT NULL,
          metting_description TEXT,
          meeting_held_date DATE NOT NULL,

          created_by VARCHAR(255) NOT NULL,
          updated_by VARCHAR(255) NOT NULL,

          created_date DATE NOT NULL,
          updated_date DATE NOT NULL
);


CREATE TABLE user_attended_meetings (
     uid INT,
     meeting_id INT
);

CREATE TABLE user_unattended_meetings (
     uid INT,
     meeting_id INT
)
