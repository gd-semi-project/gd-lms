package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.mysql.cj.xdevapi.Result;

import database.DBConnection;
import model.dto.StudentsDTO;
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
	public StudentsDTO findStudentByLoginId(String loginId) {
		System.out.println("🔥 StudentDAO 진입");

		String sql = """
		        SELECT s.*
		        FROM student s
		        JOIN `user` u ON s.user_id = u.user_id
		        WHERE u.login_id = ?
		    """;
		
		try (Connection conn = DBConnection.getConnection())
		{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loginId);
			
			ResultSet rs = pstmt.executeQuery();
			

			if (rs.next()) {
				System.out.println("🔥 student rs.next() = true");
			    StudentsDTO student = new StudentsDTO();

			    student.setStudentId(rs.getLong("student_id"));
			    student.setUserId(rs.getLong("user_id"));
			    student.setDepartmentId(rs.getLong("department_id"));

			    student.setStudentNumber(rs.getInt("student_number"));
			    student.setStudenGrade(rs.getInt("student_grade"));
			    student.setStatus(StudentType.fromLabel(rs.getString("status")));
			    student.setStudentStatus( StudentStatus.fromLabel(rs.getString("student_status")));

			    Timestamp enrollTs = rs.getTimestamp("enroll_date");
			    if (enrollTs != null)
			        student.setEnrollDate(enrollTs.toLocalDateTime());

			    Timestamp endTs = rs.getTimestamp("end_date");
			    if (endTs != null)
			        student.setEndDate(endTs.toLocalDateTime());

			    student.setTuitionAccount(rs.getString("tuition_account"));
			    System.out.println("🔥 status enum = " + student.getStatus());
			    System.out.println("🔥 studentStatus enum = " + student.getStudentStatus());
			    return student;
			}
			}	
		 catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
