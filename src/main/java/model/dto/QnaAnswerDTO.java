package model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.QnaStatus;
import model.enumtype.isDeleted;
import model.enumtype.isPrivate;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class QnaAnswerDTO {
    private Long answerId;
    private Long qnaId;
    private Long instructorId;
    private String content;
    private isDeleted isDeleted; // 'Y'/'N'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
