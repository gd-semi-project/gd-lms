package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LectureSessionDTO {

    private Long sessionId;
    private Long lectureId;

    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private boolean attendanceOpen; // 출석 열림 여부
}