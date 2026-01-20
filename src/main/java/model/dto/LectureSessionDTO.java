package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LectureSessionDTO {	// 수업 회차 DTO

    private Long sessionId;       
    private Long lectureId;       

    private LocalDate sessionDate; // 수업 날짜 (2026-03-18)
    private LocalTime startTime;   // 수업 시작 시간
    private LocalTime endTime;     // 수업 종료 시간

    private boolean isCanceled;    // 휴강 여부
    private String note;           // 보강/휴강 사유

    private LocalDateTime createdAt;
}