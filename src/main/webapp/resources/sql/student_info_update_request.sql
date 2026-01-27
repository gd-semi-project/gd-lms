CREATE TABLE IF NOT EXISTS student_info_update_request (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,

    new_name VARCHAR(50),
    new_gender CHAR(1),
    new_account_no VARCHAR(50),
    new_department_id BIGINT,
    new_academic_status VARCHAR(20),

    reason VARCHAR(500),

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL,
    processed_by BIGINT NULL,
    reject_reason VARCHAR(500),

    CONSTRAINT fk_student_update_student
        FOREIGN KEY (student_id)
        REFERENCES student(student_id)
);