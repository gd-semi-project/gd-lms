package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import model.enumtype.StudentStatus;
import model.enumtype.StudentType;

@Data
public class StudentsDto {
	private int student_id;
	private int department_id;
	private int user_id;
	
	private Integer student_number;	// 학번
	private int student_grade;	// 학년
	private StudentType status;	// 학부냐 대학원이냐
	private StudentStatus student_status;	// 재학 상태
	private LocalDateTime enroll_date;	// 입학일자
	private LocalDateTime end_date;		// 졸업일자
	private String tuition_account;		// 등록금 계좌
}
