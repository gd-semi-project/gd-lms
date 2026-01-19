package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.AttendanceDTO;
import model.dto.SessionAttendanceDTO;
import model.dto.StudentAttendanceDTO;
import model.enumtype.AttendanceStatus;

public class AttendanceDAO {

	private static final AttendanceDAO instance = new AttendanceDAO();

	public static AttendanceDAO getInstance() {
		return instance;
	}

	private AttendanceDAO() {
	}

	// 출석 등록
	public void insertAttendance(Connection conn, AttendanceDTO dto) throws SQLException {

		String sql = """
				    INSERT INTO attendance
				    (session_id, student_id, status, checked_at)
				    VALUES (?, ?, ?, ?)
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, dto.getSessionId());
			pstmt.setLong(2, dto.getStudentId());
			pstmt.setString(3, dto.getStatus().name());
			pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(dto.getCheckedAt()));
			pstmt.executeUpdate();
		}
	}

	// 자동 결석 처리
	public void insertAbsentIfNotExists(Connection conn, long sessionId) throws SQLException {

		String sql = """
				    INSERT INTO attendance (session_id, student_id, status)
				    SELECT ?, e.student_id, 'ABSENT'
				    FROM enrollment e
				    WHERE e.lecture_id = (
				        SELECT lecture_id
				        FROM lecture_session
				        WHERE session_id = ?
				    )
				    AND NOT EXISTS (
				        SELECT 1
				        FROM attendance a
				        WHERE a.session_id = ?
				          AND a.student_id = e.student_id
				    )
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, sessionId);
			pstmt.setLong(2, sessionId);
			pstmt.setLong(3, sessionId);
			pstmt.executeUpdate();
		}
	}

	// 오늘 출석 조회 ; 학생
	public AttendanceDTO selectTodayAttendance(Connection conn, long studentId, long lectureId) throws SQLException {

		String sql = """
				    SELECT a.*
				    FROM attendance a
				    JOIN lecture_session ls
				      ON a.session_id = ls.session_id
				    WHERE a.student_id = ?
				      AND ls.lecture_id = ?
				      AND ls.session_date = CURRENT_DATE
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, studentId);
			pstmt.setLong(2, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					AttendanceDTO dto = new AttendanceDTO();
					dto.setAttendanceId(rs.getLong("attendance_id"));
					dto.setSessionId(rs.getLong("session_id"));
					dto.setStudentId(rs.getLong("student_id"));
					dto.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
					dto.setCheckedAt(rs.getTimestamp("checked_at") == null ? null
							: rs.getTimestamp("checked_at").toLocalDateTime());
					return dto;
				}
			}
		}
		return null;
	}

	// 교수용 출석부
	public List<SessionAttendanceDTO> selectBySession(Connection conn, long sessionId) throws SQLException {

		String sql = """
				    SELECT
				        s.student_id,
				        u.name,
				        s.student_number,
				        s.student_grade,
				        a.status
				    FROM attendance a
				    JOIN student s ON a.student_id = s.student_id
				    JOIN user u ON s.user_id = u.user_id
				    WHERE a.session_id = ?
				    ORDER BY s.student_number
				""";

		List<SessionAttendanceDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, sessionId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					SessionAttendanceDTO dto = new SessionAttendanceDTO();
					dto.setStudentId(rs.getLong("student_id"));
					dto.setStudentName(rs.getString("name"));
					dto.setStudentNumber(rs.getInt("student_number"));
					dto.setStudentGrade(rs.getInt("student_grade"));
					dto.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
					list.add(dto);
				}
			}
		}
		return list;
	}

	// 교수용 출결 상태 수정
	public void updateStatus(Connection conn, long sessionId, long studentId, AttendanceStatus status)
			throws SQLException {

		String sql = """
				    UPDATE attendance
				    SET status = ?
				    WHERE session_id = ?
				      AND student_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, status.name());
			pstmt.setLong(2, sessionId);
			pstmt.setLong(3, studentId);
			pstmt.executeUpdate();
		}
	}

	// 학생 출결 상태
	public List<StudentAttendanceDTO> selectByStudent(Connection conn, long studentId, long lectureId)
			throws SQLException {

		String sql = """
				    SELECT
				        ls.session_date,
				        ls.start_time,
				        ls.end_time,
				        a.status,
				        a.checked_at
				    FROM lecture_session ls
				    LEFT JOIN attendance a
				      ON ls.session_id = a.session_id
				     AND a.student_id = ?
				    WHERE ls.lecture_id = ?
				    ORDER BY ls.session_date
				""";

		List<StudentAttendanceDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, studentId);
			pstmt.setLong(2, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					StudentAttendanceDTO dto = new StudentAttendanceDTO();
					dto.setSessionDate(rs.getDate("session_date").toLocalDate());
					dto.setStartTime(rs.getTime("start_time").toLocalTime());
					dto.setEndTime(rs.getTime("end_time").toLocalTime());
					dto.setCheckedAt(rs.getTimestamp("checked_at") == null ? null
							: rs.getTimestamp("checked_at").toLocalDateTime());
					if (rs.getString("status") != null) {
						dto.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
					}
					list.add(dto);
				}
			}
		}
		return list;
	}
}