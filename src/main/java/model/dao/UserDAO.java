package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;
import model.dto.UserDTO;
import model.enumtype.Role;
import model.enumtype.Status;
import model.enumtype.YesOrNo;

public class UserDAO {
	private static final UserDAO instance = new UserDAO(); 
	
	private UserDAO() {
	}
	
	public static UserDAO getInstance() {
		return instance;
	}
	
	public UserDTO SelectUsersById(String id) {
		String sql = "SELECT * FROM users WHER id = ?";
		UserDTO userDTO = new UserDTO();
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) {
				userDTO.setLogin_id(rs.getString("login_id"));
				userDTO.setPassword(rs.getString("password_hash"));
				userDTO.setName(rs.getString("name"));
				userDTO.setBirth_date(rs.getDate("birth_date").toLocalDate());
				userDTO.setEmail(rs.getString("email"));
				userDTO.setPhone(rs.getString("phone"));
				
				String roleStr = rs.getString("role");
				userDTO.setRole(Role.valueOf(roleStr));
				
				String statusStr = rs.getString("status"); 
				userDTO.setStatus(Status.valueOf(statusStr));
				
				String mustChangPwStr = rs.getString("must_change_pw"); 
				userDTO.setMustChangePw(YesOrNo.valueOf(mustChangPwStr));
				
				userDTO.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
				userDTO.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());
			}
			return userDTO;
		} catch (SQLException | ClassNotFoundException e) {
			// 예외처리 구문 작성 필요
		}
		return null;
	}
}
