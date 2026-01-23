package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.dto.LectureAttendanceStatusDTO;

public class LectureAttendanceStatusDAO {

    private static final LectureAttendanceStatusDAO instance =
            new LectureAttendanceStatusDAO();

    private LectureAttendanceStatusDAO() {}

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

    // 상태 조회
    public LectureAttendanceStatusDTO findBySession(Connection conn, long sessionId) throws Exception {
        String sql = """
            SELECT session_id, is_open, opened_at, closed_at
            FROM lecture_attendance_status
            WHERE session_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, sessionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                LectureAttendanceStatusDTO dto =
                        new LectureAttendanceStatusDTO();
                dto.setSessionId(rs.getLong("session_id"));
                dto.setOpen(rs.getBoolean("is_open"));
                dto.setOpenedAt(rs.getTimestamp("opened_at") != null
                        ? rs.getTimestamp("opened_at").toLocalDateTime()
                        : null);
                dto.setClosedAt(rs.getTimestamp("closed_at") != null
                        ? rs.getTimestamp("closed_at").toLocalDateTime()
                        : null);
                return dto;
            }
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