CREATE DATABASE IF NOT EXISTS lms;

USE lms;

CREATE TABLE IF NOT EXISTS instructor (
    user_id        BIGINT PRIMARY KEY COMMENT 'users.user_id (PK, FK)',

    instructor_no  VARCHAR(30) NOT NULL COMMENT '강사 교번',
    department     VARCHAR(100) NOT NULL COMMENT '소속 학과',
    office_room    VARCHAR(50) COMMENT '연구실',
    office_phone   VARCHAR(20) COMMENT '연구실 전화번호',
    hire_date      DATE COMMENT '임용일',

    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    CONSTRAINT uk_instructor_no UNIQUE (instructor_no),

    CONSTRAINT fk_instructor_user
        FOREIGN KEY (user_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='강사 정보 테이블';


TRUNCATE TABLE instructor;

INSERT INTO instructor (
    user_id,
    instructor_no,
    department,
    office_room,
    office_phone,
    hire_date
) VALUES
-- 김도윤 강사 (user_id = 1)
(
    1,
    'INST-2026-001',
    '컴퓨터공학과',
    'A-301',
    '02-123-4567',
    '2020-03-01'
),
-- 박서연 강사 (user_id = 2)
(
    2,
    'INST-2026-002',
    '소프트웨어학과',
    'B-204',
    '02-987-6543',
    '2021-09-01'
);