package model.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.Role;

@Data
@NoArgsConstructor
public class AccessDTO {
	private Long userId;
	private String name;
	private Role role;
}