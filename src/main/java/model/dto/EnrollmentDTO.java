package model.dto;



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
	private Long enrollmentId;		//수강신청 테이블의 PK
	private Long lectureId;		//강의 테이블의 PK
	private Long studentId;		//학생 테이블의 PK
	private EnrollmentStatus status;
}

