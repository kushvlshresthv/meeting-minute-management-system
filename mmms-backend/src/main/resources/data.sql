INSERT INTO app_users (username, password, email, firstname, lastname)
VALUES ('username', '{noop}password', 'username@gmail.com', 'admin', 'admin');


INSERT INTO members (
    first_name, last_name, institution, post, qualitifcation, email,
    created_by, created_date, modified_by, modified_date
) VALUES
      (
          'Anita', 'Shrestha', 'Tribhuvan University', 'Lecturer', 'MSc Physics', 'anita.shrestha@example.com',
          'admin', '2025-07-01', 'admin', '2025-07-01'
      ),
      (
          'Ramesh', 'Khadka', 'Kathmandu University', 'Professor', 'PhD Engineering', 'ramesh.khadka@example.com',
          'admin', '2025-07-02', 'admin', '2025-07-02'
      ),
      (
          'Sita', 'Thapa', 'Pokhara University', 'Coordinator', 'MBA', 'sita.thapa@example.com',
          'admin', '2025-07-03', 'admin', '2025-07-03'
      );