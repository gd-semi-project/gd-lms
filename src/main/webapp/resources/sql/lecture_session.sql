CREATE TABLE lecture_session (
    session_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id   BIGINT NOT NULL,

    session_date DATE NOT NULL,       -- 수업 날짜
    start_time   TIME NOT NULL,       -- 시작 시간
    end_time     TIME NOT NULL,       -- 종료 시간

    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_session_lecture
        FOREIGN KEY (lecture_id)
        REFERENCES lecture(lecture_id)
        ON DELETE CASCADE
);


