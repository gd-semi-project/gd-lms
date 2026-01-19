package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;
import model.dto.InstructorDTO;

public class ProfessorDAO {

	private static final ProfessorDAO instance = new ProfessorDAO();

	private ProfessorDAO() {
	}

	public static ProfessorDAO getInstance() {
		return instance;
	}

	// 교수 기본 정보 조회
	public InstructorDTO selectProfessorInfo(Long userId) {
		InstructorDTO professor = new InstructorDTO();
		String sql = "SELECT * FROM professors WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, userId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				professor.setUserId(rs.getLong("user_id"));
				professor.setInstructorNo("instructor_num");
				professor.setDepartment(rs.getString("department"));
				professor.setOfficeRoom(rs.getString("office_room"));
				professor.setOfficePhone(rs.getString("office_phone"));
				professor.setHireDate(rs.getDate("hire_date") != null ? rs.getDate("hire_date").toLocalDate() : null);
				professor.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				professor.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
				return professor;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
