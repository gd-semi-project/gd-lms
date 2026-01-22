package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LectureAttendanceStatusDTO {

    private Long sessionId;

    private boolean isOpen;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}