package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.StudentStatus;
import model.enumtype.StudentType;

@Data
@NoArgsConstructor
public class StudentsDTO {
	private Long studentId;
	private Long departmentId;
	private Long userId;
	
	private Integer studentNumber;	// 학번
	private int studenGrade;	// 학년
	private StudentType status;	// 학부냐 대학원이냐
	private StudentStatus studentStatus;	// 재학 상태
	private LocalDateTime enrollDate;	// 입학일자
	private LocalDateTime endDate;		// 졸업일자
	private String tuitionAccount;		// 등록금 계좌
}
