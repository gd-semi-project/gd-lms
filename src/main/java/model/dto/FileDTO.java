package model.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileDTO {

    private Long fileId;

    private String boardType;
    private Long refId;

    private UUID uuid;
    private String originalFilename;

    private LocalDateTime uploadedAt;
}