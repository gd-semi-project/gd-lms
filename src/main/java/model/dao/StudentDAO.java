package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

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
	
	// 비밀번호 변경 관련(학번일치여부 확인)
	public boolean checkStudentNumberBychangeAccount(String loginId, int studentNumber) {
		String sql = "SELECT 1 FROM student s "
				+ "JOIN user u ON s.user_id = "
				+ "u.user_id WHERE u.login_id = "
				+ "? AND s.student_number = ?";
		
		 try (Connection conn = DBConnection.getConnection()) {
			 PreparedStatement pstmt = conn.prepareStatement(sql);

		        pstmt.setString(1, loginId);
		        pstmt.setInt(2, studentNumber);

		        ResultSet rs = pstmt.executeQuery();
		        return rs.next();

		    } catch (SQLException | ClassNotFoundException e) {
				// TODO: 예외처리 구문 작성 필요
				System.out.println(e.getMessage() + "111111");
				return false;
			}
	}
	
	// 해당 학기에 수강중인 목록
	public List<LectureDTO> selectMyLectures(Connection conn, Long userId) throws SQLException {

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


