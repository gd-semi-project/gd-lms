package service;

import java.sql.SQLException;

import exception.InternalServerException;
import lombok.NoArgsConstructor;
import model.dao.TokenDAO;
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
		try {
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
		} catch (InternalServerException e) {
			throw new InternalServerException("로그인 중 에러가 발생했습니다.", e);
		}
	}
	
	public void RegistUser(UserDTO userDTO) {
		try {
			if (userDTO != null) {
				UserDAO userDAO = UserDAO.getInstance();
				String password_hash = HashUtil.sha256(userDTO.getPassword());
				userDTO.setPassword(password_hash);
				userDAO.InsertUser(userDTO);
			}
		} catch (InternalServerException e) {
			Throwable cause = e.getCause(); // 원래 DAO에서 던진 SQLException
		    if (cause instanceof SQLException) {
		        SQLException sqlEx = (SQLException) cause;
		        
		        if ("23000".equals(sqlEx.getSQLState())) {
		            if (sqlEx.getMessage().contains("email")) {
		            	throw new InternalServerException("이메일 중복입니다.", e);
		            } else if (sqlEx.getMessage().contains("login_id")) {
		            	throw new InternalServerException("로그인 아이디 중복입니다.", e);
		            }
		        } else {
		        	throw new InternalServerException("회원등록 중 오류가 발생했습니다.", e);
		        }
		    }
		}
	}
	
	// 중복확인용 서비스로직(회원등록)
	public boolean DuplicateEmail(String email) {
		try {
			UserDAO userDAO = UserDAO.getInstance();
			return userDAO.selectLoginIdByLoginId(email);	
		} catch (InternalServerException e) {
			throw new InternalServerException("이메일 중복확인 중 오류가 발생했습니다.",e);
		}
	}
	
	public boolean DuplicateLoginId(String loginId) {
		try {
			UserDAO userDAO = UserDAO.getInstance();
			return userDAO.selectLoginIdByLoginId(loginId);
		} catch (InternalServerException e) {
			throw new InternalServerException("이메일 중복확인 중 오류가 발생했습니다.",e);
		}
	}
	
	// 이메일, 생년월일이 일치하는 user 있는지 확인 로직(비밀번호 초기화)
	public boolean verifyUserInfo(String email, String birthDate) {
		try {
			UserDAO userDAO = UserDAO.getInstance();
	        return userDAO.existsByEmailAndBirth(email, birthDate);
		} catch (InternalServerException e) {
			throw new InternalServerException("생년월일 확인 중 오류가 발생했습니다.", e);
		}
    }
	
	// 이메일, 생년월일로 user_id 반환(토큰 생성시 이용)
	public Long getUserId(String email, String birthDate) {
        try {
    		UserDAO userDAO = UserDAO.getInstance();
    		Long userId = userDAO.selectUserIdByEmailAndBirthDate(email, birthDate);
    		System.out.println(userId);
            return userId;
		} catch (InternalServerException e) {
			throw new InternalServerException("사용자 정보 인증 중 오류가 발생했습니다.", e);
		}
    }
	
	public String getPlainToken(Long userId, String token_type, String ip) {
        try {
    		TokenDAO tokenDAO = TokenDAO.getInstance();
            return tokenDAO.createToken(userId, token_type, ip);
		} catch (InternalServerException e) {
			throw new InternalServerException("사용자 정보 인증 중 오류가 발생했습니다.", e);
		}
    }
	
	public void issueTempPassword(Long userId, String encryptedTempPassword) {
        try {
    		UserDAO userDAO = UserDAO.getInstance();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
            }

            int updated = userDAO.updateTempPassword(userId, encryptedTempPassword);

            if (updated != 1) {
                throw new RuntimeException("임시 비밀번호 발급 실패했습니다.");
            }
		} catch (InternalServerException e) {
			throw new InternalServerException("비밀번호 초기화 중 오류가 발생했습니다.", e);
		}
    }
	
	public Long verifyResetToken(String sessionToken) {
		try {
		    TokenDAO tokenDAO = TokenDAO.getInstance();

		    Long userId = tokenDAO.getUserIdByToken(sessionToken);

		    if (userId == null) {
		        return null;
		    }
		    return userId;
		} catch (InternalServerException e) {
			throw new InternalServerException("사용자 정보 인증 중 오류가 발생했습니다.", e);
		}
	}
	
	public Long getUserIdByToken (String token) {
		try {
			TokenDAO tokenDAO = TokenDAO.getInstance();
			return tokenDAO.getUserIdByToken(token);
		} catch (InternalServerException e) {
			throw new InternalServerException("사용자 정보 인증 중 오류가 발생했습니다.", e);
		}
	}
	
	public void markTokenAsUsed (String token) {
		try {
			TokenDAO tokenDAO = TokenDAO.getInstance();
			tokenDAO.markTokenAsUsed(token);
		} catch (InternalServerException e) {
			throw new InternalServerException("사용자 정보 인증 중 오류가 발생했습니다.", e);
		}
	}
}