CREATE TABLE lecture_schedule (
    schedule_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '시간표 식별자',

    lecture_id      BIGINT NOT NULL COMMENT '강의 식별자',

    week_day        ENUM('MON','TUE','WED','THU','FRI','SAT','SUN')
                    NOT NULL COMMENT '요일',

    start_time      TIME NOT NULL COMMENT '시작 시간',
    end_time        TIME NOT NULL COMMENT '종료 시간',

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    COMMENT '생성일시',

    CONSTRAINT fk_schedule_lecture
        FOREIGN KEY (lecture_id)
        REFERENCES lecture(lecture_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_schedule_time
        CHECK (start_time < end_time)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='강의 시간표 테이블';


SELECT
  l.lecture_id,
  u.name AS instructor,
  l.lecture_title,
  l.lecture_round,
  s.week_day,
  s.start_time,
  s.end_time
FROM lecture_schedule s
JOIN lecture l ON s.lecture_id = l.lecture_id
JOIN user u ON l.user_id = u.user_id
ORDER BY l.user_id, l.lecture_id, FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN'), s.start_time;


-- 더미용 데이터
INSERT INTO lecture_schedule (lecture_id, week_day, start_time, end_time)
VALUES
-- 김도윤(user_id=1) - 자바 프로그래밍 기초 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='자바 프로그래밍 기초' AND lecture_round=1 AND user_id=1
   ORDER BY lecture_id DESC LIMIT 1),
  'MON', '09:00:00', '11:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='자바 프로그래밍 기초' AND lecture_round=1 AND user_id=1
   ORDER BY lecture_id DESC LIMIT 1),
  'WED', '09:00:00', '11:00:00'
),

-- 김도윤(user_id=1) - 자바 프로그래밍 기초 2차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='자바 프로그래밍 기초' AND lecture_round=2 AND user_id=1
   ORDER BY lecture_id DESC LIMIT 1),
  'TUE', '10:00:00', '12:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='자바 프로그래밍 기초' AND lecture_round=2 AND user_id=1
   ORDER BY lecture_id DESC LIMIT 1),
  'THU', '10:00:00', '12:00:00'
),

-- 김도윤(user_id=1) - 객체지향 설계 심화 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='객체지향 설계 심화' AND lecture_round=1 AND user_id=1
   ORDER BY lecture_id DESC LIMIT 1),
  'MON', '13:00:00', '15:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='객체지향 설계 심화' AND lecture_round=1 AND user_id=1
   ORDER BY lecture_id DESC LIMIT 1),
  'FRI', '13:00:00', '15:00:00'
),

-- 박서연(user_id=2) - 웹 개발 입문 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='웹 개발 입문' AND lecture_round=1 AND user_id=2
   ORDER BY lecture_id DESC LIMIT 1),
  'TUE', '09:00:00', '11:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='웹 개발 입문' AND lecture_round=1 AND user_id=2
   ORDER BY lecture_id DESC LIMIT 1),
  'THU', '09:00:00', '11:00:00'
),

-- 박서연(user_id=2) - HTML/CSS 실습 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='HTML/CSS 실습' AND lecture_round=1 AND user_id=2
   ORDER BY lecture_id DESC LIMIT 1),
  'MON', '11:00:00', '13:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='HTML/CSS 실습' AND lecture_round=1 AND user_id=2
   ORDER BY lecture_id DESC LIMIT 1),
  'WED', '11:00:00', '13:00:00'
),

-- 박서연(user_id=2) - JavaScript 핵심 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='JavaScript 핵심' AND lecture_round=1 AND user_id=2
   ORDER BY lecture_id DESC LIMIT 1),
  'TUE', '14:00:00', '16:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='JavaScript 핵심' AND lecture_round=1 AND user_id=2
   ORDER BY lecture_id DESC LIMIT 1),
  'FRI', '14:00:00', '16:00:00'
),

-- 이준혁(user_id=3) - 데이터베이스 개론 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='데이터베이스 개론' AND lecture_round=1 AND user_id=3
   ORDER BY lecture_id DESC LIMIT 1),
  'MON', '09:00:00', '11:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='데이터베이스 개론' AND lecture_round=1 AND user_id=3
   ORDER BY lecture_id DESC LIMIT 1),
  'WED', '09:00:00', '11:00:00'
),

-- 이준혁(user_id=3) - SQL 실전 활용 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='SQL 실전 활용' AND lecture_round=1 AND user_id=3
   ORDER BY lecture_id DESC LIMIT 1),
  'TUE', '11:00:00', '13:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='SQL 실전 활용' AND lecture_round=1 AND user_id=3
   ORDER BY lecture_id DESC LIMIT 1),
  'THU', '11:00:00', '13:00:00'
),

-- 이준혁(user_id=3) - 성능 튜닝과 인덱스 1차
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='성능 튜닝과 인덱스' AND lecture_round=1 AND user_id=3
   ORDER BY lecture_id DESC LIMIT 1),
  'WED', '14:00:00', '16:00:00'
),
(
  (SELECT lecture_id FROM lecture
   WHERE lecture_title='성능 튜닝과 인덱스' AND lecture_round=1 AND user_id=3
   ORDER BY lecture_id DESC LIMIT 1),
  'FRI', '14:00:00', '16:00:00'
);
