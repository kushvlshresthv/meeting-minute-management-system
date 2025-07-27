INSERT INTO app_users (username, password, email, firstname, lastname)
VALUES ('username', '{noop}password', 'username@gmail.com', 'admin', 'admin'),
       ('not_username', '{noop}password', 'username@gmail.com', 'admin', 'admin');


INSERT INTO members (
    first_name, last_name, institution, post, qualification, email,
    created_by, created_date, modified_by, modified_date, uuid
)
VALUES
    ('Hari',   'Bahadur',    'Nepal Engineering College',              'Assistant Professor',     'MSc IT',                  'hari.bahadur@example.com',   'username', '2025-07-04', 'username', '2025-07-04', UUID()),
    ('Gita',   'Oli',        'Ministry of Education',                  'Officer',                 'MPA',                     'gita.oli@example.com',        'username', '2025-07-05', 'username', '2025-07-05', UUID()),
    ('Bikash', 'Lama',       'Nist College',                           'HOD',                     'PhD Computer Science',    'bikash.lama@example.com',     'username', '2025-07-06', 'username', '2025-07-06', UUID()),
    ('Sunita', 'Maharjan',   'St. Xaviers College',                    'Lecturer',                'MSc Environmental Science','sunita.maharjan@example.com','username', '2025-07-07', 'username', '2025-07-07', UUID()),
    ('Kamal',  'Pandey',     'Pulchowk Campus',                        'Associate Professor',     'PhD Civil Engineering',   'kamal.pandey@example.com',    'username', '2025-07-12', 'username', '2025-07-12', UUID()),
    ('Deepa',  'Gurung',     'Patan College for Professional Studies', 'HR Manager',              'MBS',                     'deepa.gurung@example.com',    'username', '2025-07-13', 'username', '2025-07-13', UUID()),
    ('Nabin',  'Tamang',     'Softwarica College',                     'IT Head',                 'MSc Computer Science',    'nabin.tamang@example.com',    'username', '2025-07-14', 'username', '2025-07-14', UUID()),
    ('Anita',  'Shrestha',   'Tribhuvan University',                  'Assistant Lecturer',       'MA Sociology',           'anita.shrestha@example.com',  'username', '2025-07-15', 'username', '2025-07-15', UUID()),
    ('Ramesh', 'Karki',      'Nepal Telecom',                         'IT Officer',               'BSc CSIT',               'ramesh.karki@example.com',    'username', '2025-07-16', 'username', '2025-07-16', UUID()),
    ('Sita',   'Basnet',     'Kathmandu University',                  'Research Fellow',          'PhD Biotechnology',      'sita.basnet@example.com',     'username', '2025-07-17', 'username', '2025-07-17', UUID()),
    ('Prakash','Rana',       'Nepal Police Academy',                  'Training Coordinator',     'M.Ed',                   'prakash.rana@example.com',    'username', '2025-07-18', 'username', '2025-07-18', UUID()),
    ('Meena',  'Thapa',      'Nepal Rastra Bank',                     'Economist',                'MA Economics',           'meena.thapa@example.com',     'username', '2025-07-19', 'username', '2025-07-19', UUID()),
    ('Dipesh', 'KC',         'Kathford College',                      'HOD',                      'MSc IT',                 'dipesh.kc@example.com',       'username', '2025-07-20', 'username', '2025-07-20', UUID()),
    ('Sarita', 'Dhakal',     'Pokhara University',                    'Professor',                'PhD Management',         'sarita.dhakal@example.com',   'username', '2025-07-21', 'username', '2025-07-21', UUID()),
    ('Bijay',  'Gurung',     'Nepal Electricity Authority',           'Electrical Engineer',      'BE Electrical',          'bijay.gurung@example.com',    'username', '2025-07-22', 'username', '2025-07-22', UUID()),
    ('Rojina', 'Maharjan',   'Prime College',                         'Lecturer',                 'MSc CSIT',               'rojina.maharjan@example.com', 'username', '2025-07-23', 'username', '2025-07-23', UUID()),
    ('Suman',  'Bista',      'Everest Engineering College',           'Dean',                     'PhD Information Systems','suman.bista@example.com',    'username', '2025-07-24', 'username', '2025-07-24', UUID());

