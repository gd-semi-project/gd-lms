package service;

import java.time.LocalDateTime;

import lombok.NoArgsConstructor;
import model.dao.TokenDAO;
import model.dao.UserDAO;
import model.dto.AccessDTO;
import model.dto.ResetToken;
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
		Long userId = userDAO.selectUserIdByEmailAndBirthDate(email, birthDate);
		System.out.println(userId);
        return userId;
    }
	
	public String getPlainToken(Long userId, String token_type, String ip) {
		TokenDAO tokenDAO = TokenDAO.getInstance();
        return tokenDAO.createToken(userId, token_type, ip);
    }
	
	public void issueTempPassword(Long userId, String encryptedTempPassword) {
		UserDAO userDAO = UserDAO.getInstance();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID");
        }

        int updated = userDAO.updateTempPassword(userId, encryptedTempPassword);

        if (updated != 1) {
            throw new RuntimeException("임시 비밀번호 발급 실패");
        }
    }
	
	public boolean verifyResetToken(Long userId, String sessionToken) {

	    TokenDAO tokenDAO = TokenDAO.getInstance();

	    ResetToken rt = tokenDAO.findValidToken(userId, sessionToken);

	    if (rt == null) {
	        return false;
	    }

	    // 이미 사용된 토큰
	    if (rt.getVerifiedAt() != null) {
	        // 이미 사용됨 → null 반환
	        return false;
	    }

	    // 만료 체크
	    if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
	        return false;
	    }

	    // 사용 처리 (1회성)
	    int updated = tokenDAO.markTokenAsUsed(sessionToken);
	    return updated == 1;
	}
}
