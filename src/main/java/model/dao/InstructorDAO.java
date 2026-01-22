package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import database.DBConnection;
import model.dto.InstructorDTO;

public class InstructorDAO {

    private static InstructorDAO instance = new InstructorDAO();

    private InstructorDAO() {}

    public static InstructorDAO getInstance() {
        return instance;
    }

    // user_id(FK)을 통해서 강사 테이블 조회
    public InstructorDTO selectInstructorInfo(long userId) {

        InstructorDTO instructor = new InstructorDTO();

        String sql = """
            SELECT
                user_id,
                instructor_no,
                department,
                office_room,
                office_phone,
                hire_date,
                created_at,
                updated_at
            FROM instructor
            WHERE user_id = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                instructor.setUserId(rs.getLong("user_id"));
                instructor.setInstructorNo(rs.getString("instructor_no"));
                instructor.setDepartment(rs.getString("department"));
                instructor.setOfficeRoom(rs.getString("office_room"));
                instructor.setOfficePhone(rs.getString("office_phone"));
                instructor.setHireDate(
                    rs.getDate("hire_date") != null
                        ? rs.getDate("hire_date").toLocalDate()
                        : null
                );
                instructor.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                instructor.setUpdatedAt(
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return instructor;
    }

	public static ArrayList<InstructorDTO> getAllInstructorByDepartment(Long departmentId, String status) {
		ArrayList<InstructorDTO> list = new ArrayList<InstructorDTO>();
		if (departmentId == null) return list;
		
		String statusCheck = (status == null || status.isBlank()) ? "ACTIVE" : status.trim().toUpperCase();

		final String sql = """
				SELECT
					i.user_id,
		            i.instructor_no,
		            i.department_id,
		            i.office_room,
		            i.office_phone,
		            i.hire_date,
		            u.login_id,
		            u.name,
		            u.email,
		            u.phone,
		            u.role,
		            u.status
		        FROM instructor i
		        JOIN user u ON u.user_id = i.user_id
		        WHERE i.department_id = ?
		          AND u.role = 'INSTRUCTOR'
		          AND u.status = ?
		        ORDER BY u.name ASC, i.instructor_no ASC
				""";
		
		try (
				Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)
				) {
			
			pstmt.setLong(1, departmentId);
			pstmt.setString(2, statusCheck);
	
			try (ResultSet rs = pstmt.executeQuery()){
				
				while (rs.next()) {
					InstructorDTO dto = new InstructorDTO();
					
					dto.setUserId(rs.getLong("user_id"));
					dto.setInstructorNo(rs.getString("instructor_no"));
					dto.setDepartmentId(rs.getLong("department_id"));
					dto.setOfficeRoom(rs.getString("office_room"));
					dto.setOfficePhone(rs.getString("office_phone"));
					
					dto.setHireDate(rs.getDate("hire_date").toLocalDate());
					
					dto.setName(rs.getString("name"));
					dto.setEmail(rs.getString("email"));
					dto.setPhone(rs.getString("phone"));
					
					list.add(dto);
				}
			}
			
		} catch (Exception e) {
			System.out.println("getAllInstructorByDepartment(): 실패");
		}
		
		return list;
	}
}
