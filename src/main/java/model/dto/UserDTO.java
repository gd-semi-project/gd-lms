package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.Gender;
import model.enumtype.Role;
import model.enumtype.Status;
import model.enumtype.YesOrNo;

@Data
@NoArgsConstructor
public class UserDTO {
	private long user_id;
	private String login_id;
	private String password;
	private String name;
	private Gender gender;
	private LocalDate birth_date;
	private String email;
	private String phone;
	private String address;
	private Role role;
	private Status status;
	private boolean mustChangePw;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
}