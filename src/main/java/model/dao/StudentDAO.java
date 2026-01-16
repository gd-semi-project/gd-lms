package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
	public StudentsDTO findStudentByUserId(int userId) {
		StudentsDTO student = new StudentsDTO();
		
		String sql = "SELECT * FROM student WHERE user_id = ?";
		
		try (Connection conn = DBConnection.getConnection())
		{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				student.setStudentNumber(rs.getInt("student_number"));
				student.setStudenGrade(rs.getInt("student_grade"));
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

}
