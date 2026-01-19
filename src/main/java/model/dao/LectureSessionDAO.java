package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.dto.LectureDTO;
import model.dto.LectureScheduleDTO;
import model.dto.LectureSessionDTO;
import model.enumtype.Week;

public class LectureSessionDAO {

    private static final LectureSessionDAO instance =
        new LectureSessionDAO();

    public static LectureSessionDAO getInstance() {
        return instance;
    }

    private LectureSessionDAO() {}

    // =========================
    // 회차 단건 조회
    // =========================
    public LectureSessionDTO selectById(
            Connection conn,
            long sessionId
    ) throws SQLException {

        String sql = """
            SELECT *
            FROM lecture_session
            WHERE session_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LectureSessionDTO dto = new LectureSessionDTO();
                    dto.setSessionId(rs.getLong("session_id"));
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setSessionDate(
                        rs.getDate("session_date").toLocalDate()
                    );
                    dto.setStartTime(
                        rs.getTime("start_time").toLocalTime()
                    );
                    dto.setEndTime(
                        rs.getTime("end_time").toLocalTime()
                    );
                    return dto;
                }
            }
        }
        return null;
    }

    // =========================
    // 강의별 회차 조회 (교수)
    // =========================
    public List<LectureSessionDTO> selectByLectureId(
            Connection conn,
            long lectureId
    ) throws SQLException {

        String sql = """
            SELECT *
            FROM lecture_session
            WHERE lecture_id = ?
            ORDER BY session_date
        """;

        List<LectureSessionDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureSessionDTO dto = new LectureSessionDTO();
                    dto.setSessionId(rs.getLong("session_id"));
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setSessionDate(
                        rs.getDate("session_date").toLocalDate()
                    );
                    dto.setStartTime(
                        rs.getTime("start_time").toLocalTime()
                    );
                    dto.setEndTime(
                        rs.getTime("end_time").toLocalTime()
                    );
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // =========================
    // 특정 날짜 수업 조회 (학생)
    // =========================
    public LectureSessionDTO selectByDate(
            Connection conn,
            long lectureId,
            LocalDate date
    ) throws SQLException {

        String sql = """
            SELECT *
            FROM lecture_session
            WHERE lecture_id = ?
              AND session_date = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LectureSessionDTO dto = new LectureSessionDTO();
                    dto.setSessionId(rs.getLong("session_id"));
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setSessionDate(
                        rs.getDate("session_date").toLocalDate()
                    );
                    dto.setStartTime(
                        rs.getTime("start_time").toLocalTime()
                    );
                    dto.setEndTime(
                        rs.getTime("end_time").toLocalTime()
                    );
                    return dto;
                }
            }
        }
        return null;
    }

    // =========================
    // 회차 자동 생성
    // =========================
    public void generateSessions(
            Connection conn,
            LectureDTO lecture,
            List<LectureScheduleDTO> schedules
    ) throws SQLException {

        String sql = """
            INSERT IGNORE INTO lecture_session
            (lecture_id, session_date, start_time, end_time)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalDate start = lecture.getStartDate();
            LocalDate end   = lecture.getEndDate();

            for (LocalDate date = start;
                 !date.isAfter(end);
                 date = date.plusDays(1)) {

                DayOfWeek javaDay = date.getDayOfWeek();

                for (LectureScheduleDTO s : schedules) {
                    if (matches(javaDay, s.getWeekDay())) {
                        pstmt.setLong(1, lecture.getLectureId());
                        pstmt.setDate(2,
                            java.sql.Date.valueOf(date));
                        pstmt.setTime(3,
                            java.sql.Time.valueOf(
                                s.getStartTime()));
                        pstmt.setTime(4,
                            java.sql.Time.valueOf(
                                s.getEndTime()));
                        pstmt.addBatch();
                    }
                }
            }
            pstmt.executeBatch();
        }
    }

    private boolean matches(DayOfWeek javaDay, Week dbDay) {
        return switch (dbDay) {
            case MON -> javaDay == DayOfWeek.MONDAY;
            case TUE -> javaDay == DayOfWeek.TUESDAY;
            case WED -> javaDay == DayOfWeek.WEDNESDAY;
            case THU -> javaDay == DayOfWeek.THURSDAY;
            case FRI -> javaDay == DayOfWeek.FRIDAY;
            case SAT -> javaDay == DayOfWeek.SATURDAY;
            case SUN -> javaDay == DayOfWeek.SUNDAY;
        };
    }
}