package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.dto.ProfessorDTO;

public class ProfessorDAO {

	private static final ProfessorDAO instance = new ProfessorDAO();

	private ProfessorDAO() {
	}

	public static ProfessorDAO getInstance() {
		return instance;
	}

	// 교수 기본 정보 조회
	public ProfessorDTO selectProfessorInfo(Connection conn, int userId) throws SQLException {

		String sql = """
				    SELECT
				        user_id,
				        employee_no,
				        department,
				        office_room,
				        office_phone,
				        hire_date,
				        created_at,
				        updated_at
				    FROM professors
				    WHERE user_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next())
					return null;

				ProfessorDTO professor = new ProfessorDTO();
				professor.setUserId(rs.getInt("user_id"));
				professor.setEmployeeNo(rs.getString("employee_no"));
				professor.setDepartment(rs.getString("department"));
				professor.setOfficeRoom(rs.getString("office_room"));
				professor.setOfficePhone(rs.getString("office_phone"));
				professor.setHireDate(rs.getDate("hire_date") != null ? rs.getDate("hire_date").toLocalDate() : null);
				professor.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				professor.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

				return professor;
			}
		}
	}
}
