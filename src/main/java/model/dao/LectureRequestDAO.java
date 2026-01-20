package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import model.dto.LectureRequestDTO;
import model.enumtype.LectureValidation;

public class LectureRequestDAO {

	private static final LectureRequestDAO instance = new LectureRequestDAO();

	private LectureRequestDAO() {
	}

	public static LectureRequestDAO getInstance() {
		return instance;
	}

	// 강의 개설 신청 리스트
	public List<LectureRequestDTO> selectByInstructor(Connection conn, long instructorId) {

		String sql = """
				    SELECT
				        lecture_id,
				        lecture_title,
				        section,
				        capacity,
				        validation,
				        created_at
				    FROM lecture
				    WHERE user_id = ?
				    ORDER BY created_at DESC
				""";

		List<LectureRequestDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, instructorId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LectureRequestDTO dto = new LectureRequestDTO();
					dto.setLectureId(rs.getLong("lecture_id"));
					dto.setLectureTitle(rs.getString("lecture_title"));
					dto.setSection(rs.getString("section"));
					dto.setCapacity(rs.getInt("capacity"));
					dto.setValidation(LectureValidation.valueOf(rs.getString("validation")));
					dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					list.add(dto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("강의 개설 신청 목록 조회 실패", e);
		}

		return list;
	}

	// 강의 개설 insert문
	public Long insertLecture(Connection conn, Long instructorId, HttpServletRequest request) throws SQLException {

		String sql = """
				    INSERT INTO lecture (
				        user_id,
				        lecture_title,
				        lecture_round,
				        start_date,
				        end_date,
				        room,
				        capacity,
				        section,
				        validation
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			pstmt.setLong(1, instructorId);
			pstmt.setString(2, request.getParameter("lectureTitle"));
			pstmt.setInt(3, Integer.parseInt(request.getParameter("lectureRound")));
			pstmt.setDate(4, java.sql.Date.valueOf(request.getParameter("startDate")));
			pstmt.setDate(5, java.sql.Date.valueOf(request.getParameter("endDate")));
			pstmt.setString(6, request.getParameter("room"));
			pstmt.setInt(7, Integer.parseInt(request.getParameter("capacity")));
			pstmt.setString(8, request.getParameter("section"));

			pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
			}
		}
		throw new SQLException("lecture_id 생성 실패");
	}

	// 강의 스케줄 insert문
	public void insertSchedule(Connection conn, Long lectureId, HttpServletRequest request) throws SQLException {

		String sql = """
				    INSERT INTO lecture_schedule (
				        lecture_id,
				        week_day,
				        start_time,
				        end_time
				    ) VALUES (?, ?, ?, ?)
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);
			pstmt.setString(2, request.getParameter("weekDay"));
			pstmt.setTime(3, java.sql.Time.valueOf(request.getParameter("startTime") + ":00"));
			pstmt.setTime(4, java.sql.Time.valueOf(request.getParameter("endTime") + ":00"));
			pstmt.executeUpdate();
		}
	}

	// 강의 상태 조회(필터링
	public LectureValidation getValidation(Connection conn, Long lectureId) throws SQLException {

		String sql = "SELECT validation FROM lecture WHERE lecture_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return LectureValidation.valueOf(rs.getString("validation"));
				}
			}
		}
		throw new SQLException("강의 상태 조회 실패");
	}

	// 강의 정보 수정
	public void updateLecture(Connection conn, Long lectureId, HttpServletRequest request) throws SQLException {

		String sql = """
				    UPDATE lecture
				    SET lecture_title = ?,
				        lecture_round = ?,
				        start_date = ?,
				        end_date = ?,
				        room = ?,
				        capacity = ?,
				        section = ?
				    WHERE lecture_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, request.getParameter("lectureTitle"));
			pstmt.setInt(2, Integer.parseInt(request.getParameter("lectureRound")));
			pstmt.setDate(3, java.sql.Date.valueOf(request.getParameter("startDate")));
			pstmt.setDate(4, java.sql.Date.valueOf(request.getParameter("endDate")));
			pstmt.setString(5, request.getParameter("room"));
			pstmt.setInt(6, Integer.parseInt(request.getParameter("capacity")));
			pstmt.setString(7, request.getParameter("section"));
			pstmt.setLong(8, lectureId);
			pstmt.executeUpdate();
		}
	}

	// 단건 조회
	public LectureRequestDTO selectByLectureId(
	        Connection conn, Long lectureId) throws SQLException {

	    String sql = """
	        SELECT
	            lecture_id,
	            lecture_title,
	            lecture_round,
	            start_date,
	            end_date,
	            room,
	            capacity,
	            section,
	            validation
	        FROM lecture
	        WHERE lecture_id = ?
	    """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setLong(1, lectureId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                LectureRequestDTO dto = new LectureRequestDTO();
	                dto.setLectureId(rs.getLong("lecture_id"));
	                dto.setLectureTitle(rs.getString("lecture_title"));
	                dto.setLectureRound(rs.getInt("lecture_round")); 
	                dto.setStartDate(rs.getDate("start_date").toLocalDate());
	                dto.setEndDate(rs.getDate("end_date").toLocalDate());
	                dto.setRoom(rs.getString("room"));
	                dto.setCapacity(rs.getInt("capacity"));
	                dto.setSection(rs.getString("section"));
	                dto.setValidation(
	                    LectureValidation.valueOf(rs.getString("validation"))
	                );
	                return dto;
	            }
	        }
	    }
	    return null;
	}

	// 강의 신청 삭제(PENDING 상태에서만 삭제 가능하도록
	public void deleteLecture(Connection conn, Long lectureId) throws SQLException {

		try (PreparedStatement pstmt1 = conn.prepareStatement("DELETE FROM lecture_schedule WHERE lecture_id = ?")) {
			pstmt1.setLong(1, lectureId);
			pstmt1.executeUpdate();
		}

		try (PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM lecture WHERE lecture_id = ?")) {
			pstmt2.setLong(1, lectureId);
			pstmt2.executeUpdate();
		}
	}
}