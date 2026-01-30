package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.AttendanceStatus;

@Data
@NoArgsConstructor
public class SessionAttendanceDTO { // 교수 출석용

    private Long attendanceId;
    private Long sessionId;
    private Long studentId;

    private String studentName;
    private Integer studentNumber;
    private Integer studentGrade;

    private AttendanceStatus status;
    private LocalDateTime checkedAt;
}