CREATE TABLE file_upload (
    file_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    board_type VARCHAR(50) NOT NULL,   -- ASSIGNMENT, NOTICE, QNA 등
    ref_id BIGINT NOT NULL,             -- 게시글 번호 / submission_id

    uuid VARCHAR(36) NOT NULL,           -- UUID (중복 방지, 파일 식별)
    original_filename VARCHAR(255) NOT NULL, -- 사용자에게 보여줄 이름

    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 업로드 시각
    is_deleted CHAR(1) DEFAULT 'N'       -- 재제출/삭제 대비 (Y/N)
);