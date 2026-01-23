CREATE TABLE lecture_session (
    session_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id   BIGINT NOT NULL,

    session_date DATE NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,

    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_session_lecture
        FOREIGN KEY (lecture_id)
        REFERENCES lecture(lecture_id)
        ON DELETE CASCADE
);


