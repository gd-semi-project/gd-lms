package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;
import model.dto.UserDTO;
import model.enumtype.Role;
import model.enumtype.Status;

public class UserDAO {
	private static final UserDAO instance = new UserDAO(); 
	
	private UserDAO() {
	}
	
	public static UserDAO getInstance() {
		return instance;
	}
	
	private UserDTO SelectUsersById(String id) {
		String sql = "SELECT * FROM users WHER id = ?";
		UserDTO user = new UserDTO();
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) {
				user.setLogin_id(rs.getString("login_id"));
				user.setPassword(rs.getString("password_hash"));
				user.setName(rs.getString("name"));
				user.setBirth_date(rs.getDate("birth_date").toLocalDate());
				user.setEmail(rs.getString("email"));
				user.setPhone(rs.getString("phone"));
				
				String roleStr = rs.getString("role");
				user.setRole(Role.valueOf(roleStr));
				
				String statusStr = rs.getString("status"); 
				user.setStatus(Status.valueOf(statusStr));
				
				String mustChangPwStr = rs.getString("must_change_pw"); 
				user.setmust(Status.valueOf(mustChangPwStr)));
			}
		} catch (SQLException | ClassNotFoundException e) {
			// 예외처리 구문 작성 필요
		}
		
		
		return user;
	}
}
