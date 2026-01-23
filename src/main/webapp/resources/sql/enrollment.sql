CREATE TABLE IF NOT EXISTS enrollment (
  enrollment_id  BIGINT NOT NULL AUTO_INCREMENT COMMENT '수강_식별자(PK)',

  lecture_id     BIGINT NOT NULL COMMENT '강의_식별자(FK lecture)',
  user_id        BIGINT NOT NULL COMMENT '수강생_식별자(FK user, role=STUDENT)',

  status         ENUM('ENROLLED','DROPPED') NOT NULL DEFAULT 'ENROLLED' COMMENT '수강상태',
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                 ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

  PRIMARY KEY (enrollment_id),

  UNIQUE KEY uq_enrollment_lecture_user (lecture_id, user_id),

  KEY idx_enrollment_user (user_id),
  KEY idx_enrollment_lecture (lecture_id),
  KEY idx_enrollment_status (status),

  CONSTRAINT fk_enrollment_lecture
    FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

  CONSTRAINT fk_enrollment_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='수강신청/수강(enrollment)';
  
  INSERT INTO enrollment (lecture_id, user_id, status)
VALUES
  (1, 3, 'ENROLLED'),
  (2, 3, 'ENROLLED'),
  (3, 3, 'ENROLLED'),
  (4, 3, 'DROPPED'),
  (5, 3, 'ENROLLED');
  
  UPDATE lecture
SET validation = 'CONFIRMED'
WHERE lecture_id IN (1,2,3,5);

UPDATE lecture
SET validation = 'CONFIRMED'
WHERE lecture_id BETWEEN 158 AND 160;
INSERT INTO enrollment (lecture_id, user_id, status)
VALUES
  (158, 3, 'ENROLLED'),
  (159, 3, 'ENROLLED'),
  (160, 3, 'ENROLLED')
ON DUPLICATE KEY UPDATE
  status = 'ENROLLED';
  

UPDATE lecture
SET validation = 'CONFIRMED'
WHERE lecture_id BETWEEN 158 AND 160;
INSERT INTO enrollment (lecture_id, user_id, status)
VALUES
  (158, 4, 'ENROLLED'),
  (159, 4, 'ENROLLED'),
  (160, 4, 'ENROLLED')
ON DUPLICATE KEY UPDATE
  status = 'ENROLLED';
  
  
  
UPDATE student
SET department_id=21
WHERE user_id in(3,4);