CREATE TABLE IF NOT EXISTS instructor (
    user_id        BIGINT PRIMARY KEY COMMENT 'users.user_id (PK, FK)',

    instructor_no  VARCHAR(30) NOT NULL COMMENT '강사 교번',
    department_id  BIGINT NOT NULL COMMENT '소속 학과',
    office_room    VARCHAR(50) COMMENT '연구실',
    office_phone   VARCHAR(20) COMMENT '연구실 전화번호',
    hire_date      DATE COMMENT '임용일',

    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    CONSTRAINT uk_instructor_no UNIQUE (instructor_no),
	CONSTRAINT fk_department_id
		FOREIGN KEY (department_id)
		REFERENCES department(department_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_instructor_user
        FOREIGN KEY (user_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='강사 정보 테이블';

INSERT INTO instructor
(
  user_id,
  instructor_no,
  department_id,
  office_room,
  office_phone,
  hire_date
)
SELECT
  u.user_id,
  CONCAT('INS-', 2016 + (u.user_id % 8), '-', LPAD(u.user_id, 4, '0')) AS instructor_no,
  d.department_id,
  CONCAT(
    'G-',
    LPAD(2 + (u.user_id % 9), 2, '0'),
    '-',
    LPAD(401 + (u.user_id % 20), 3, '0')
  ) AS office_room,
  CONCAT('02-6900-', LPAD(u.user_id, 4, '0')) AS office_phone,
  DATE_ADD('2016-03-01', INTERVAL (u.user_id % 8) YEAR) AS hire_date
FROM
  (SELECT user_id FROM user WHERE user_id BETWEEN 71 AND 100 ORDER BY user_id) u
JOIN
  (
    SELECT
      department_id,
      ROW_NUMBER() OVER (ORDER BY department_id) AS rn
    FROM department
  ) d
  ON d.rn = ((u.user_id - 71) % (SELECT COUNT(*) FROM department)) + 1;
  
  
INSERT INTO instructor (
    user_id,
    instructor_no,
    department_id,
    office_room,
    office_phone,
    hire_date
)
VALUES (
    2,
    'INST-0002',
    1,            
    '101호',
    '02-1234-5678',
    '2020-03-01'
);