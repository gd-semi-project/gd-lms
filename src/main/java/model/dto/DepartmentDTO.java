package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepartmentDTO {
	private Long departmentId;
	private String departmentName;	// 부서/전공
}
