package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.Result;

import database.DBConnection;
import model.dto.LectureDTO;
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
	public StudentsDTO findStudentByUserId(long userId) {
		StudentsDTO student = new StudentsDTO();
		
		String sql = "SELECT * FROM student WHERE user_id = ?";
		
		try (Connection conn = DBConnection.getConnection())
		{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, userId);
			
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
	
	
	public List<LectureDTO> selectMyLectures(Connection conn, Long userId)
	        throws SQLException {

	    String sql = """
	        SELECT
	            l.lecture_id,
	            l.lecture_title,
	            l.lecture_round,
	            l.section,
	            l.start_date,
	            l.end_date,
	            l.room
	        FROM enrollment e
	        JOIN lecture l ON e.lecture_id = l.lecture_id
	        JOIN student s ON e.student_id = s.student_id
	        WHERE s.user_id = ?
	          AND e.status = 'ENROLLED'
	        ORDER BY l.start_date
	    """;

	    List<LectureDTO> list = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setLong(1, userId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                LectureDTO dto = new LectureDTO();
	                dto.setLectureId(rs.getLong("lecture_id"));
	                dto.setLectureTitle(rs.getString("lecture_title"));
	                dto.setLectureRound(rs.getInt("lecture_round"));
	                dto.setSection(rs.getString("section"));
	                dto.setStartDate(rs.getDate("start_date").toLocalDate());
	                dto.setEndDate(rs.getDate("end_date").toLocalDate());
	                dto.setRoom(rs.getString("room"));
	                list.add(dto);
	            }
	        }
	    }
	    return list;
	}

}
