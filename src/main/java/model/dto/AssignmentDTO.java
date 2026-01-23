// AssignmentDTO.java
package model.dto;

import lombok.Data;
import model.enumtype.IsDeleted;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentDTO {
    private Long assignmentId;
    private Long lectureId;
    private String title;
    private String content;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private IsDeleted isDeleted;
    
    // 조회용
    private int submissionCount;
    private boolean isSubmitted;
    private List<FileDTO> fileList;
}