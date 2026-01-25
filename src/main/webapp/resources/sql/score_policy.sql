CREATE TABLE score_policy (
    score_policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    lecture_id BIGINT NOT NULL COMMENT '강의 ID',

    attendance_weight INT NOT NULL COMMENT '출석 배점 (%)',
    assignment_weight INT NOT NULL COMMENT '과제 배점 (%)',
    midterm_weight INT NOT NULL COMMENT '중간 배점 (%)',
    final_weight INT NOT NULL COMMENT '기말 배점 (%)',

    is_confirmed BOOLEAN DEFAULT FALSE COMMENT '배점 확정 여부',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    UNIQUE KEY uk_score_policy_lecture (lecture_id),

    FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id)
);

SELECT
    l.lecture_id,
    l.lecture_title,
    sp.attendance_weight,
    sp.assignment_weight,
    sp.midterm_weight,
    sp.final_weight
FROM lecture l
LEFT JOIN score_policy sp
    ON l.lecture_id = sp.lecture_id
ORDER BY l.lecture_id;