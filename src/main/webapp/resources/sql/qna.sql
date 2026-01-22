CREATE TABLE IF NOT EXISTS qna_posts (
  qna_id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '질문_식별자(PK)',
  lecture_id  BIGINT NOT NULL COMMENT '강의_식별자(FK lecture)',
  author_id   BIGINT NOT NULL COMMENT '작성자_식별자(FK user) - 학생',
  title       VARCHAR(200) NOT NULL COMMENT '제목',
  content     TEXT NOT NULL COMMENT '질문 내용',
  is_private  ENUM('Y','N') NOT NULL DEFAULT 'N' COMMENT '비공개 여부',
  status      ENUM('OPEN','ANSWERED','CLOSED') NOT NULL DEFAULT 'OPEN' COMMENT '처리상태',
  is_deleted  ENUM('Y','N') NOT NULL DEFAULT 'N' COMMENT '삭제여부(소프트)',
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (qna_id),
  KEY idx_qna_lecture_created (lecture_id, created_at),
  KEY idx_qna_author_created (author_id, created_at),
  KEY idx_qna_private (is_private),
  KEY idx_qna_status (status),
  KEY idx_qna_deleted (is_deleted),
  CONSTRAINT fk_qna_posts_lecture
    FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_qna_posts_author
    FOREIGN KEY (author_id) REFERENCES user(user_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='QnA 질문글';


CREATE TABLE IF NOT EXISTS qna_answers (
  answer_id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '답변_식별자(PK)',
  qna_id         BIGINT NOT NULL COMMENT '질문_식별자(FK qna_posts)',
  instructor_id  BIGINT NOT NULL COMMENT '답변자_식별자(FK user) - 강사/관리자',
  content        TEXT NOT NULL COMMENT '답변 내용',
  is_deleted     ENUM('Y','N') NOT NULL DEFAULT 'N' COMMENT '삭제여부(소프트)',
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (answer_id),
  KEY idx_ans_qna_created (qna_id, created_at),
  KEY idx_ans_instructor_created (instructor_id, created_at),
  KEY idx_ans_deleted (is_deleted),
  CONSTRAINT fk_qna_answers_post
    FOREIGN KEY (qna_id) REFERENCES qna_posts(qna_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_qna_answers_instructor
    FOREIGN KEY (instructor_id) REFERENCES user(user_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='QnA 답변';
  
  
  
  
  
SET @lecture1 = 158;
SET @lecture2 = 159;
SET @lecture3 = 160;
SET @lecture4 = 161;
SET @lecture5 = 162;
SET @lecture6 = 163;
SET @lecture7 = 164;
SET @lecture8 = 165;
SET @lecture9 = 166;
SET @lecture10 = 167;

SET @student1 = 10;
SET @student2 = 11;

SET @instructor = 2;  -- 강사 user_id=2

INSERT INTO qna_posts
(lecture_id, author_id, title, content, is_private, status, is_deleted)
VALUES
-- ===== 158 =====
(@lecture1,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture1,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture1,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 159 =====
(@lecture2,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture2,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture2,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 160 =====
(@lecture3,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture3,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture3,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 161 =====
(@lecture4,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture4,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture4,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 162 =====
(@lecture5,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture5,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture5,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 163 =====
(@lecture6,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture6,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture6,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 164 =====
(@lecture7,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture7,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture7,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 165 =====
(@lecture8,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture8,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture8,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 166 =====
(@lecture9,  @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture9,  @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture9,  @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N'),

-- ===== 167 =====
(@lecture10, @student1, '과제 제출 형식 문의',        '과제는 PDF로 제출해도 되나요? 파일명 규칙이 있나요?',                   'N', 'ANSWERED', 'N'),
(@lecture10, @student2, '시험 범위 질문(비공개)',     '중간고사 범위가 1~5주차 전체인가요?',                                   'Y', 'ANSWERED', 'N'),
(@lecture10, @student1, '실습 환경 설치 오류',        'JDK 설치 후 PATH 설정이 안 잡히는 것 같습니다. 확인 방법이 있나요?',   'N', 'ANSWERED', 'N');

INSERT INTO qna_answers (qna_id, instructor_id, content, is_deleted)
SELECT
  p.qna_id,
  @instructor AS instructor_id,
  CASE p.title
    WHEN '과제 제출 형식 문의' THEN
      '네, PDF 제출 가능합니다. 파일명은 "학번_이름_과제명.pdf" 형식으로 권장합니다. 업로드 후 미리보기로 정상 업로드 여부를 확인해주세요.'
    WHEN '시험 범위 질문(비공개)' THEN
      '시험 범위는 공지된 주차 기준으로 전체가 맞습니다. 특히 실습 예제/과제와 연계된 문제가 출제됩니다. 자세한 범위는 공지사항을 다시 안내하겠습니다.'
    WHEN '실습 환경 설치 오류' THEN
      'PATH는 터미널/명령프롬프트에서 "java -version"으로 확인 가능합니다. 안 되면 JAVA_HOME 설정과 Path 우선순위를 점검해 주세요.'
    ELSE
      '문의 확인했습니다. 공지/자료 업데이트 후 다시 안내드리겠습니다.'
  END AS content,
  'N' AS is_deleted
FROM qna_posts p
WHERE p.lecture_id BETWEEN 158 AND 167
  AND p.is_deleted = 'N'
  AND NOT EXISTS (SELECT 1 FROM qna_answers a WHERE a.qna_id = p.qna_id);