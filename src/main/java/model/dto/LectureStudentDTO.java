package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.EnrollmentStatus;

@Data
@NoArgsConstructor
public class LectureStudentDTO {	// 화면에 뿌릴 용도 : 수강중인 학생 조회

    private Long studentId;
    private Long userId;
    private String studentName;
    private Integer studentNumber;
    private int studentGrade;

    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime appliedAt;
}