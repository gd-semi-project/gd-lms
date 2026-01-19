package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.AttendanceStatus;

@Data
@NoArgsConstructor
public class AttendanceDTO {

    private Long attendanceId;   // PK
    private Long sessionId;      // FK → lecture_session
    private Long studentId;       // FK → student

    private AttendanceStatus status; // 출석 / 지각 / 결석
    private LocalDateTime checkedAt; // 출석 버튼 누른 시간

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}