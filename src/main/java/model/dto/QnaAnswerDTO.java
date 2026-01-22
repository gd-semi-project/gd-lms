package model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.QnaStatus;
import model.enumtype.IsDeleted;
import model.enumtype.IsPrivate;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class QnaAnswerDTO {
    private Long answerId;
    private Long qnaId;
    private Long instructorId;
    private String content;
    private IsDeleted isDeleted; // 'Y'/'N'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
