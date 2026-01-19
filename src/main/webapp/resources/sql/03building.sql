CREATE TABLE building (
  building_id    BIGINT AUTO_INCREMENT PRIMARY KEY
    COMMENT '건물 PK',

  building_code  VARCHAR(10) NOT NULL
    COMMENT '건물 번호 (G-01)',

  building_name  VARCHAR(80) NOT NULL
    COMMENT '건물명',

  building_use   VARCHAR(120) NOT NULL
    COMMENT '주요 소속/용도',

  floors         INT NOT NULL
    COMMENT '총 층수',

  lecture_rooms  INT NOT NULL DEFAULT 0
    COMMENT '강의실 수',

  lab_rooms      INT NOT NULL DEFAULT 0
    COMMENT '실습실 수',

  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '생성일시',

  CONSTRAINT uq_building_code UNIQUE (building_code),
  CONSTRAINT ck_building_floors CHECK (floors >= 1),
  CONSTRAINT ck_building_counts CHECK (lecture_rooms >= 0 AND lab_rooms >= 0)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='캠퍼스 건물';

INSERT INTO building
(building_code, building_name, building_use, floors, lecture_rooms, lab_rooms)
VALUES
('G-01', '본관', '총장실, 교무처, 학사행정', 6, 6, 0),
('G-02', '인문사회관', '인문대학, 사회과학대학', 5, 28, 4),
('G-03', '경영경제관', '경영경제대학', 5, 22, 6),
('G-04', '자연과학관', '자연과학대학', 6, 14, 18),
('G-05', '공학관 A', '컴퓨터·소프트웨어·AI', 7, 12, 20),
('G-06', '공학관 B', '전자·기계·산업공학', 7, 10, 22),
('G-07', '정보통신관', '정보통신·보안·데이터', 6, 10, 16),
('G-08', '미디어콘텐츠관', '미디어·게임·영상', 5, 12, 14),
('G-09', '예술체육관', '예술·체육대학', 4, 6, 10),
('G-10', '사범관', '사범대학', 4, 14, 6),
('G-11', '융합관', '융합공학부, 자유전공', 5, 16, 8),
('G-12', '중앙도서관', '학습·열람', 7, 6, 0),
('G-13', '학생회관', '학생지원, 동아리', 4, 4, 0),
('G-14', '산학협력관', '산학, 스타트업', 6, 6, 10);
