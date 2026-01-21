-- 과제 테이블
CREATE TABLE assignment (
  assignment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  lecture_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  due_date DATETIME NOT NULL,
  max_score INT DEFAULT 100,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted ENUM('Y','N') NOT NULL DEFAULT 'N',
  FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id)
);

-- 과제 제출 테이블
CREATE TABLE assignment_submission (
  submission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  assignment_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  content TEXT,
  score INT,
  feedback TEXT,
  submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  graded_at DATETIME,
  FOREIGN KEY (assignment_id) REFERENCES assignment(assignment_id),
  FOREIGN KEY (student_id) REFERENCES `user`(user_id)
);