-- Insert Committees
INSERT INTO committees (
    committee_name,
    committee_description,
    created_by,
    created_date,
    modified_by,
    modified_date,
    uuid
)
VALUES
    ('Academic Committee',                 'Oversees academic policies and curriculum development.', 1, CURDATE(), 'username', CURDATE(), UUID()),
    ('Events Committee',                   'Plans and organizes all institutional events and seminars.', 1, CURDATE(), 'username', CURDATE(), UUID()),
    ('Research and Development Committee', 'Promotes research and innovation.', 1, CURDATE(), 'username', CURDATE(), UUID()),
    ('Disciplinary Committee',             'Handles student and staff disciplinary issues.', 1, CURDATE(), 'username', CURDATE(), UUID()),
    ('Student Welfare Committee',          'Addresses student concerns and well-being.', 1, CURDATE(), 'username', CURDATE(), UUID()),
    ('IT and Infrastructure Committee',    'Manages IT resources and campus infrastructure.', 2, CURDATE(), 'username', CURDATE(), UUID());

-- Insert Meetings
INSERT INTO meetings (
    committee_id, meeting_title, meeting_description,
    meeting_held_date, meeting_held_place, meeting_held_time,
    created_by, updated_by, created_date, updated_date, coordinator, uuid
)
VALUES
    -- meetings for committee 1
    (1, 'Syllabus Update Discussion',         'Discussing proposed updates to the engineering syllabus.',             '2025-07-18', 'Pulchowk Campus',       '14:30:00', 'username', 'username', '2025-07-13', '2025-07-13', 1, UUID()),
    (1, 'Annual Seminar Planning',            'Organizing the annual institutional seminar.',                        '2025-07-22', 'Auditorium',     '11:00:00', 'username', 'username', '2025-07-14', '2025-07-14',1, UUID()),
    (1, 'Research Grant Proposals Review',    'Assessment of new research funding requests.',                        '2025-07-25', 'Innovation Hub', '13:00:00', 'username', 'username', '2025-07-21', '2025-07-21', 1, UUID()),
    (1, 'Review of Recent Incidents',         'Addressing recent disciplinary cases and policy updates.',            '2025-07-28', 'Admin Office 1', '10:00:00', 'username', 'username', '2025-07-22', '2025-07-22', 1, UUID()),

    -- meetings for committee 2
    (2, 'Canteen and Hostel Feedback Session','Discussing feedback from students on facilities.',                    '2025-07-29', 'Student Lounge', '15:00:00', 'username', 'username', '2025-07-23', '2025-07-23', 9, UUID()),
    (2, 'Campus Wi-Fi Upgrade Plan',          'Finalizing the plan to upgrade network infrastructure.',              '2025-08-01', 'IT Department',  '11:00:00', 'username', 'username', '2025-07-25', '2025-07-25', 9, UUID());

