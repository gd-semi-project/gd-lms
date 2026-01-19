CREATE TABLE IF NOT EXISTS college (
  college_id   BIGINT AUTO_INCREMENT PRIMARY KEY
    COMMENT '단과대학 PK',

  college_name VARCHAR(80) NOT NULL
    COMMENT '단과대학명',

  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '생성일시',

  CONSTRAINT uq_college_name
    UNIQUE (college_name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='단과대학 테이블';
  
INSERT INTO college (college_name) VALUES
('인문대학'),
('사회과학대학'),
('경영경제대학'),
('자연과학대학'),
('공과대학'),
('정보통신대학'),
('미디어콘텐츠대학'),
('예술체육대학'),
('사범대학'),
('융합학부');
