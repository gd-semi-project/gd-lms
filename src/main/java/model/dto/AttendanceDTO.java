package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.AttendanceStatus;

@Data
@NoArgsConstructor
public class AttendanceDTO {

    private Long attendanceId;
    private Long sessionId;
    private Long studentId;

    private AttendanceStatus status; // ABSENT, PRESENT, LATE

    private LocalDate sessionDate;   // 수업 날짜
    private LocalDateTime checkedAt; // 출석 체크 시간
    
    // 조회용
    private LocalTime startTime;
    private LocalTime endTime;
}