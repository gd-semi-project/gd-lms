package model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    private int attendanceId;     // 출석 ID
    private int studentId;        // 수강생 ID
    private int sessionId;        // 회차 ID
    private String status;         // 출석 상태 (출석/지각/결석/공결)
    private LocalDateTime checkTime; // 출석 체크 시각
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}