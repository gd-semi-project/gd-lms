package model.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import model.enumtype.EnrollmentStatus;

@NoArgsConstructor
@ToString
@AllArgsConstructor
@Data
public class EnrollmentDTO {
	private int enroll_id;		//수강신청 테이블의 PK
	private int course_id;		//강의 테이블의 PK
	private int student_id;		//학생 테이블의 PK
	private EnrollmentStatus status;
	private LocalDateTime applied_at;
}

