package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreDTO {
    private Long scoreId;
    private Long lectureId;
    private Long studentId;

    private Integer attendanceScore;
    private Integer assignmentScore;
    private Integer midtermScore;
    private Integer finalScore;

    private Integer totalScore;
    private String gradeLetter;

    private boolean isCompleted;
    private boolean isConfirmed;
    
    
    // 조회용
    private String studentName;
    private Long studentNumber;
    private Integer studentGrade;
    
}
