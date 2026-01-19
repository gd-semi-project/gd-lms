CREATE TABLE IF NOT EXISTS lecture_schedule (
    schedule_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '시간표 식별자',

    lecture_id      BIGINT NOT NULL COMMENT '강의 식별자',

    week_day        ENUM('MON','TUE','WED','THU','FRI','SAT','SUN')
                    NOT NULL COMMENT '요일',

    start_time      TIME NOT NULL COMMENT '시작 시간',
    end_time        TIME NOT NULL COMMENT '종료 시간',

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    COMMENT '생성일시',

    CONSTRAINT fk_schedule_lecture
        FOREIGN KEY (lecture_id)
        REFERENCES lecture(lecture_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_schedule_time
        CHECK (start_time < end_time)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='강의 시간표 테이블';
