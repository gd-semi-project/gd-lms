package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.AttendanceStatus;

@Data
@NoArgsConstructor
public class SessionAttendanceDTO {

    private long studentId;
    private String studentName;
    private Integer studentNumber;
    private int studentGrade;

    private AttendanceStatus status;
}