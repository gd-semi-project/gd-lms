package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import database.DBConnection;
import model.dto.StudentDTO;
import model.enumtype.StudentStatus;
import model.enumtype.StudentType;

public class StudentDAO {
	private static StudentDAO instance = new StudentDAO();

	private StudentDAO() {}
	
	// 객체 변수 instance에 대한 Getter 메소드 작성
	public static StudentDAO getInstance() {
		return instance;
	}
	
	// user_id(FK)을 통해서 학생테이블을 가져옴
	public StudentDTO findStudentByUserId(long userId) {
		StudentDTO student = new StudentDTO();
		
		String sql = "SELECT * FROM student WHERE user_id = ?";
		
		try (Connection conn = DBConnection.getConnection())
		{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, userId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				student.setStudentNumber(rs.getInt("student_number"));
				student.setStudentGrade(rs.getInt("student_grade"));
				student.setStatus(StudentType.valueOf(rs.getString("status")));
				student.setStudentStatus(StudentStatus.valueOf(rs.getString("student_status")));
				student.setEnrollDate(rs.getTimestamp("enroll_date").toLocalDateTime());
				student.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
				student.setTuitionAccount(rs.getString("tuition_account"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return student;
		
	}

	public static ArrayList<StudentDTO> getAllStudentByDepartment(Long departmentId, String status) {
		
		ArrayList<StudentDTO> list = new ArrayList<StudentDTO>();
		
		if (departmentId == null) return list;
		
		String statusCheck = (status == null || status.isBlank()) ? "ACTIVE" : status.trim().toUpperCase();

		String sql ="""
				
				SELECT
		            s.student_id,
		            s.department_id,
		            s.user_id,
		            s.student_number,
		            s.student_grade,
		            s.status          AS academic_status,
		            s.enroll_date,
		            s.end_date,
		            s.tuition_account,
	
		            u.name,
		            u.email,
		            u.phone,
		        FROM student s
		        JOIN user u ON u.user_id = s.user_id
		        WHERE s.department_id = ?
		          AND u.role = 'STUDENT'
		          AND u.status = ?
		        ORDER BY s.student_grade ASC, s.student_number ASC
		        
				 """;
		
		try (
				Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				){
			
			pstmt.setLong(1, departmentId);
			pstmt.setString(2, statusCheck);
			
			try(ResultSet rs = pstmt.executeQuery()){
				
				while (rs.next()) {
					
					StudentDTO dto = new StudentDTO();
					
					dto.setStudentId(rs.getLong("student_id"));
					dto.setDepartmentId(rs.getLong("department_id"));
					dto.setUserId(rs.getLong("user_id"));
					dto.setStudentNumber(rs.getInt("student_number"));
					
					int grade = rs.getInt("student_grade");
					dto.setStudentGrade(rs.wasNull() ? null : grade);
					
					dto.setStatus(StudentType.valueOf(rs.getString("academic_status")));
					dto.setStudentStatus(StudentStatus.valueOf(rs.getString("student_status")));
					
	                Timestamp enrollTs = rs.getTimestamp("enroll_date");
	                dto.setEnrollDate(enrollTs == null ? null : enrollTs.toLocalDateTime());

	                Timestamp endTs = rs.getTimestamp("end_date");
	                dto.setEndDate(endTs == null ? null : endTs.toLocalDateTime());
					
	                dto.setName(rs.getString("name"));
	                dto.setEmail(rs.getString("email"));
	                dto.setPhone(rs.getString("phone"));
	                
	                list.add(dto);
				}
			}
			
			return list;
			
		} catch (Exception e) {
			System.out.println("getAllStudentByDepartment(): 실패");
		}
		
		return null;
	}

}
