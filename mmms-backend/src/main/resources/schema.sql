-- create database mmms_db;
-- use mmms_db;

CREATE TABLE app_users
(
    uid       INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(50),
    lastname  VARCHAR(50),
    username  VARCHAR(50) UNIQUE,
    email     VARCHAR(100),
    password  VARCHAR(100)
);

CREATE TABLE committees (
           committee_id INT AUTO_INCREMENT PRIMARY KEY,
           committee_name VARCHAR(255) NOT NULL,
           committee_description TEXT,
           created_by INT NOT NULL,
           created_date DATE NOT NULL,
           modified_by VARCHAR(255) NOT NULL,
           modified_date DATE NOT NULL,
           FOREIGN KEY (created_by) REFERENCES app_users(uid)
);


CREATE TABLE members (
                         member_id INT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(255) NOT NULL,
                         last_name VARCHAR(255) NOT NULL,
                         institution VARCHAR(255),
                         post VARCHAR(255),
                         qualification VARCHAR(255),
                         email VARCHAR(255),
                         created_by VARCHAR(255) NOT NULL,
                         created_date DATE NOT NULL,
                         modified_by VARCHAR(255) NOT NULL,
                         modified_date DATE NOT NULL
);

CREATE TABLE committee_memberships (
           committee_id INT NOT NULL,
           member_id INT NOT NULL,
           role VARCHAR(255) NOT NULL,
           PRIMARY KEY (committee_id, member_id),
           FOREIGN KEY (committee_id) REFERENCES committees(committee_id),
           FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE meetings (
          meeting_id INT AUTO_INCREMENT PRIMARY KEY,
          meeting_name VARCHAR(255) NOT NULL,
          meeting_description TEXT,
          meeting_held_date DATE NOT NULL,
          meeting_held_place VARCHAR(255) NOT NULL,
          created_by VARCHAR(255) NOT NULL,
          updated_by VARCHAR(255) NOT NULL,
          created_date DATE NOT NULL,
          updated_date DATE NOT NULL,

          committee_id INT NOT NULL,  -- Now NOT NULL
          FOREIGN KEY (committee_id) REFERENCES committees(committee_id)
);



CREATE TABLE meeting_attendees (
           member_id INT NOT NULL,
           meeting_id INT NOT NULL,
           PRIMARY KEY (member_id, meeting_id),
           FOREIGN KEY (member_id) REFERENCES members(member_id),
           FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id)
);


CREATE TABLE decisions (
        decision_id INT AUTO_INCREMENT PRIMARY KEY,
        meeting_id INT,
        decision TEXT,
        FOREIGN KEY (meeting_id) REFERENCES meetings(meeting_id)
);

