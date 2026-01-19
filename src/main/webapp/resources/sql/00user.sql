CREATE TABLE IF NOT EXISTS user (
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 식별자',

    login_id        VARCHAR(50) NOT NULL COMMENT '로그인 아이디',
    password_hash   VARCHAR(255) NOT NULL COMMENT '비밀번호 해시',

    name            VARCHAR(50) NOT NULL COMMENT '이름',
    birth_date      DATE COMMENT '생년월일',

    email           VARCHAR(100) NOT NULL COMMENT '이메일',
    phone           VARCHAR(20) COMMENT '전화번호',

    role            ENUM('ADMIN', 'INSTRUCTOR', 'STUDENT')
                    NOT NULL COMMENT '역할',

    status          ENUM('ACTIVE', 'INACTIVE')
                    NOT NULL DEFAULT 'ACTIVE'
                    COMMENT '상태',

    gender          ENUM('M', 'F')
                    COMMENT '성별',

    address         VARCHAR(255) COMMENT '주소',

    must_change_pw  BOOLEAN NOT NULL DEFAULT FALSE
                    COMMENT '비밀번호 변경 필수 여부',

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    COMMENT '생성일시',

    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP
                    COMMENT '수정일시',

    CONSTRAINT uk_user_login_id UNIQUE (login_id),
    CONSTRAINT uk_user_email UNIQUE (email)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='사용자 테이블';