use lms;

CREATE TABLE user (
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





SELECT user_id, login_id, name, role
FROM user
WHERE role = 'INSTRUCTOR';




-- 더미용 데이터

INSERT INTO user (
    login_id,
    password_hash,
    name,
    birth_date,
    email,
    phone,
    role,
    status,
    gender,
    address,
    must_change_pw
) VALUES
(
    'inst_kim',
    '$2a$10$dummyhashforpassword01',
    '김도윤',
    '1985-04-12',
    'kim.doyoon@example.com',
    '010-1234-5678',
    'INSTRUCTOR',
    'ACTIVE',
    'M',
    '서울특별시 강남구 테헤란로 123',
    FALSE
),
(
    'inst_park',
    '$2a$10$dummyhashforpassword02',
    '박서연',
    '1990-09-30',
    'park.seoyeon@example.com',
    '010-2345-6789',
    'INSTRUCTOR',
    'ACTIVE',
    'F',
    '서울특별시 마포구 월드컵북로 45',
    FALSE
),
(
    'inst_lee',
    '$2a$10$dummyhashforpassword03',
    '이준혁',
    '1978-01-08',
    'lee.junhyuk@example.com',
    '010-3456-7890',
    'INSTRUCTOR',
    'ACTIVE',
    'M',
    '경기도 성남시 분당구 판교로 256',
    FALSE
);



