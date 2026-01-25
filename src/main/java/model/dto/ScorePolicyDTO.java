package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScorePolicyDTO {

    private Long scorePolicyId;
    private Long lectureId;

    private int attendanceWeight;
    private int assignmentWeight;
    private int midtermWeight;
    private int finalWeight;

    private boolean isConfirmed;
    
    // 헬퍼
    public int getTotalWeight() {
        return attendanceWeight
             + assignmentWeight
             + midtermWeight
             + finalWeight;
    }
}