package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.Gender;
import model.enumtype.Role;
import model.enumtype.UserStatus;

@Data
@NoArgsConstructor
public class UserDTO {
	private Long userId;
	private String loginId;
	private String password;
	private String name;
	private Gender gender;
	private LocalDate birthDate;
	private String email;
	private String phone;
	private String address;
	private Role role;
	private UserStatus status;
	private boolean mustChangePw;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int loginTryCount;
}