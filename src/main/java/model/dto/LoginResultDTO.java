package model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.LoginStatus;

@Data
@NoArgsConstructor
public class LoginResultDTO {
	private LoginStatus loginStatus;
	private AccessDTO accessDTO;
}