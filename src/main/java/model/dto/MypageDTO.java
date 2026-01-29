package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MypageDTO {
//	공통 users
	private UserDTO user;
	
//	전공/부서
	private DepartmentDTO department;
	
//	학생 
	private StudentDTO student;
	
//	교수 
	private InstructorDTO professor;
	
	
	
	
}