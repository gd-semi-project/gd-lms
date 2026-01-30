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

    // 강사 테이블 조회
    public InstructorDTO selectInstructorInfo(long userId) {

        InstructorDTO instructor = null;

        String sql = """
            SELECT
			    i.user_id,
			    i.instructor_no,
			    i.department_id,
			    i.office_room,
			    i.office_phone,
			    i.hire_date,
			    i.created_at,
			    i.updated_at,
			
			    u.name,
			    u.email,
			    u.phone,
			    d.department_name
			FROM instructor i
			JOIN user u
			  ON u.user_id = i.user_id
			LEFT JOIN department d
			  ON d.department_id = i.department_id
			WHERE i.user_id = ?
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    instructor = new InstructorDTO();

                    // instructor
                    instructor.setUserId(rs.getLong("user_id"));
                    instructor.setInstructorNo(rs.getString("instructor_no"));
                    instructor.setDepartmentId(rs.getLong("department_id"));
                    instructor.setOfficeRoom(rs.getString("office_room"));
                    instructor.setOfficePhone(rs.getString("office_phone"));

                    instructor.setHireDate(
                        rs.getDate("hire_date") != null
                            ? rs.getDate("hire_date").toLocalDate()
                            : null
                    );

                    instructor.setCreatedAt(
                        rs.getTimestamp("created_at") != null
                            ? rs.getTimestamp("created_at").toLocalDateTime()
                            : null
                    );

                    instructor.setUpdatedAt(
                        rs.getTimestamp("updated_at") != null
                            ? rs.getTimestamp("updated_at").toLocalDateTime()
                            : null
                    );

                    // user
                    instructor.setName(rs.getString("name"));
                    instructor.setEmail(rs.getString("email"));
                    instructor.setPhone(rs.getString("phone"));

                    // department
                    instructor.setDepartment(rs.getString("department_name"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return instructor;
    }
    
    
    public void updateInstructorUserInfo(Long userId, String name, String email, String phone) {

        String sql = """
            UPDATE user
            SET name = ?, email = ?, phone = ?
            WHERE user_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setLong(4, userId);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateInstructorOfficeInfo(Long userId, String officeRoom, String officePhone) {

        String sql = """
            UPDATE instructor
            SET office_room = ?, office_phone = ?
            WHERE user_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, officeRoom);
            pstmt.setString(2, officePhone);
            pstmt.setLong(3, userId);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    // admin
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
