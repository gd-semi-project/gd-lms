package service;

import lombok.NoArgsConstructor;
import model.dao.UserDAO;
import model.dto.UserDTO;

@NoArgsConstructor
public class LoginService {
	private static final LoginService instance = new LoginService();
	
	public static LoginService getInstance() {
		return instance;
	}
	
	public UserDTO DoLogin(String id, String passwd) {
		if (id != null && id.isEmpty() && passwd != null && passwd.isEmpty()) {
			UserDAO userDAO = UserDAO.getInstance();
			UserDTO userDTO = userDAO.SelectUsersById(id);
			
			if (id.equals(userDTO.getLogin_id())) {
				if (passwd.equals(userDTO.getPassword())) {
					return userDTO;
				}
			}
		}
		return null;
	}
}
