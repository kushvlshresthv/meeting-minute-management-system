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

          created_by INT NOT NULL,
          updated_by INT NOT NULL,

          created_date DATE NOT NULL,
          updated_date DATE NOT NULL,

          CONSTRAINT fk_meeting_created_by FOREIGN KEY (created_by) REFERENCES app_users(uid),
          CONSTRAINT fk_meeting_updated_by FOREIGN KEY (updated_by) REFERENCES app_users(uid)
);
