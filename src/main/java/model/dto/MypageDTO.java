package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.StudentStatus;
import model.enumtype.StudentType;

@Data
@NoArgsConstructor
public class MypageDTO {
//	공통 users
	private UserDTO user;
	
//	전공/부서
	private DepartmentDTO department;
	
//	학생 
	private StudentsDTO student;
	
//	교수 
	private ProfessorDTO professor;
	

	
	
	
}
