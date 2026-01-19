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
	private Long enrollmentId;
	private Long lectureId;
	private Long studentId;
	private EnrollmentStatus status;
}

