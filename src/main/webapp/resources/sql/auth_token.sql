CREATE TABLE auth_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_value VARCHAR(255) NOT NULL,
    token_type VARCHAR(30) NOT NULL,
    issued_ip VARCHAR(45),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    verified_at DATETIME NULL,

    CONSTRAINT fk_auth_token_user
        FOREIGN KEY (user_id) REFERENCES user(user_id)
);