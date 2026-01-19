CREATE TABLE IF NOT EXISTS lecture_schedule (
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



INSERT INTO lecture_schedule (lecture_id, week_day, start_time, end_time)
SELECT x.lecture_id,
       d.week_day,
       t.start_time,
       t.end_time
FROM (
  SELECT lecture_id
  FROM lecture
  ORDER BY lecture_id
  LIMIT 60
) x
JOIN (
  SELECT 'MON' AS week_day
  UNION ALL SELECT 'WED'
) d
JOIN (
  SELECT 0 AS slot, '09:00:00' AS start_time, '10:15:00' AS end_time
  UNION ALL SELECT 1, '10:30:00', '11:45:00'
  UNION ALL SELECT 2, '12:00:00', '13:15:00'
  UNION ALL SELECT 3, '13:30:00', '14:45:00'
  UNION ALL SELECT 4, '15:00:00', '16:15:00'
  UNION ALL SELECT 5, '16:30:00', '17:45:00'
  UNION ALL SELECT 6, '18:00:00', '19:15:00'
  UNION ALL SELECT 7, '19:30:00', '20:45:00'
) t
WHERE (x.lecture_id % 2) = 1
  AND t.slot = (x.lecture_id % 8);

INSERT INTO lecture_schedule (lecture_id, week_day, start_time, end_time)
SELECT x.lecture_id,
       d.week_day,
       t.start_time,
       t.end_time
FROM (
  SELECT lecture_id
  FROM lecture
  ORDER BY lecture_id
  LIMIT 60
) x
JOIN (
  SELECT 'TUE' AS week_day
  UNION ALL SELECT 'THU'
) d
JOIN (
  SELECT 0 AS slot, '09:00:00' AS start_time, '10:15:00' AS end_time
  UNION ALL SELECT 1, '10:30:00', '11:45:00'
  UNION ALL SELECT 2, '12:00:00', '13:15:00'
  UNION ALL SELECT 3, '13:30:00', '14:45:00'
  UNION ALL SELECT 4, '15:00:00', '16:15:00'
  UNION ALL SELECT 5, '16:30:00', '17:45:00'
  UNION ALL SELECT 6, '18:00:00', '19:15:00'
  UNION ALL SELECT 7, '19:30:00', '20:45:00'
) t
WHERE (x.lecture_id % 2) = 0
  AND t.slot = (x.lecture_id % 8);

  INSERT INTO lecture_schedule (lecture_id, week_day, start_time, end_time)
SELECT x.lecture_id,
       d.week_day,
       t.start_time,
       t.end_time
FROM (
  SELECT lecture_id
  FROM lecture
  ORDER BY lecture_id
  LIMIT 30 OFFSET 60
) x
JOIN (
  SELECT 0 AS d, 'MON' AS week_day
  UNION ALL SELECT 1, 'TUE'
  UNION ALL SELECT 2, 'WED'
  UNION ALL SELECT 3, 'THU'
  UNION ALL SELECT 4, 'FRI'
) d
  ON d.d = (x.lecture_id % 5)
JOIN (
  SELECT 0 AS slot, '09:00:00' AS start_time, '10:15:00' AS end_time
  UNION ALL SELECT 1, '10:30:00', '11:45:00'
  UNION ALL SELECT 2, '12:00:00', '13:15:00'
  UNION ALL SELECT 3, '13:30:00', '14:45:00'
  UNION ALL SELECT 4, '15:00:00', '16:15:00'
  UNION ALL SELECT 5, '16:30:00', '17:45:00'
  UNION ALL SELECT 6, '18:00:00', '19:15:00'
  UNION ALL SELECT 7, '19:30:00', '20:45:00'
) t
  ON t.slot = (x.lecture_id % 8);

  INSERT INTO lecture_schedule (lecture_id, week_day, start_time, end_time)
SELECT x.lecture_id,
       d.week_day,
       t.start_time,
       t.end_time
FROM (
  SELECT lecture_id
  FROM lecture
  ORDER BY lecture_id
  LIMIT 10 OFFSET 90
) x
JOIN (
  SELECT 0 AS d, 'MON' AS week_day
  UNION ALL SELECT 1, 'TUE'
  UNION ALL SELECT 2, 'WED'
  UNION ALL SELECT 3, 'THU'
  UNION ALL SELECT 4, 'FRI'
) d
  ON d.d = (x.lecture_id % 5)
JOIN (
  SELECT 0 AS slot, '13:00:00' AS start_time, '16:00:00' AS end_time
  UNION ALL SELECT 1, '18:00:00', '21:00:00'
) t
  ON t.slot = (x.lecture_id % 2);
