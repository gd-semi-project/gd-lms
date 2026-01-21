package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			    return student;
			}
			}	
		 catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	// 학생정보 수정
	public void updateStudentInfo(StudentsDTO studentsDTO, String loginId) {
		String sql = "UPDATE student s "
				+ "Join user u On s.user_id = u.user_id "
				+ "Set s.tuition_account = ? "
				+ "WHERE u.login_id = ?";
		
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, studentsDTO.getTuitionAccount());
			pstmt.setString(2, loginId);
			pstmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println(e.getMessage() + "111111");
		}
	}

}
