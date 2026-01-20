package service;

import lombok.NoArgsConstructor;
import model.dao.UserDAO;
import model.dto.AccessDTO;
import model.dto.UserDTO;
import utils.HashUtil;

@NoArgsConstructor
public class LoginService {
	private static final LoginService instance = new LoginService();
	
	public static LoginService getInstance() {
		return instance;
	}
	
	public AccessDTO DoLogin(String id, String passwd) {
		if (id != null && !id.isEmpty() && passwd != null && !passwd.isEmpty()) {
			UserDAO userDAO = UserDAO.getInstance();
			AccessDTO accessDTO = userDAO.selectAccessById(id);
			UserDTO userDTO = userDAO.selectUsersById(id);
			
			if (userDTO != null) {
				if (id.equals(userDTO.getLoginId())) {
					if (passwd.equals(userDTO.getPassword())) {
						return accessDTO;
					}
				}
			}
		}
		return null;
	}
	
	public void RegistUser(UserDTO userDTO) {
		if (userDTO != null) {
			UserDAO userDAO = UserDAO.getInstance();
			String password_hash = HashUtil.sha256(userDTO.getPassword());
			userDTO.setPassword(password_hash);
			userDAO.InsertUser(userDTO);
		}
	}
	
}
