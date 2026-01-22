// AssignmentSubmissionDTO.java
package model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentSubmissionDTO {
    private Long submissionId;
    private Long assignmentId;
    private Long studentId;
    private String content;
    private Integer score;
    private String feedback;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    
    // 조회용
    private String studentName;
    private List<FileDTO> fileList;
}