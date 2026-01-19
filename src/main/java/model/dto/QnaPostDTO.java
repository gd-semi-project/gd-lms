package model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.QnaStatus;
import model.enumtype.isPrivate;
import model.enumtype.isDeleted;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class QnaPostDTO {
    private Long qnaId;
    private Long lectureId;
    private Long authorId;
    private String title;
    private String content;
    private isPrivate isPrivate; // 'Y'/'N'
    private QnaStatus status;    // OPEN/ANSWERED/CLOSED
    private isDeleted isDeleted; // 'Y'/'N'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
