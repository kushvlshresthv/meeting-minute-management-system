INSERT INTO app_users (username, password, email, firstname, lastname)
VALUES ('username', '{noop}password', 'username@gmail.com', 'admin', 'admin');


INSERT INTO members (
    first_name, last_name, institution, post, qualification, email,
    created_by, created_date, modified_by, modified_date
) VALUES
      (
          'Anita', 'Shrestha', 'Tribhuvan University', 'Lecturer', 'MSc Physics', 'anita.shrestha@example.com',
          'username', '2025-07-01', 'username', '2025-07-01'
      ),
      (
          'Ramesh', 'Khadka', 'Kathmandu University', 'Professor', 'PhD Engineering', 'ramesh.khadka@example.com',
          'username', '2025-07-02', 'username', '2025-07-02'
      ),
      (
          'Sita', 'Thapa', 'Pokhara University', 'Coordinator', 'MBA', 'sita.thapa@example.com',
          'username', '2025-07-03', 'username', '2025-07-03'
      ),
      (
          'Mel', 'Sitara', 'Pokhara University', 'Coordinator', 'MBA', 'sita.thapa@example.com',
          'username', '2025-07-03', 'username', '2025-07-03'
      )
;


INSERT INTO committees (
    committee_name,
    committee_description,
    created_by,
    created_date,
    modified_by,
    modified_date
) VALUES (
             'Finance Committee',
             'Handles all financial matters of the organization',
             1,
             CURDATE(),
             'username',
             CURDATE()
         );

-- get the id of the last inserted entity
SET @committeeId = LAST_INSERT_ID();

-- Insert a new meeting linked to the newly inserted committee
INSERT INTO meetings (
    meeting_title,
    meeting_description,
    meeting_held_date,
    meeting_held_place,
    meeting_held_time,
    created_by,
    updated_by,
    created_date,
    updated_date,
    committee_id
) VALUES (
             'Budget Planning Meeting',
             'Meeting to discuss the budget allocation for the next fiscal year',
             '2025-07-08',
             'Conference Hall A',
            '10:30:00',
             'username',
             'username',
             CURDATE(),
             CURDATE(),
             @committeeId
         );

INSERT INTO decisions (
    meeting_id,
    decision
) VALUES
      (1, 'Approved FY2025 budget.'),
      (1, 'Requested audit report by August.');


-- Now insert into committee_memberships
-- Using committee_id = 1 and the newly inserted member_id = 1
INSERT INTO committee_memberships (
    committee_id,
    member_id,
    role
) VALUES (
             1,
             1,
             'Chairperson'
         );