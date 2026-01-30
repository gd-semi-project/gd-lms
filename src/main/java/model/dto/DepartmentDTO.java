package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepartmentDTO {
	private Long departmentId;
	private Long collegeId;
	private String departmentName;	// 부서/전공
	private int annualQuota;
	private String departmentCode;
}