-- Insert Decisions
INSERT INTO decisions (meeting_id, decision, uuid)
VALUES
    -- decisions for meeting 1
    (1, 'पाठ्यक्रममा नविकरणीय ऊर्जा सम्बन्धी नयाँ मोड्युल थपिनेछ।', UUID()),
    (1, 'हालको डाटा स्ट्रक्चर पाठ्यक्रमलाई नयाँ सामग्रीहरूसँग अद्यावधिक गरिनेछ।', UUID()),
    (1, 'आधुनिक इन्जिनियरिङ प्रवृत्तिहरूमा अतिथि व्याख्यान शृङ्खला आयोजना गरिनेछ।', UUID()),
    (1, 'अन्तिम वर्षको परियोजनाका लागि अन्तर-विभागीय प्रस्ताव स्वीकृत गरिएको छ।', UUID()),
    (1, 'सेमिनारको शीर्षक "प्रविधिमा नवीनता" रहनेछ।', UUID()),

    -- decisions for meeting 2
    (2, 'The proposal to upgrade campus-wide Wi-Fi to Wi-Fi 6 is approved.', UUID()),
    (2, 'Quotations from three different vendors are to be collected within two weeks.', UUID()),
    (2, 'The library computer systems will be upgraded with new SSDs and more RAM.', UUID()),
    (2, 'A new firewall will be implemented to enhance network security.', UUID()),


    -- decisions for meeting 3
    (3, 'Keynote speakers to be finalized by next week.', UUID()),
    (3, 'The event budget is finalized at NPR 300,000.', UUID()),
    (3, 'A student volunteer committee will be formed to manage logistics.', UUID()),

    -- decisions for meeting 4
    (4, 'Grant proposal from the Physics department for quantum computing research is approved.', UUID()),
    (4, 'A new policy for intellectual property rights for student projects will be drafted.', UUID()),
    (4, 'The budget for a new 3D printing lab is provisionally accepted; final quotes required.', UUID()),
    (4, 'Collaboration with Kathmandu University on AI research is to be initiated.', UUID()),

    -- decisions for meeting 5
    (5, 'Case #001: Student involved in plagiarism will receive a formal warning and must resubmit the assignment.', UUID()),
    (5, 'The code of conduct for examinations will be updated and circulated to all students.', UUID()),
    (5, 'A workshop on academic integrity will be made mandatory for all first-year students.', UUID()),
    (5, 'Case #002 regarding hostel rule violation is postponed pending more evidence.', UUID()),

    -- decisions for meeting 6
    (6, 'The contract with the current canteen vendor will be reviewed based on negative feedback.', UUID()),
    (6, 'A new water purification system will be installed in Hostel Block B.', UUID()),
    (6, 'Mental health counseling services will be extended to weekends during exam periods.', UUID()),
    (6, 'A survey will be conducted to assess the demand for a new sports facility.', UUID());

-- Insert Committee Memberships
INSERT INTO committee_memberships (committee_id, member_id, role, uuid)
VALUES
    -- Committee 1 (8 members)
    (1, 1, 'Chairperson', UUID()),
    (1, 2, 'Member', UUID()),
    (1, 3, 'Treasurer', UUID()),
    (1, 4, 'Member', UUID()),
    (1, 5, 'Member', UUID()),
    (1, 6, 'Member', UUID()),
    (1, 7, 'Member', UUID()),
    (1, 8, 'Secretary', UUID()),

    -- Committee 2 (5 members)
    (2, 9,  'Chairperson', UUID()),
    (2, 10, 'Member', UUID()),
    (2, 11, 'Coordinator', UUID()),
    (2, 12, 'Member', UUID()),
    (2, 13, 'Member', UUID()),

    -- Committee 3 (3 members)
    (3, 14, 'Chairperson', UUID()),
    (3, 15, 'Member', UUID()),
    (3, 16, 'Member', UUID()),

    -- Committee 4 (1 member)
    (4, 17, 'Chairperson', UUID());


INSERT INTO meeting_attendees (member_id, meeting_id)
VALUES
    -- Meeting 1 attendees. meeting 1 belongs to committee 1, so only members belonging to committee 1 should be present

    -- Furthermore while populatig the meeting_attendees, make sure that the meeting coordinator is part of the meeting_attendee
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 1),

    -- Meeting 2 attendees
    (3, 2),
    (1, 2),
    (4, 2),
    (5, 2),
    (7, 2),

    -- Meeting 3 attendees
    (5, 3),
    (8, 3),
    (1, 3),
    (2, 3),

    -- Meeting 4 attendees
    (1, 4),
    (2, 4),
    (7, 4),
    (8, 4),

    -- Meeting 5 attendees
    (9, 5),
    (10, 5),
    (11, 5),
    (12, 5),

    -- Meeting 6 attendees
    (10, 6),
    (12, 6),
    (9, 6),
    (11, 6);