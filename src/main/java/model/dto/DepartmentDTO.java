package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepartmentDTO {
	private long departmentId;
	private String departmentName;	// 부서/전공
}
