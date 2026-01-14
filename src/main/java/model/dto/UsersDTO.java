package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import model.enumtype.Role;
import model.enumtype.Status;

@Data
public class UsersDTO {
	private int user_id;
	private String login_id;
	private String password;
	private String name;
	private String email;
	private String phone;
	private Role role;
	private Status status;
	private LocalDateTime created_at;
	private LocalDateTime updated_atd;
}
