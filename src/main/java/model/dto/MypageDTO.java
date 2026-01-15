package model.dto;

import java.time.LocalDateTime;

import model.enumtype.StudentStatus;
import model.enumtype.StudentType;

public class MypageDTO {
//	공통 users 
	
//	전공	
	private String department_name;	// 부서/전공
	
//	학생 
	private Integer student_number;	// 학번
	private int student_grade;	// 학년
	private StudentType status;	// 학부냐 대학원이냐
	private StudentStatus student_status;	// 재학 상태
	private LocalDateTime enroll_date;	// 입학일자
	private LocalDateTime end_date;		// 졸업일자
	private String tuition_account;		// 등록금 계좌
	
//	교수 
	
	

	
	
	
}
