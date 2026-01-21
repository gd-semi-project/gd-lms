CREATE TABLE IF NOT EXISTS lecture (
    lecture_id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '강의 식별자',

    lecture_title   VARCHAR(200) NOT NULL COMMENT '강의명',
    lecture_round   INT NOT NULL COMMENT '강의 차수',

    user_id         BIGINT NOT NULL COMMENT '강사 식별자',
	department_id   BIGINT NOT NULL COMMENT '소속 학과 ID',
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
                    
    section         CHAR(1) NOT NULL DEFAULT 'A' COMMENT '분반',

    CONSTRAINT fk_lecture_user
        FOREIGN KEY (user_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

	CONSTRAINT fk_lecture_department
		FOREIGN KEY (department_id)
		REFERENCES department(department_id)
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

DROP TEMPORARY TABLE IF EXISTS tmp_lecture_seed;

CREATE TEMPORARY TABLE tmp_lecture_seed (
  lecture_title   VARCHAR(200) NOT NULL,
  lecture_round   INT NOT NULL,
  base_user_id    BIGINT NOT NULL,    
  room            VARCHAR(100),
  capacity        INT NOT NULL,
  section_count   INT NOT NULL,       
  department_id   BIGINT NOT NULL
) ENGINE=MEMORY;
INSERT INTO tmp_lecture_seed
(lecture_title, lecture_round, base_user_id, room, capacity, section_count, department_id)
VALUES
('현대문학의 이해',            1, 71, 'G-02-201', 60,  1, (SELECT department_id FROM department WHERE department_code='KLL')),
('고전문학 개론',              1, 72, 'G-02-202', 50,  1, (SELECT department_id FROM department WHERE department_code='KLL')),
('영어학 입문',                1, 73, 'G-02-203', 55,  1, (SELECT department_id FROM department WHERE department_code='ELL')),
('일본어 초급 I',              1, 74, 'G-02-204', 35,  1, (SELECT department_id FROM department WHERE department_code='JPN')),
('중국어 초급 I',              1, 75, 'G-02-205', 35,  1, (SELECT department_id FROM department WHERE department_code='CHN')),
('한국사 개론',                1, 76, 'G-02-206', 45,  1, (SELECT department_id FROM department WHERE department_code='HIS')),
('행정학 원론',                1, 77, 'G-02-207', 60,  1, (SELECT department_id FROM department WHERE department_code='PA')),
('정치학 개론',                1, 78, 'G-02-208', 50,  1, (SELECT department_id FROM department WHERE department_code='POL')),
('사회복지개론',               1, 79, 'G-02-209', 55,  1, (SELECT department_id FROM department WHERE department_code='SW')),
('언론과 사회',                1, 80, 'G-02-210', 50,  1, (SELECT department_id FROM department WHERE department_code='JMC')),
('도시정책의 이해',            1, 81, 'G-02-211', 40,  1, (SELECT department_id FROM department WHERE department_code='URB')),
('회계원리',                   1, 82, 'G-03-301', 80,  1, (SELECT department_id FROM department WHERE department_code='ACC')),
('경제학 입문',                1, 83, 'G-03-302', 70,  1, (SELECT department_id FROM department WHERE department_code='ECO')),
('국제통상론',                 1, 84, 'G-03-303', 65,  1, (SELECT department_id FROM department WHERE department_code='INT')),
('금융시장 이해',              1, 85, 'G-03-304', 60,  1, (SELECT department_id FROM department WHERE department_code='FIN')),
('미적분학 I',                 1, 86, 'G-04-101', 45,  1, (SELECT department_id FROM department WHERE department_code='MATH')),
('일반물리학 I',               1, 87, 'G-04-102', 50,  1, (SELECT department_id FROM department WHERE department_code='PHY')),
('일반화학 I',                 1, 88, 'G-04-103', 50,  1, (SELECT department_id FROM department WHERE department_code='CHEM')),
('생명과학 개론',              1, 89, 'G-04-104', 60,  1, (SELECT department_id FROM department WHERE department_code='BIO')),
('교육학 개론',                1, 90, 'G-10-201', 45,  1, (SELECT department_id FROM department WHERE department_code='FREE')),

('경영학 원론',                1, 91, 'G-03-305', 120, 2, (SELECT department_id FROM department WHERE department_code='BUS')),
('마케팅의 이해',              1, 92, 'G-03-306', 80,  2, (SELECT department_id FROM department WHERE department_code='BUS')),
('조직행동론',                 1, 93, 'G-03-307', 70,  2, (SELECT department_id FROM department WHERE department_code='BUS')),
('재무관리',                   1, 94, 'G-03-308', 70,  2, (SELECT department_id FROM department WHERE department_code='BUS')),
('통계학 개론',                1, 95, 'G-04-105', 60,  2, (SELECT department_id FROM department WHERE department_code='MATH')),
('선형대수',                   1, 96, 'G-04-106', 40,  2, (SELECT department_id FROM department WHERE department_code='MATH')),
('물리실험 I',                 1, 97, 'G-04-107', 30,  2, (SELECT department_id FROM department WHERE department_code='PHY')),
('화학실험 I',                 1, 98, 'G-04-108', 30,  2, (SELECT department_id FROM department WHERE department_code='CHEM')),
('분자생물학 기초',            1, 99, 'G-04-109', 45,  2, (SELECT department_id FROM department WHERE department_code='BIO')),
('프로그래밍 기초',            1,100, 'G-05-401', 120, 2, (SELECT department_id FROM department WHERE department_code='CSE')),
('객체지향 프로그래밍',        1, 71, 'G-05-402', 100, 2, (SELECT department_id FROM department WHERE department_code='CSE')),
('자료구조',                   1, 72, 'G-05-403', 90,  2, (SELECT department_id FROM department WHERE department_code='CSE')),
('운영체제',                   1, 73, 'G-05-404', 80,  2, (SELECT department_id FROM department WHERE department_code='CSE')),
('데이터베이스',               1, 74, 'G-05-405', 90,  2, (SELECT department_id FROM department WHERE department_code='DS')),
('컴퓨터네트워크',             1, 75, 'G-07-301', 80,  2, (SELECT department_id FROM department WHERE department_code='ICE')),
('정보보안 개론',              1, 76, 'G-07-302', 70,  2, (SELECT department_id FROM department WHERE department_code='SEC')),
('클라우드 컴퓨팅',            1, 77, 'G-07-303', 70,  2, (SELECT department_id FROM department WHERE department_code='CLOUD')),
('데이터사이언스 입문',        1, 78, 'G-07-304', 100, 2, (SELECT department_id FROM department WHERE department_code='DS')),
('UX/UI 디자인 기초',          1, 79, 'G-08-201', 60,  2, (SELECT department_id FROM department WHERE department_code='MD')),
('영상제작 입문',              1, 80, 'G-08-202', 65,  2, (SELECT department_id FROM department WHERE department_code='VIDEO')),
('게임기획 개론',              1, 81, 'G-08-203', 80,  2, (SELECT department_id FROM department WHERE department_code='GAME')),
('애니메이션 기초',            1, 82, 'G-08-204', 60,  2, (SELECT department_id FROM department WHERE department_code='ANI')),
('시각디자인 입문',            1, 83, 'G-09-101', 60,  2, (SELECT department_id FROM department WHERE department_code='VD')),
('체육학 개론',                1, 84, 'G-09-102', 70,  2, (SELECT department_id FROM department WHERE department_code='PE')),
('자유전공 탐색',              1, 85, 'G-11-301', 70,  2, (SELECT department_id FROM department WHERE department_code='FREE')),

('캡스톤디자인 I',             1, 86, 'G-05-406', 60,  3, (SELECT department_id FROM department WHERE department_code='CSE')),
('인공지능 개론',              1, 87, 'G-05-407', 120, 3, (SELECT department_id FROM department WHERE department_code='AI')),
('웹프로그래밍',               1, 88, 'G-05-408', 90,  3, (SELECT department_id FROM department WHERE department_code='SWE')),
('데이터분석 실습',            1, 89, 'G-07-305', 40,  3, (SELECT department_id FROM department WHERE department_code='DS')),
('정보보안 실습',              1, 90, 'G-07-306', 35,  3, (SELECT department_id FROM department WHERE department_code='SEC')),
('영상편집 실습',              1, 91, 'G-08-205', 30,  3, (SELECT department_id FROM department WHERE department_code='VIDEO')),
('게임프로그래밍',             1, 92, 'G-08-206', 50,  3, (SELECT department_id FROM department WHERE department_code='GAME')),
('실용음악 앙상블',            1, 93, 'G-09-103', 40,  3, (SELECT department_id FROM department WHERE department_code='MUSIC')),
('수학교육론',                 1, 94, 'G-10-202', 40,  3, (SELECT department_id FROM department WHERE department_code='MED')),
('융합공학 세미나',            1, 95, 'G-11-302', 80,  3, (SELECT department_id FROM department WHERE department_code='FUSION'));

INSERT INTO lecture
(lecture_title, lecture_round, user_id, department_id, start_date, end_date, room, capacity, status, section)
SELECT
  s.lecture_title,
  s.lecture_round,
  (71 + MOD((s.base_user_id - 71) + sec.offset, 30)) AS user_id,
  s.department_id,
  '2026-03-02' AS start_date,
  '2026-06-20' AS end_date,
  s.room,
  s.capacity,
  'PLANNED' AS status,
  sec.section
FROM tmp_lecture_seed s
JOIN (
  SELECT 0 AS offset, 'A' AS section
  UNION ALL SELECT 1, 'B'
  UNION ALL SELECT 2, 'C'
) sec
WHERE sec.offset < s.section_count
ORDER BY s.lecture_title, sec.offset;

INSERT INTO lecture
(lecture_title, lecture_round, user_id, department_id, start_date, end_date, room, capacity, status, validation, section)
VALUES
('파이썬 데이터분석 입문',        1, 71, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-07-401',  80, 'PLANNED', 'CONFIRMED', 'A'),
('자바 웹개발 실전',              1, 72, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-409',  90, 'PLANNED', 'CONFIRMED', 'A'),
('리눅스 시스템 기초',            1, 73, (SELECT department_id FROM department WHERE department_code='ICE'),   '2026-03-02', '2026-06-20', 'G-07-402',  70, 'PLANNED', 'CONFIRMED', 'A'),
('네트워크 실무와 트러블슈팅',     1, 74, (SELECT department_id FROM department WHERE department_code='ICE'),   '2026-03-02', '2026-06-20', 'G-07-403',  60, 'PLANNED', 'CONFIRMED', 'B'),
('SQL 집중 트레이닝',             1, 75, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-05-410', 100, 'PLANNED', 'CONFIRMED', 'A'),
('웹UI/UX 프로토타이핑',          1, 76, (SELECT department_id FROM department WHERE department_code='MD'),    '2026-03-02', '2026-06-20', 'G-08-301',  60, 'PLANNED', 'CONFIRMED', 'B'),
('알고리즘 문제해결 워크숍',       1, 77, (SELECT department_id FROM department WHERE department_code='CSE'),   '2026-03-02', '2026-06-20', 'G-05-411',  80, 'PLANNED', 'CONFIRMED', 'A'),
('객체지향 설계 패턴',            1, 78, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-412',  70, 'PLANNED', 'CONFIRMED', 'A'),
('클라우드 인프라 실습',          1, 79, (SELECT department_id FROM department WHERE department_code='CLOUD'), '2026-03-02', '2026-06-20', 'G-07-404',  45, 'PLANNED', 'CONFIRMED', 'A'),
('정보보안 위협과 대응',          1, 80, (SELECT department_id FROM department WHERE department_code='SEC'),   '2026-03-02', '2026-06-20', 'G-07-405',  70, 'PLANNED', 'CONFIRMED', 'B'),

('데이터 시각화 기초',            1, 81, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-07-406',  60, 'PLANNED', 'CONFIRMED', 'A'),
('기초 통계와 실험설계',          1, 82, (SELECT department_id FROM department WHERE department_code='MATH'),  '2026-03-02', '2026-06-20', 'G-04-201',  50, 'PLANNED', 'CONFIRMED', 'A'),
('디지털 콘텐츠 기획',            1, 83, (SELECT department_id FROM department WHERE department_code='VIDEO'), '2026-03-02', '2026-06-20', 'G-08-302',  65, 'PLANNED', 'CONFIRMED', 'A'),
('영상촬영과 조명 실습',          1, 84, (SELECT department_id FROM department WHERE department_code='VIDEO'), '2026-03-02', '2026-06-20', 'G-08-303',  30, 'PLANNED', 'CONFIRMED', 'B'),
('편집디자인 실무',               1, 85, (SELECT department_id FROM department WHERE department_code='VD'),    '2026-03-02', '2026-06-20', 'G-09-201',  55, 'PLANNED', 'CONFIRMED', 'A'),
('타이포그래피 기초',             1, 86, (SELECT department_id FROM department WHERE department_code='VD'),    '2026-03-02', '2026-06-20', 'G-09-202',  45, 'PLANNED', 'CONFIRMED', 'B'),
('게임레벨디자인',                1, 87, (SELECT department_id FROM department WHERE department_code='GAME'),  '2026-03-02', '2026-06-20', 'G-08-304',  60, 'PLANNED', 'CONFIRMED', 'A'),
('게임서사와 연출',               1, 88, (SELECT department_id FROM department WHERE department_code='GAME'),  '2026-03-02', '2026-06-20', 'G-08-305',  70, 'PLANNED', 'CONFIRMED', 'B'),
('프론트엔드 프레임워크 입문',    1, 89, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-413',  90, 'PLANNED', 'CONFIRMED', 'A'),
('백엔드 API 설계',               1, 90, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-414',  80, 'PLANNED', 'CONFIRMED', 'B'),

('DevOps 파이프라인 기초',        1, 91, (SELECT department_id FROM department WHERE department_code='CLOUD'), '2026-03-02', '2026-06-20', 'G-07-407',  50, 'PLANNED', 'CONFIRMED', 'A'),
('컨테이너와 쿠버네티스 입문',     1, 92, (SELECT department_id FROM department WHERE department_code='CLOUD'), '2026-03-02', '2026-06-20', 'G-07-408',  60, 'PLANNED', 'CONFIRMED', 'A'),
('AI 활용 프롬프트 엔지니어링',    1, 93, (SELECT department_id FROM department WHERE department_code='AI'),    '2026-03-02', '2026-06-20', 'G-05-415', 120, 'PLANNED', 'CONFIRMED', 'A'),
('머신러닝 실습: 분류/회귀',       1, 94, (SELECT department_id FROM department WHERE department_code='AI'),    '2026-03-02', '2026-06-20', 'G-07-409',  40, 'PLANNED', 'CONFIRMED', 'B'),
('데이터베이스 튜닝 기초',         1, 95, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-05-416',  70, 'PLANNED', 'CONFIRMED', 'A'),
('운영체제 심화: 동시성',          1, 96, (SELECT department_id FROM department WHERE department_code='CSE'),   '2026-03-02', '2026-06-20', 'G-05-417',  60, 'PLANNED', 'CONFIRMED', 'B'),
('클린코드와 리팩토링',            1, 97, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-418',  80, 'PLANNED', 'CONFIRMED', 'A'),
('테스트 자동화(JUnit) 실습',      1, 98, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-419',  50, 'PLANNED', 'CONFIRMED', 'B'),
('프로젝트 관리와 협업(Git)',      1, 99, (SELECT department_id FROM department WHERE department_code='FUSION'),'2026-03-02', '2026-06-20', 'G-11-401',  90, 'PLANNED', 'CONFIRMED', 'A'),
('기술면접 대비: CS 핵심정리',     1,100, (SELECT department_id FROM department WHERE department_code='FREE'), '2026-03-02', '2026-06-20', 'G-11-402', 120, 'PLANNED', 'CONFIRMED', 'B');



INSERT INTO lecture
(lecture_title, lecture_round, user_id, department_id, start_date, end_date, room, capacity, status, validation, section)
VALUES
('Spring Framework 기초',        1, 2, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-01-06', '2026-02-28', 'A동 301호', 30, 'ONGOING', 'CONFIRMED', 'A'),
('React 실전 프로젝트',          2, 2, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-01-13', '2026-03-07', 'B동 201호', 25, 'ONGOING', 'CONFIRMED', 'A'),
('Python 데이터 분석',           1, 2, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-01-06', '2026-02-21', 'C동 401호', 35, 'ONGOING', 'CONFIRMED', 'B'),
('Java 프로그래밍 입문',         3, 2, (SELECT department_id FROM department WHERE department_code='CSE'),   '2026-01-20', '2026-03-14', 'A동 302호', 28, 'ONGOING', 'CONFIRMED', 'A'),

('SQL 집중 트레이닝',            1, 2, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-05-410', 40, 'PLANNED', 'CONFIRMED', 'A'),
('백엔드 API 설계',              1, 2, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-414', 35, 'PLANNED', 'CONFIRMED', 'B'),
('클라우드 인프라 실습',          1, 2, (SELECT department_id FROM department WHERE department_code='CLOUD'), '2026-03-02', '2026-06-20', 'G-07-404', 20, 'PLANNED', 'CONFIRMED', 'A'),
('정보보안 위협과 대응',          1, 2, (SELECT department_id FROM department WHERE department_code='SEC'),   '2026-03-02', '2026-06-20', 'G-07-405', 25, 'PLANNED', 'CONFIRMED', 'B'),
('데이터사이언스 입문',           1, 2, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-07-304', 45, 'PLANNED', 'CONFIRMED', 'A'),
('AI 활용 프롬프트 엔지니어링',    1, 2, (SELECT department_id FROM department WHERE department_code='AI'),    '2026-03-02', '2026-06-20', 'G-05-415', 60, 'PLANNED', 'CONFIRMED', 'A');

  
  
INSERT INTO lecture
(lecture_title, lecture_round, user_id, department_id, start_date, end_date, room, capacity, status, validation, section)
VALUES
('Spring Framework 기초',        1, 2, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-01-06', '2026-02-28', 'A동 301호', 30, 'ONGOING', 'CONFIRMED', 'A'),
('React 실전 프로젝트',          2, 2, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-01-13', '2026-03-07', 'B동 201호', 25, 'ONGOING', 'CONFIRMED', 'A'),
('Python 데이터 분석',           1, 2, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-01-06', '2026-02-21', 'C동 401호', 35, 'ONGOING', 'CONFIRMED', 'B'),
('Java 프로그래밍 입문',         3, 2, (SELECT department_id FROM department WHERE department_code='CSE'),   '2026-01-20', '2026-03-14', 'A동 302호', 28, 'ONGOING', 'CONFIRMED', 'A'),

('SQL 집중 트레이닝',            1, 2, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-05-410', 40, 'PLANNED', 'CONFIRMED', 'A'),
('백엔드 API 설계',              1, 2, (SELECT department_id FROM department WHERE department_code='SWE'),   '2026-03-02', '2026-06-20', 'G-05-414', 35, 'PLANNED', 'CONFIRMED', 'B'),
('클라우드 인프라 실습',          1, 2, (SELECT department_id FROM department WHERE department_code='CLOUD'), '2026-03-02', '2026-06-20', 'G-07-404', 20, 'PLANNED', 'CONFIRMED', 'A'),
('정보보안 위협과 대응',          1, 2, (SELECT department_id FROM department WHERE department_code='SEC'),   '2026-03-02', '2026-06-20', 'G-07-405', 25, 'PLANNED', 'CONFIRMED', 'B'),
('데이터사이언스 입문',           1, 2, (SELECT department_id FROM department WHERE department_code='DS'),    '2026-03-02', '2026-06-20', 'G-07-304', 45, 'PLANNED', 'CONFIRMED', 'A'),
('AI 활용 프롬프트 엔지니어링',    1, 2, (SELECT department_id FROM department WHERE department_code='AI'),    '2026-03-02', '2026-06-20', 'G-05-415', 60, 'PLANNED', 'CONFIRMED', 'A');
  