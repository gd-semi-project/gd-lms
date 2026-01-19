CREATE TABLE IF NOT EXISTS lecture (
    lecture_id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '강의 식별자',

    lecture_title   VARCHAR(200) NOT NULL COMMENT '강의명',
    lecture_round   INT NOT NULL COMMENT '강의 차수',

    user_id         BIGINT NOT NULL COMMENT '강사 식별자',

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
  section_count   INT NOT NULL       
) ENGINE=MEMORY;


INSERT INTO tmp_lecture_seed (lecture_title, lecture_round, base_user_id, room, capacity, section_count) VALUES
('현대문학의 이해',            1, 71, 'G-02-201', 60,  1),
('고전문학 개론',              1, 72, 'G-02-202', 50,  1),
('영어학 입문',                1, 73, 'G-02-203', 55,  1),
('일본어 초급 I',              1, 74, 'G-02-204', 35,  1),
('중국어 초급 I',              1, 75, 'G-02-205', 35,  1),
('한국사 개론',                1, 76, 'G-02-206', 45,  1),
('행정학 원론',                1, 77, 'G-02-207', 60,  1),
('정치학 개론',                1, 78, 'G-02-208', 50,  1),
('사회복지개론',               1, 79, 'G-02-209', 55,  1),
('언론과 사회',                1, 80, 'G-02-210', 50,  1),
('도시정책의 이해',            1, 81, 'G-02-211', 40,  1),
('회계원리',                   1, 82, 'G-03-301', 80,  1),
('경제학 입문',                1, 83, 'G-03-302', 70,  1),
('국제통상론',                 1, 84, 'G-03-303', 65,  1),
('금융시장 이해',              1, 85, 'G-03-304', 60,  1),
('미적분학 I',                 1, 86, 'G-04-101', 45,  1),
('일반물리학 I',               1, 87, 'G-04-102', 50,  1),
('일반화학 I',                 1, 88, 'G-04-103', 50,  1),
('생명과학 개론',              1, 89, 'G-04-104', 60,  1),
('교육학 개론',                1, 90, 'G-10-201', 45,  1),

('경영학 원론',                1, 91, 'G-03-305', 120, 2),
('마케팅의 이해',              1, 92, 'G-03-306', 80,  2),
('조직행동론',                 1, 93, 'G-03-307', 70,  2),
('재무관리',                   1, 94, 'G-03-308', 70,  2),
('통계학 개론',                1, 95, 'G-04-105', 60,  2),
('선형대수',                   1, 96, 'G-04-106', 40,  2),
('물리실험 I',                 1, 97, 'G-04-107', 30,  2),
('화학실험 I',                 1, 98, 'G-04-108', 30,  2),
('분자생물학 기초',            1, 99, 'G-04-109', 45,  2),
('프로그래밍 기초',            1,100, 'G-05-401', 120, 2),
('객체지향 프로그래밍',        1, 71, 'G-05-402', 100, 2),
('자료구조',                   1, 72, 'G-05-403', 90,  2),
('운영체제',                   1, 73, 'G-05-404', 80,  2),
('데이터베이스',               1, 74, 'G-05-405', 90,  2),
('컴퓨터네트워크',             1, 75, 'G-07-301', 80,  2),
('정보보안 개론',              1, 76, 'G-07-302', 70,  2),
('클라우드 컴퓨팅',            1, 77, 'G-07-303', 70,  2),
('데이터사이언스 입문',        1, 78, 'G-07-304', 100, 2),
('UX/UI 디자인 기초',          1, 79, 'G-08-201', 60,  2),
('영상제작 입문',              1, 80, 'G-08-202', 65,  2),
('게임기획 개론',              1, 81, 'G-08-203', 80,  2),
('애니메이션 기초',            1, 82, 'G-08-204', 60,  2),
('시각디자인 입문',            1, 83, 'G-09-101', 60,  2),
('체육학 개론',                1, 84, 'G-09-102', 70,  2),
('자유전공 탐색',              1, 85, 'G-11-301', 70,  2),

('캡스톤디자인 I',             1, 86, 'G-05-406', 60,  3),
('인공지능 개론',              1, 87, 'G-05-407', 120, 3),
('웹프로그래밍',               1, 88, 'G-05-408', 90,  3),
('데이터분석 실습',            1, 89, 'G-07-305', 40,  3),
('정보보안 실습',              1, 90, 'G-07-306', 35,  3),
('영상편집 실습',              1, 91, 'G-08-205', 30,  3),
('게임프로그래밍',             1, 92, 'G-08-206', 50,  3),
('실용음악 앙상블',            1, 93, 'G-09-103', 40,  3),
('수학교육론',                 1, 94, 'G-10-202', 40,  3),
('융합공학 세미나',            1, 95, 'G-11-302', 80,  3);

INSERT INTO lecture
(lecture_title, lecture_round, user_id, start_date, end_date, room, capacity, status, section)
SELECT
  s.lecture_title,
  s.lecture_round,
  (71 + MOD((s.base_user_id - 71) + sec.offset, 30)) AS user_id,
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
