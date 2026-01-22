package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import model.dto.LectureSessionDTO;

public class LectureSessionDAO {

    private static final LectureSessionDAO instance = new LectureSessionDAO();
    private LectureSessionDAO() {}

    public static LectureSessionDAO getInstance() {
        return instance;
    }

    /* =================================================
     * 교수 출석 시작 → 회차 생성
     * ================================================= */
    public long insertSession(
            Connection conn,
            long lectureId,
            LocalDate sessionDate,
            LocalTime startTime,
            LocalTime endTime
    ) throws Exception {

        String sql = """
            INSERT INTO lecture_session
            (lecture_id, session_date, start_time, end_time)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt =
                     conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, lectureId);
            pstmt.setDate(2, java.sql.Date.valueOf(sessionDate));
            pstmt.setTime(3, java.sql.Time.valueOf(startTime));
            pstmt.setTime(4, java.sql.Time.valueOf(endTime));
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1); // 생성된 session_id
                }
            }
        }

        throw new IllegalStateException("회차 생성 실패");
    }

    /* =================================================
     * 같은 날 회차 이미 존재하는지 확인
     * ================================================= */
    public boolean existsTodaySession(
            Connection conn,
            long lectureId,
            LocalDate today
    ) throws Exception {

        String sql = """
            SELECT 1
            FROM lecture_session
            WHERE lecture_id = ?
              AND session_date = ?
            LIMIT 1
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setDate(2, java.sql.Date.valueOf(today));

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /* =================================================
     * 회차 단건 조회
     * ================================================= */
    public LectureSessionDTO findById(Connection conn, long sessionId)
            throws Exception {

        String sql = """
            SELECT session_id, lecture_id, session_date, start_time, end_time
            FROM lecture_session
            WHERE session_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                LectureSessionDTO dto = new LectureSessionDTO();
                dto.setSessionId(rs.getLong("session_id"));
                dto.setLectureId(rs.getLong("lecture_id"));
                dto.setSessionDate(rs.getDate("session_date").toLocalDate());
                dto.setStartTime(rs.getTime("start_time").toLocalTime());
                dto.setEndTime(rs.getTime("end_time").toLocalTime());
                return dto;
            }
        }
    }

    /* =================================================
     * 강의별 전체 회차 목록 (교수용)
     * ================================================= */
    public List<LectureSessionDTO> findByLecture(
            Connection conn,
            long lectureId
    ) throws Exception {

        String sql = """
            SELECT session_id, lecture_id, session_date, start_time, end_time
            FROM lecture_session
            WHERE lecture_id = ?
            ORDER BY session_date DESC, start_time DESC
        """;

        List<LectureSessionDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureSessionDTO dto = new LectureSessionDTO();
                    dto.setSessionId(rs.getLong("session_id"));
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setSessionDate(rs.getDate("session_date").toLocalDate());
                    dto.setStartTime(rs.getTime("start_time").toLocalTime());
                    dto.setEndTime(rs.getTime("end_time").toLocalTime());
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /* =================================================
     * 오늘 회차 조회 (학생용)
     * ================================================= */
    public LectureSessionDTO findToday(
            Connection conn,
            long lectureId,
            LocalDate today
    ) throws Exception {

        String sql = """
            SELECT session_id, lecture_id, session_date, start_time, end_time
            FROM lecture_session
            WHERE lecture_id = ?
              AND session_date = ?
            LIMIT 1
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setDate(2, java.sql.Date.valueOf(today));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                LectureSessionDTO dto = new LectureSessionDTO();
                dto.setSessionId(rs.getLong("session_id"));
                dto.setLectureId(rs.getLong("lecture_id"));
                dto.setSessionDate(rs.getDate("session_date").toLocalDate());
                dto.setStartTime(rs.getTime("start_time").toLocalTime());
                dto.setEndTime(rs.getTime("end_time").toLocalTime());
                return dto;
            }
        }
    }
}