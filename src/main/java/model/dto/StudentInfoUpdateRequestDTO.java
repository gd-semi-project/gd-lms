package model.dto;

import lombok.Data;
import model.enumtype.Gender;
import model.enumtype.StudentStatus;

@Data
public class StudentInfoUpdateRequestDTO {
    private Long studentId;
    private Long requestId;

    private String newName;
    private Gender newGender;    
    private String newAccountNo;
    private Long newDepartmentId;   
    private StudentStatus newAcademicStatus;
    private String createdAt;

    private String reason;
    private String agree;              // "Y" (체크 필수)
}
