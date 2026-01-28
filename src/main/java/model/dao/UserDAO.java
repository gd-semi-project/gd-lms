package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import database.DBConnection;
import exception.InternalServerException;
import model.dto.AccessDTO;
import model.dto.UserDTO;
import model.enumtype.Gender;
import model.enumtype.Role;
import model.enumtype.UserStatus;

public class UserDAO {
	private static final UserDAO instance = new UserDAO(); 
	
	private UserDAO() {
	}
	
	public static UserDAO getInstance() {
		return instance;
	}
	
	public UserDTO selectUsersById(String Id) {
		String sql = "SELECT * FROM user WHERE login_id = ?";
		UserDTO userDTO = new UserDTO();
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Id);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) {
				userDTO.setUserId(rs.getLong("user_id"));
				userDTO.setLoginId(rs.getString("login_id"));
				userDTO.setPassword(rs.getString("password_hash"));
				userDTO.setName(rs.getString("name"));
				
				String genderStr = rs.getString("gender");
				if (genderStr != null) {
				    userDTO.setGender(Gender.valueOf(genderStr));
				}

				Date birthDate = rs.getDate("birth_date");
				if (birthDate != null) {
				    userDTO.setBirthDate(birthDate.toLocalDate());
				}
				
				userDTO.setEmail(rs.getString("email"));
				userDTO.setPhone(rs.getString("phone"));
				userDTO.setAddress(rs.getString("address"));
				
				String roleStr = rs.getString("role");
				userDTO.setRole(Role.valueOf(roleStr));
				
				String statusStr = rs.getString("status"); 
				userDTO.setStatus(UserStatus.valueOf(statusStr));
				
				userDTO.setMustChangePw(rs.getBoolean("must_change_pw"));
				
				userDTO.setLoginTryCount(rs.getInt("login_try_count"));
				

				userDTO.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

				Timestamp updatedAt = rs.getTimestamp("updated_at");
				if (updatedAt != null) {
				    userDTO.setUpdatedAt(updatedAt.toLocalDateTime());
				}
			}
			return userDTO;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public AccessDTO selectAccessById(String Id) {
		String sql = "SELECT * FROM user WHERE login_id = ?";
		AccessDTO accessDTO = new AccessDTO();
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Id);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) {
				accessDTO.setUserId(rs.getLong("user_id"));
				accessDTO.setName(rs.getString("name"));
				
				String roleStr = rs.getString("role");
				accessDTO.setRole(Role.valueOf(roleStr));
			}
			return accessDTO;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public UserStatus selectStatusByUserId(Long userId) {
		String sql = """
				SELECT status
				FROM user
				WHERE user_id = ?
				""";
		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, userId);
			ResultSet rs = pstmt.executeQuery();
			
			UserStatus userStatus = null;
			if (rs.next()) {
				userStatus = UserStatus.valueOf(rs.getString("status"));
			}
			return userStatus;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public int updateLoginTryCount(String id) {
		String sql = """
				UPDATE user
				SET
				    login_try_count = login_try_count + 1,
				    status = CASE
				        WHEN login_try_count + 1 >= 5 THEN 'LOCKED'
				        ELSE status
				    END
				WHERE login_id = ?
				  AND login_try_count < 5
				  AND status = 'ACTIVE'
				""";
		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			int updated = pstmt.executeUpdate();
			
			return updated;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public int updateLoginStatusACTIVEByLoginID(String id) {
		String sql = """
				UPDATE user
				SET status = 'ACTIVE', login_try_count = 0
				WHERE login_id = ?
				""";
		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			int updated = pstmt.executeUpdate();
			
			return updated;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public int updateLoginStatusACTIVEByUserID(Long userId) {
		String sql = """
				UPDATE user
				SET status = 'ACTIVE', login_try_count = 0
				WHERE user_id = ?
				""";
		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, userId);
			int updated = pstmt.executeUpdate();
			
			return updated;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public int updateLoginStatusINACTIVE(String id) {
		String sql = """
				UPDATE user
				SET status = 'INACTIVE'
				WHERE login_id = ?
				""";
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			int updated = pstmt.executeUpdate();
			
			return updated;
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	public void InsertUser(UserDTO userDTO) {
		String sql = """
					INSERT INTO user (login_id, password_hash, name, birth_date, email, phone, role)
					VALUES (?,?,?,?,?,?,?)
				""";
				
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userDTO.getLoginId());
			pstmt.setString(2, userDTO.getPassword());
			pstmt.setString(3, userDTO.getName());
			pstmt.setObject(4, userDTO.getBirthDate());
			pstmt.setString(5, userDTO.getEmail());
			pstmt.setString(6, userDTO.getPhone());
			pstmt.setString(7, userDTO.getRole().toString());
			pstmt.executeUpdate();
		}  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}

	// 유저 기본정보 수정
	public void updateUserInfo(UserDTO userDTO) {
		String sql = "UPDATE user SET birth_date = ?, email = ?, phone = ?, address = ? WHERE login_id = ?";
		
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, userDTO.getBirthDate());
			pstmt.setString(2, userDTO.getEmail());
			pstmt.setString(3, userDTO.getPhone());
			pstmt.setObject(4, userDTO.getAddress());
			pstmt.setString(5, userDTO.getLoginId());
			pstmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
	}
	
	// 비밀번호 변경
	public void updatePassword(String loginId, String passwordHash) {
	    String sql = "UPDATE user SET password_hash = ? WHERE login_id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	    	PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.setString(1, passwordHash);
           pstmt.setString(2, loginId);
           pstmt.executeUpdate();
       } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
       }
	}

	// 강사 프로필 정보 : 지윤
	public UserDTO selectUserByUserId(Long userId) {
	    String sql = "SELECT * FROM user WHERE user_id = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setLong(1, userId);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            UserDTO userDTO = new UserDTO();
	            userDTO.setUserId(rs.getLong("user_id"));
	            userDTO.setLoginId(rs.getString("login_id"));
	            userDTO.setName(rs.getString("name"));
	            userDTO.setEmail(rs.getString("email"));
	            userDTO.setRole(Role.valueOf(rs.getString("role")));
	            // 필요한 필드만
	            return userDTO;
	        }
	        return null;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }

	}
	
	public String selectLoginIdByUserId(Long userId) {
		String sql = "SELECT login_id FROM user WHERE user_id = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setLong(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	return rs.getString("login_id");
	        }
	    } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
        return null;
	}
	
	public boolean selectLoginIdByLoginId(String loginId) {
		String sql = "SELECT 1 FROM user WHERE login_id = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, loginId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	return true;
	        }
	    } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
        return false;
	}
	
	public boolean selectEmailByEmail(String email) {
		String sql = "SELECT 1 FROM user WHERE email = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	return true;
	        }
	    } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
        return false;
	}
	
	// 비밀번호 초기화시 이메일 + 생년월일 맞는지 확인하는 로직
	public boolean existsByEmailAndBirth(String email, String birthDate) {
        String sql = "SELECT 1 FROM user WHERE email = ? AND birth_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, birthDate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
    }
	public Long selectUserIdByEmailAndBirthDate(String email, String birthDate) {
		String sql = "SELECT user_id FROM user WHERE email = ? AND birth_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, birthDate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("user_id");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
        return Long.getLong("0");
	}
	
	public int updateTempPassword(Long userId, String encryptedTempPassword) {
        String sql = """
            UPDATE user
               SET password_hash = ?,
                   must_change_pw = '1',
                   updated_at = NOW()
             WHERE user_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encryptedTempPassword);
            pstmt.setLong(2, userId);

            return pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
    }
	
}