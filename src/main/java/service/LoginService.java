package service;

import lombok.NoArgsConstructor;
import model.dao.UserDAO;
import model.dto.UserDTO;
import utils.HashUtil;

@NoArgsConstructor
public class LoginService {
	private static final LoginService instance = new LoginService();
	
	public static LoginService getInstance() {
		return instance;
	}
	
	public UserDTO DoLogin(String id, String passwd) {
		if (id != null && !id.isEmpty() && passwd != null && !passwd.isEmpty()) {
			UserDAO userDAO = UserDAO.getInstance();
			UserDTO userDTO = userDAO.SelectUsersById(id);
			
			if (userDTO != null) {
				System.out.println(userDTO.getLogin_id());

				if (id.equals(userDTO.getLogin_id())) {
					System.out.println(passwd);
					System.out.println(userDTO.getPassword());
					if (passwd.equals(userDTO.getPassword())) {
						return userDTO;
					}
				}
			}
		}
		return null;
	}
	
	public void RegistUser(UserDTO userDTO) {
		System.out.println("1234");
		if (userDTO != null) {
			UserDAO userDAO = UserDAO.getInstance();
			String password_hash = HashUtil.sha256(userDTO.getPassword());
			userDTO.setPassword(password_hash);
			userDAO.InsertUser(userDTO);
		}
	}
	
}
