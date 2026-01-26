CREATE TABLE score (
    score_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    lecture_id BIGINT NOT NULL COMMENT '강의 ID',
    student_id BIGINT NOT NULL COMMENT '학생 ID',

    attendance_score INT COMMENT '출석 점수',
    assignment_score INT COMMENT '과제 점수',
    midterm_score INT COMMENT '중간고사 점수',
    final_score INT COMMENT '기말고사 점수',

    total_score INT COMMENT '총점',
    grade_letter CHAR(2) COMMENT '학점 (A+, A, B, C …)',

    is_completed BOOLEAN DEFAULT FALSE COMMENT '모든 성적 입력 완료 여부',
    is_confirmed BOOLEAN DEFAULT FALSE COMMENT '교수 성적 확정 여부',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    UNIQUE KEY uk_score_lecture_student (lecture_id, student_id),

    FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id)
);