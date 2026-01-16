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