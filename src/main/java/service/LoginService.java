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
			
			// 입력값 검증 필요
			// 이메일 중복 여부, 로그인아이디 중복 여부
			if (DuplicateEmail(userDTO.getEmail())) {
				throw new RuntimeException("이메일 중복입니다..");
			}
			
			if (DuplicateLoginId(userDTO.getLoginId())) {
				throw new RuntimeException("로그인 아이디 중복입니다.");
			}
			
			userDAO.InsertUser(userDTO);
		}
	}
	
	// 중복확인용 서비스로직(회원등록)
	public boolean DuplicateEmail(String email) {
		UserDAO userDAO = UserDAO.getInstance();
		return userDAO.selectLoginIdByLoginId(email);
	}
	
	public boolean DuplicateLoginId(String loginId) {
		UserDAO userDAO = UserDAO.getInstance();
		return userDAO.selectLoginIdByLoginId(loginId);
	}
	
	// 이메일, 생년월일이 일치하는 user 있는지 확인 로직(비밀번호 초기화)
	public boolean verifyUserInfo(String email, String birthDate) {
		UserDAO userDAO = UserDAO.getInstance();
        return userDAO.existsByEmailAndBirth(email, birthDate);
    }
	
	// 이메일, 생년월일로 user_id 반환(토큰 생성시 이용)
	public Long getUserId(String email, String birthDate) {
		UserDAO userDAO = UserDAO.getInstance();
        return userDAO.selectUserIdByEmailAndBirthDate(email, birthDate);
    }
}
