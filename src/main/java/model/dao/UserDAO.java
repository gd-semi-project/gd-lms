package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;
import model.dto.UserDTO;
import model.enumtype.Gender;
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
	
	public UserDTO SelectUsersById(String Id) {
		String sql = "SELECT * FROM users WHERE login_id = ?";
		UserDTO userDTO = new UserDTO();
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Id);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) {
				userDTO.setUserId(rs.getInt("user_id"));
				userDTO.setLoginId(rs.getString("login_id"));
				userDTO.setPassword(rs.getString("password_hash"));
				userDTO.setName(rs.getString("name"));
				
				String genderStr = rs.getString("gender"); 
				userDTO.setGender(Gender.valueOf(genderStr));
				
				userDTO.setBirthDate(rs.getDate("birth_date").toLocalDate());
				userDTO.setEmail(rs.getString("email"));
				userDTO.setPhone(rs.getString("phone"));
				userDTO.setAddress(rs.getString("address"));
				
				String roleStr = rs.getString("role");
				userDTO.setRole(Role.valueOf(roleStr));
				
				String statusStr = rs.getString("status"); 
				userDTO.setStatus(Status.valueOf(statusStr));
				
				userDTO.setMustChangePw(rs.getBoolean("must_change_pw"));
				
				userDTO.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				userDTO.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
			}
			return userDTO;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println("UserDAO selectuserbyid" + e.getMessage());
		}
		return null;
	}
	
	public void InsertUser(UserDTO userDTO) {
		String sql = "INSERT INTO users (login_id, password_hash, name, birth_date, email, phone, role)"
				+ " VALUES (?,?,?,?,?,?,?)";
				
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
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println(e.getMessage() + "111111");
		}
		
	}
}