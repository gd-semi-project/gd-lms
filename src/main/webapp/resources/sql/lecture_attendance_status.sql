CREATE TABLE lecture_attendance_status (
    session_id BIGINT PRIMARY KEY,

    is_open BOOLEAN NOT NULL DEFAULT FALSE,
    opened_at DATETIME NULL,
    closed_at DATETIME NULL,

    CONSTRAINT fk_att_status_session
        FOREIGN KEY (session_id)
        REFERENCES lecture_session(session_id)
        ON DELETE CASCADE
);