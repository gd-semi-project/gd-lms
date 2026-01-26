package model.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceSummaryDTO {
    private Long studentId;

    private int totalSessionCount;
    private int presentCount;
    private int lateCount;

    // 계산 결과
    private int effectiveAttendCount; // 지각 반영 후 출석 수
    private int absentCount;
    private int attendanceScore; // 0~100
}