CREATE TABLE student (
    student_id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- PK

    department_id BIGINT NOT NULL,                   -- 학과
    user_id BIGINT NOT NULL,                         -- user 테이블 FK

    student_number INT NOT NULL UNIQUE,              -- 학번
    student_grade INT,                               -- 학년

    status ENUM('학부','대학원') NOT NULL,           -- 학부상태
    student_status ENUM('재학','휴학','졸업') 
                   NOT NULL DEFAULT '재학',          -- 학적상태

    enroll_date DATETIME,                            -- 입학일
    end_date DATETIME,                               -- 졸업일
    tuition_account VARCHAR(255),                    -- 등록금 계좌

    -- FK 설정
    CONSTRAINT fk_student_user
        FOREIGN KEY (user_id) 
        REFERENCES `user`(user_id),

    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id) 
        REFERENCES department(department_id)
);

INSERT INTO `user`
(login_id, password_hash, name, gender, email, phone, birth_date, role, status)
VALUES
('student01', 'hashed_pw_01', '김학생', 'M', 'student01@test.com', '010-1111-1111', '2002-03-15', 'STUDENT', 'ACTIVE'),
('student02', 'hashed_pw_02', '이학생', 'F', 'student02@test.com', '010-2222-2222', '2001-07-21', 'STUDENT', 'ACTIVE'),
('student03', 'hashed_pw_03', '박학생', 'M', 'student03@test.com', '010-3333-3333', '2000-12-05', 'STUDENT', 'ACTIVE');

