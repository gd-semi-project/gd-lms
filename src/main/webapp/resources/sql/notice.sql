CREATE TABLE IF NOT EXISTS notice (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id BIGINT NULL,
    author_id BIGINT NOT NULL,
    notice_type VARCHAR(50) DEFAULT 'GENERAL',
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT DEFAULT 0,
    pinned CHAR(1) DEFAULT 'N',
    is_deleted CHAR(1) DEFAULT 'N',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id),
    FOREIGN KEY (author_id) REFERENCES user(user_id)
);