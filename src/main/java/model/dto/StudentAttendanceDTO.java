package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.AttendanceStatus;

@Data
@NoArgsConstructor
public class StudentAttendanceDTO {	 // 학생용 출석 기록 보기 전용

    private long sessionId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private AttendanceStatus status;
    private LocalDateTime checkedAt;
}