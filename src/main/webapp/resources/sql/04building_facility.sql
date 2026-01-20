CREATE TABLE building_facility (
  facility_id   BIGINT AUTO_INCREMENT PRIMARY KEY
    COMMENT '시설 PK',

  building_id   BIGINT NOT NULL
    COMMENT '소속 건물 ID',

  facility_name VARCHAR(80) NOT NULL
    COMMENT '시설명 (예: 회의실, 대강당, 서버실)',

  quantity      INT NULL
    COMMENT '시설 개수 (정원 아님, 수량)',

  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '생성일시',

  CONSTRAINT fk_facility_building
    FOREIGN KEY (building_id)
    REFERENCES building(building_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

  CONSTRAINT uq_building_facility
    UNIQUE (building_id, facility_name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='건물 특수시설(안내/설비용)';

INSERT INTO building_facility (building_id, facility_name, quantity) VALUES
-- G-01 본관
((SELECT building_id FROM building WHERE building_code='G-01'), '회의실', 8),
((SELECT building_id FROM building WHERE building_code='G-01'), '대강당', 1),

-- G-02 인문사회관
((SELECT building_id FROM building WHERE building_code='G-02'), '세미나실', 6),

-- G-03 경영경제관
((SELECT building_id FROM building WHERE building_code='G-03'), '모의투자실', 2),

-- G-04 자연과학관
((SELECT building_id FROM building WHERE building_code='G-04'), '공동실험실', 4),

-- G-05 공학관 A
((SELECT building_id FROM building WHERE building_code='G-05'), '서버실', 1),
((SELECT building_id FROM building WHERE building_code='G-05'), '캡스톤실', 1),

-- G-06 공학관 B
((SELECT building_id FROM building WHERE building_code='G-06'), '공작실', 1),
((SELECT building_id FROM building WHERE building_code='G-06'), '로봇실', 1),

-- G-07 정보통신관
((SELECT building_id FROM building WHERE building_code='G-07'), '보안관제실', 1),

-- G-08 미디어콘텐츠관
((SELECT building_id FROM building WHERE building_code='G-08'), '스튜디오', 4),

-- G-09 예술체육관
((SELECT building_id FROM building WHERE building_code='G-09'), '체육관', 1),
((SELECT building_id FROM building WHERE building_code='G-09'), '연습실', 1),

-- G-10 사범관
((SELECT building_id FROM building WHERE building_code='G-10'), '모의수업실', 4),

-- G-11 융합관
((SELECT building_id FROM building WHERE building_code='G-11'), '프로젝트룸', 1),

-- G-12 중앙도서관
((SELECT building_id FROM building WHERE building_code='G-12'), '열람석', 1200),

-- G-13 학생회관
((SELECT building_id FROM building WHERE building_code='G-13'), '동아리실', 20),

-- G-14 산학협력관
((SELECT building_id FROM building WHERE building_code='G-14'), '기업연구실', 12);
