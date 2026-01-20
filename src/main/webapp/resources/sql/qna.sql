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