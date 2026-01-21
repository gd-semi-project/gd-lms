CREATE TABLE file_upload (
    file_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    board_type VARCHAR(50) NOT NULL,   
    ref_id BIGINT NOT NULL,             

    uuid VARCHAR(36) NOT NULL,          
    original_filename VARCHAR(255) NOT NULL, 

    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP, 
    is_deleted CHAR(1) DEFAULT 'N'       
);