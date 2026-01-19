package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.LectureScheduleDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.EnrollmentStatus;
import model.enumtype.LectureValidation;
import model.enumtype.Week;

public class EnrollmentDAO {

	// 싱글톤 패턴
	public static final EnrollmentDAO instance = new EnrollmentDAO();

	public static EnrollmentDAO getInstance() {
		return instance;
	}



	// 강의별 수강생 조회
	public List<LectureStudentDTO> selectStudentsByLectureId(Connection conn, long lectureId) throws SQLException {
		
		String sql = """
				    SELECT
				        s.student_id,
				        s.user_id,
				        s.student_number,
				        s.student_grade,
				        u.name AS student_name,
				        e.status,
				        e.applied_at
				    FROM enrollment e
				    JOIN student s ON e.student_id = s.student_id
				    JOIN user u    ON s.user_id = u.user_id
				    WHERE e.lecture_id = ?
				    ORDER BY s.student_number
				""";

		List<LectureStudentDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LectureStudentDTO dto = new LectureStudentDTO();

					dto.setStudentId(rs.getInt("student_id"));
					dto.setUserId(rs.getInt("user_id"));
					dto.setStudentNumber(rs.getInt("student_number"));
					dto.setStudenGrade(rs.getInt("student_grade"));
					dto.setStudentName(rs.getString("student_name"));
					dto.setEnrollmentStatus(EnrollmentStatus.valueOf(rs.getString("status")));
					dto.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());

					list.add(dto);
				}
			}
		}
		return list;
	}

}
