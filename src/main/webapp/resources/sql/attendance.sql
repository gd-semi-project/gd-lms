CREATE TABLE attendance (
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    session_id BIGINT NOT NULL,   -- 수업 회차
    student_id BIGINT NOT NULL,   -- 학생(user_id)

    status ENUM('ABSENT','PRESENT','LATE')
           NOT NULL DEFAULT 'ABSENT',

    checked_at DATETIME NULL,     -- 출석 체크 시간
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attendance_session
        FOREIGN KEY (session_id)
        REFERENCES lecture_session(session_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id)
        REFERENCES user(user_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_attendance
        UNIQUE (session_id, student_id)
);
