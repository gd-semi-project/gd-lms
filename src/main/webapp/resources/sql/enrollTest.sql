CREATE TABLE IF NOT EXISTS enrollment (
    enrollment_id   BIGINT AUTO_INCREMENT PRIMARY KEY
        COMMENT '수강신청 PK',

    lecture_id      BIGINT NOT NULL
        COMMENT '강의 ID',

    user_id         BIGINT NOT NULL
        COMMENT '학생 user_id',

    department_id   BIGINT NOT NULL
        COMMENT '신청 당시 학과 ID',

    status          ENUM('ENROLLED','DROPPED')
        NOT NULL DEFAULT 'ENROLLED'
        COMMENT '수강 상태',

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '신청일시',

    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
        COMMENT '상태 변경일시',

    -- 같은 강의를 같은 사용자가 중복 신청 불가
    CONSTRAINT uq_enrollment_lecture_user
        UNIQUE (lecture_id, user_id),

    -- FK: lecture
    CONSTRAINT fk_enrollment_lecture
        FOREIGN KEY (lecture_id)
        REFERENCES lecture(lecture_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    -- FK: user
    CONSTRAINT fk_enrollment_user
        FOREIGN KEY (user_id)
        REFERENCES user(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    -- FK: department
    CONSTRAINT fk_enrollment_department
        FOREIGN KEY (department_id)
        REFERENCES department(department_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='수강신청 테이블';
