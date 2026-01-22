package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstructorDTO {

	private Long instructorId;   // PK (또는 user_id)
	private Long userId;         // FK → user.user_id
	private String instructorNo;
	private Long departmentId;
	private String officeRoom;
	private String officePhone;
	private LocalDate hireDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	
	// 조회용
	private String name;     // user.name
	private String email;    // user.email
	private String phone;    // user.phone
	private String department; // department.name
	
}