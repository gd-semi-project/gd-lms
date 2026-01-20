CREATE TABLE IF NOT EXISTS department (
  department_id       BIGINT AUTO_INCREMENT PRIMARY KEY
    COMMENT '학과 PK',

  college_id     BIGINT NOT NULL
    COMMENT '소속 단과대학 ID',

  department_name     VARCHAR(50) NOT NULL
    COMMENT '학과명',

  annual_quota   INT NOT NULL DEFAULT 0
    COMMENT '학과 연간 입학 정원',

 department_code     VARCHAR(20)
    COMMENT '학과 코드 (예: CSE, BUS, ENG 등)',

  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '생성일시',

  CONSTRAINT fk_major_college
    FOREIGN KEY (college_id)
    REFERENCES college(college_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

  CONSTRAINT uq_major_name_in_college
    UNIQUE (college_id, department_name),

  CONSTRAINT ck_department_quota
    CHECK (annual_quota >= 0)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='학과 테이블';

INSERT INTO department (college_id, department_name, annual_quota, department_code) VALUES
-- =========================
-- 인문대학
-- =========================
((SELECT college_id FROM college WHERE college_name='인문대학'), '국어국문학과', 40, 'KLL'),
((SELECT college_id FROM college WHERE college_name='인문대학'), '영어영문학과', 45, 'ELL'),
((SELECT college_id FROM college WHERE college_name='인문대학'), '일본어학과', 35, 'JPN'),
((SELECT college_id FROM college WHERE college_name='인문대학'), '중국어학과', 35, 'CHN'),
((SELECT college_id FROM college WHERE college_name='인문대학'), '역사문화학과', 30, 'HIS'),

-- =========================
-- 사회과학대학
-- =========================
((SELECT college_id FROM college WHERE college_name='사회과학대학'), '행정학과', 60, 'PA'),
((SELECT college_id FROM college WHERE college_name='사회과학대학'), '정치외교학과', 45, 'POL'),
((SELECT college_id FROM college WHERE college_name='사회과학대학'), '사회복지학과', 55, 'SW'),
((SELECT college_id FROM college WHERE college_name='사회과학대학'), '언론정보학과', 50, 'JMC'),
((SELECT college_id FROM college WHERE college_name='사회과학대학'), '도시정책학과', 40, 'URB'),

-- =========================
-- 경영경제대학
-- =========================
((SELECT college_id FROM college WHERE college_name='경영경제대학'), '경영학과', 120, 'BUS'),
((SELECT college_id FROM college WHERE college_name='경영경제대학'), '회계학과', 80, 'ACC'),
((SELECT college_id FROM college WHERE college_name='경영경제대학'), '경제학과', 70, 'ECO'),
((SELECT college_id FROM college WHERE college_name='경영경제대학'), '국제통상학과', 65, 'INT'),
((SELECT college_id FROM college WHERE college_name='경영경제대학'), '금융보험학과', 60, 'FIN'),

-- =========================
-- 자연과학대학
-- =========================
((SELECT college_id FROM college WHERE college_name='자연과학대학'), '수학과', 45, 'MATH'),
((SELECT college_id FROM college WHERE college_name='자연과학대학'), '물리학과', 40, 'PHY'),
((SELECT college_id FROM college WHERE college_name='자연과학대학'), '화학과', 50, 'CHEM'),
((SELECT college_id FROM college WHERE college_name='자연과학대학'), '생명과학과', 60, 'BIO'),

-- =========================
-- 공과대학
-- =========================
((SELECT college_id FROM college WHERE college_name='공과대학'), '컴퓨터공학과', 140, 'CSE'),
((SELECT college_id FROM college WHERE college_name='공과대학'), '소프트웨어공학과', 130, 'SWE'),
((SELECT college_id FROM college WHERE college_name='공과대학'), '전자공학과', 110, 'ECE'),
((SELECT college_id FROM college WHERE college_name='공과대학'), '기계공학과', 100, 'ME'),
((SELECT college_id FROM college WHERE college_name='공과대학'), '산업공학과', 80, 'IE'),
((SELECT college_id FROM college WHERE college_name='공과대학'), '인공지능학과', 120, 'AI'),

-- =========================
-- 정보통신대학
-- =========================
((SELECT college_id FROM college WHERE college_name='정보통신대학'), '정보통신공학과', 90, 'ICE'),
((SELECT college_id FROM college WHERE college_name='정보통신대학'), '데이터사이언스학과', 100, 'DS'),
((SELECT college_id FROM college WHERE college_name='정보통신대학'), '정보보안학과', 80, 'SEC'),
((SELECT college_id FROM college WHERE college_name='정보통신대학'), '클라우드컴퓨팅학과', 70, 'CLOUD'),

-- =========================
-- 미디어콘텐츠대학
-- =========================
((SELECT college_id FROM college WHERE college_name='미디어콘텐츠대학'), '미디어디자인학과', 70, 'MD'),
((SELECT college_id FROM college WHERE college_name='미디어콘텐츠대학'), '영상콘텐츠학과', 65, 'VIDEO'),
((SELECT college_id FROM college WHERE college_name='미디어콘텐츠대학'), '게임학과', 80, 'GAME'),
((SELECT college_id FROM college WHERE college_name='미디어콘텐츠대학'), '애니메이션학과', 60, 'ANI'),

-- =========================
-- 예술체육대학
-- =========================
((SELECT college_id FROM college WHERE college_name='예술체육대학'), '시각디자인학과', 60, 'VD'),
((SELECT college_id FROM college WHERE college_name='예술체육대학'), '실용음악학과', 40, 'MUSIC'),
((SELECT college_id FROM college WHERE college_name='예술체육대학'), '체육학과', 70, 'PE'),
((SELECT college_id FROM college WHERE college_name='예술체육대학'), '스포츠과학과', 50, 'SPORT'),

-- =========================
-- 사범대학
-- =========================
((SELECT college_id FROM college WHERE college_name='사범대학'), '국어교육과', 45, 'KED'),
((SELECT college_id FROM college WHERE college_name='사범대학'), '영어교육과', 45, 'EED'),
((SELECT college_id FROM college WHERE college_name='사범대학'), '수학교육과', 40, 'MED'),
((SELECT college_id FROM college WHERE college_name='사범대학'), '컴퓨터교육과', 50, 'CED'),

-- =========================
-- 융합학부
-- =========================
((SELECT college_id FROM college WHERE college_name='융합학부'), '융합공학부', 80, 'FUSION'),
((SELECT college_id FROM college WHERE college_name='융합학부'), '자유전공학부', 70, 'FREE');
