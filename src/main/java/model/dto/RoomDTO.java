package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomDTO {

    private Long roomId;
    private Long buildingId;

    private int floorNo;
    private String roomNo;

    private String roomCode;
    private String roomType;

    private int capacity;
    private String roomName;

    private LocalDateTime createdAt;
}