package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.EnrollmentStatus;

@Data
@NoArgsConstructor
public class LectureStudentDTO {	// 화면에 뿌릴 용도 : 수강중인 학생 조회

    private long studentId;
    private long userId;
    private String studentName;
    private Integer studentNumber;
    private int studenGrade;

    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime appliedAt;
}