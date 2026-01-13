package model.dto;

import lombok.Data;

@Data
public class AttendanceDTO {
	
	private int attendanceId;
    private int courseId;
    private int studentId;
    private int week;
    private String status;
    private String note;
    private String createdAt;
    

}
