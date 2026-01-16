SELECT
    l.lecture_id    AS lectureId,
    l.lecture_title AS lectureTitle,
    l.section       AS section,
    s.week_day      AS weekDay,
    s.start_time    AS startTime,
    s.end_time      AS endTime,
    l.capacity      AS capacity,
    l.validation    AS validation,
    l.created_at    AS createdAt,
    u.name          AS instructorName
FROM
    lecture l
JOIN lecture_schedule s
    ON l.lecture_id = s.lecture_id
JOIN user u
    ON u.user_id = l.user_id
ORDER BY
    l.lecture_id,
    l.section;
    
    
    
    
    
    
    INSERT INTO user
(
  login_id,
  password_hash,
  name,
  birth_date,
  email,
  phone,
  role,
  status,
  gender,
  address,
  must_change_pw,
  created_at,
  updated_at
)
VALUES
(
  'instructor_lee',
  '$2a$10$abcdefghijklmnopqrstuv', -- 더미 해시
  '이준호',
  '1982-04-18',
  'leejh@example.com',
  '010-3456-7890',
  'INSTRUCTOR',
  'ACTIVE',
  'M',
  '서울특별시 마포구',
  0,
  NOW(),
  NOW()
),
(
  'instructor_kim',
  '$2a$10$zyxwvutsrqponmlkjihgf', -- 더미 해시
  '김서연',
  '1986-09-02',
  'kimsy@example.com',
  '010-9876-5432',
  'INSTRUCTOR',
  'ACTIVE',
  'F',
  '경기도 성남시 분당구',
  0,
  NOW(),
  NOW()
);

    
    
    
    SET @lee_id := (SELECT user_id FROM user WHERE login_id = 'instructor_lee' LIMIT 1);
SET @kim_id := (SELECT user_id FROM user WHERE login_id = 'instructor_kim' LIMIT 1);

    INSERT INTO lecture
(
  lecture_title, lecture_round, section, user_id,
  start_date, end_date, room, capacity,
  status, validation, created_at, updated_at
)
VALUES
-- ===== 이준호 (instructor_lee) =====
('객체지향 설계',        1, 'A', @lee_id, '2026-03-02', '2026-06-20', '301호', 30, 'PLANNED', 'CONFIRMED', NOW(), NOW()),
('Java 심화',           1, 'B', @lee_id, '2026-03-03', '2026-06-21', '302호', 28, 'PLANNED', 'CANCELED',  NOW(), NOW()),
('알고리즘 기초',       1, 'C', @lee_id, '2026-03-04', '2026-06-22', '303호', 35, 'PLANNED', 'CONFIRMED', NOW(), NOW()),

-- ===== 김서연 (instructor_kim) =====
('웹 프로그래밍',       1, 'A', @kim_id, '2026-03-05', '2026-06-23', '401호', 32, 'PLANNED', 'CONFIRMED', NOW(), NOW()),
('데이터베이스 실습',   1, 'B', @kim_id, '2026-03-06', '2026-06-24', '402호', 26, 'PLANNED', 'CANCELED',  NOW(), NOW()),
('네트워크 입문',       1, 'C', @kim_id, '2026-03-07', '2026-06-25', '403호', 40, 'PLANNED', 'CANCELED',  NOW(), NOW());

    INSERT INTO lecture_schedule (lecture_id, week_day, start_time, end_time, created_at)
SELECT l.lecture_id, x.week_day, x.start_time, x.end_time, NOW()
FROM lecture l
JOIN (
  -- 이준호
  SELECT @lee_id AS user_id, '객체지향 설계' AS lecture_title, 'A' AS section, 'MON' AS week_day, '09:00:00' AS start_time, '11:00:00' AS end_time
  UNION ALL SELECT @lee_id, '객체지향 설계', 'A', 'WED', '09:00:00', '11:00:00'
  UNION ALL SELECT @lee_id, 'Java 심화',      'B', 'TUE', '13:00:00', '15:00:00'
  UNION ALL SELECT @lee_id, '알고리즘 기초',  'C', 'FRI', '10:00:00', '12:00:00'

  -- 김서연
  UNION ALL SELECT @kim_id, '웹 프로그래밍',     'A', 'MON', '13:00:00', '15:00:00'
  UNION ALL SELECT @kim_id, '웹 프로그래밍',     'A', 'THU', '13:00:00', '15:00:00'
  UNION ALL SELECT @kim_id, '데이터베이스 실습', 'B', 'WED', '15:00:00', '17:00:00'
  UNION ALL SELECT @kim_id, '네트워크 입문',     'C', 'TUE', '10:00:00', '12:00:00'
) x
  ON l.user_id = x.user_id
 AND l.lecture_title = x.lecture_title
 AND l.section = x.section;

    
    