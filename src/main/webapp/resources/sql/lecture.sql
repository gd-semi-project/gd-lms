create database lms;
use lms;
CREATE TABLE lecture (
    lecture_id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '강의 식별자',

    lecture_title   VARCHAR(200) NOT NULL COMMENT '강의명',
    lecture_round   INT NOT NULL COMMENT '강의 차수',

    user_id         BIGINT NOT NULL COMMENT '강사 식별자',

    start_date      DATE NOT NULL COMMENT '시작일',
    end_date        DATE NOT NULL COMMENT '종료일',

    room            VARCHAR(100) COMMENT '강의실',
    capacity        INT NOT NULL COMMENT '정원',

    status          ENUM('PLANNED','ONGOING','ENDED')
                    NOT NULL DEFAULT 'PLANNED'
                    COMMENT '진행 상태',

    validation      ENUM('CONFIRMED','PENDING','CANCELED')
                    NOT NULL DEFAULT 'PENDING'
                    COMMENT '승인 상태',

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
                    
	section 		CHAR(1) NOT NULL DEFAULT 'A' COMMENT '분반',

    CONSTRAINT fk_lecture_user
        FOREIGN KEY (user_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_capacity
        CHECK (capacity > 0),

    CONSTRAINT chk_date_range
        CHECK (start_date <= end_date)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='강의 정보 테이블';

SELECT
    l.lecture_id,
    u.name AS instructor,
    l.lecture_title,
    l.lecture_round,
    l.status,
    l.validation,
    l.section
FROM lecture l
JOIN user u ON l.user_id = u.user_id
ORDER BY u.user_id, l.lecture_id;


-- 더미용 데이터
TRUNCATE TABLE lec;

INSERT INTO lecture (
    lecture_title,
    lecture_round,
    user_id,
    start_date,
    end_date,
    room,
    capacity,
    status,
    validation,
    section
) VALUES
-- 김도윤 강사 (user_id = 1)
(
    '자바 프로그래밍 기초',
    1,
    1,
    '2026-03-02',
    '2026-04-13',
    'A-101',
    30,
    'PLANNED',
    'PENDING',
    
),
(
    '자바 프로그래밍 기초',
    2,
    1,
    '2026-05-04',
    '2026-06-15',
    'A-102',
    28,
    'PLANNED',
    'PENDING',
    'B'
),
(
    '객체지향 설계 심화',
    1,
    1,
    '2026-07-01',
    '2026-08-12',
    'A-201',
    25,
    'PLANNED',
    'PENDING'
),

-- 박서연 강사 (user_id = 2)
(
    '웹 개발 입문',
    1,
    2,
    '2026-03-10',
    '2026-04-21',
    'B-301',
    35,
    'PLANNED',
    'PENDING'
),
(
    'HTML/CSS 실습',
    1,
    2,
    '2026-05-12',
    '2026-06-23',
    'B-302',
    32,
    'PLANNED',
    'PENDING'
),
(
    'JavaScript 핵심',
    1,
    2,
    '2026-07-07',
    '2026-08-18',
    'B-401',
    30,
    'PLANNED',
    'PENDING'
),

-- 이준혁 강사 (user_id = 3)
(
    '데이터베이스 개론',
    1,
    3,
    '2026-03-05',
    '2026-04-16',
    'C-101',
    40,
    'PLANNED',
    'PENDING'
),
(
    'SQL 실전 활용',
    1,
    3,
    '2026-05-08',
    '2026-06-19',
    'C-102',
    38,
    'PLANNED',
    'PENDING'
),
(
    '성능 튜닝과 인덱스',
    1,
    3,
    '2026-07-03',
    '2026-08-14',
    'C-201',
    25,
    'PLANNED',
    'PENDING'
);
