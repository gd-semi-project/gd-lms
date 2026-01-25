package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.dto.LectureAttendanceStatusDTO;

public class LectureAttendanceStatusDAO { // 출석 가능 여부 판단

	private static final LectureAttendanceStatusDAO instance = new LectureAttendanceStatusDAO();

	private LectureAttendanceStatusDAO() {
	}

	public static LectureAttendanceStatusDAO getInstance() {
		return instance;
	}

	// 없으면 기본 row 생성
	public void ensureRow(Connection conn, long sessionId) throws Exception {
		String sql = """
				    INSERT INTO lecture_attendance_status (session_id)
				    VALUES (?)
				    ON DUPLICATE KEY UPDATE session_id = session_id
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, sessionId);
			pstmt.executeUpdate();
		}
	}

	// 출석 시작
	public void openAttendance(Connection conn, long sessionId) throws Exception {
		String sql = """
				    UPDATE lecture_attendance_status
				    SET is_open = TRUE,
				        opened_at = NOW(),
				        closed_at = NULL
				    WHERE session_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, sessionId);
			pstmt.executeUpdate();
		}
	}

	// 버튼 여부 확인
	public boolean isOpen(Connection conn, long sessionId) throws Exception {

		String sql = """
				    SELECT is_open
				    FROM lecture_attendance_status
				    WHERE session_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, sessionId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					return false;
				}
				return rs.getBoolean("is_open");
			}
		}
	}

	// 출석 종료
	public void closeAttendance(Connection conn, long sessionId) throws Exception {
		String sql = """
				    UPDATE lecture_attendance_status
				    SET is_open = FALSE,
				        closed_at = NOW()
				    WHERE session_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, sessionId);
			pstmt.executeUpdate();
		}
	}

}