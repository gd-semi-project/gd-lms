package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
	
	public UserDTO SelectUsersById(String id) {
		String sql = "SELECT * FROM users WHERE login_id = ?";
		UserDTO userDTO = new UserDTO();
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) {
				userDTO.setLogin_id(rs.getString("login_id"));
				userDTO.setPassword(rs.getString("password_hash"));
				userDTO.setName(rs.getString("name"));
				
				String genderStr = rs.getString("gender");
				if (genderStr != null) {
				    userDTO.setGender(Gender.valueOf(genderStr));
				}

				Date birthDate = rs.getDate("birth_date");
				if (birthDate != null) {
				    userDTO.setBirth_date(birthDate.toLocalDate());
				}

				userDTO.setEmail(rs.getString("email"));
				userDTO.setPhone(rs.getString("phone"));
				userDTO.setAddress(rs.getString("address"));
				
				String roleStr = rs.getString("role");
				userDTO.setRole(Role.valueOf(roleStr));
				
				String statusStr = rs.getString("status"); 
				userDTO.setStatus(Status.valueOf(statusStr));
				
				userDTO.setMustChangePw(rs.getBoolean("must_change_pw"));
				
				userDTO.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());

				Timestamp updatedAt = rs.getTimestamp("updated_at");
				if (updatedAt != null) {
				    userDTO.setUpdated_at(updatedAt.toLocalDateTime());
				}
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
			pstmt.setString(1, userDTO.getLogin_id());
			pstmt.setString(2, userDTO.getPassword());
			pstmt.setString(3, userDTO.getName());
			pstmt.setObject(4, userDTO.getBirth_date());
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