package model.dao;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import database.DBConnection;
import model.dto.LectureScheduleDTO;
import model.enumtype.Week;

public class LectureScheduleDAO {

    private static final LectureScheduleDAO instance = new LectureScheduleDAO();

    private LectureScheduleDAO() {}

    public static LectureScheduleDAO getInstance() {
        return instance;
    }

    public List<LectureScheduleDTO> selectByLectureId(Connection conn, long lectureId)
            throws SQLException {

        String sql = """
            SELECT
                schedule_id,
                lecture_id,
                week_day,
                start_time,
                end_time,
                created_at
            FROM lecture_schedule
            WHERE lecture_id = ?
            ORDER BY 
                FIELD(week_day,'MON','TUE','WED','THU','FRI','SAT','SUN'),
                start_time
        """;

        List<LectureScheduleDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureScheduleDTO schedule = new LectureScheduleDTO();

                    schedule.setScheduleId(rs.getLong("schedule_id"));
                    schedule.setLectureId(rs.getLong("lecture_id")); // DTO가 int라 OK
                    schedule.setWeekDay(Week.valueOf(rs.getString("week_day")));
                    schedule.setStartTime(rs.getTime("start_time").toLocalTime());
                    schedule.setEndTime(rs.getTime("end_time").toLocalTime());
                    schedule.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    list.add(schedule);
                }
            }
        }
        return list;
    }

    /* ============================
     * 강의 시간표 등록
     * ============================ */
    public void insertSchedule(Connection conn, LectureScheduleDTO schedule)
            throws SQLException {

        String sql = """
            INSERT INTO lecture_schedule (
                lecture_id,
                week_day,
                start_time,
                end_time
            ) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, schedule.getLectureId());

            // 🔥 Week enum → String
            pstmt.setString(2, schedule.getWeekDay().name());

            pstmt.setTime(3, java.sql.Time.valueOf(schedule.getStartTime()));
            pstmt.setTime(4, java.sql.Time.valueOf(schedule.getEndTime()));

            pstmt.executeUpdate();
        }
    }

    /* ============================
     * 강의 시간표 삭제 (강의 기준)
     * ============================ */
    public void deleteByLectureId(Connection conn, int lectureId)
            throws SQLException {

        String sql = "DELETE FROM lecture_schedule WHERE lecture_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lectureId);
            pstmt.executeUpdate();
        }
    }

	public List<LectureScheduleDTO> findByLectureIds(List<Long> lectureIds) {
		 if (lectureIds == null || lectureIds.isEmpty()) {
	            return Collections.emptyList();
	        }

	        String placeholders = lectureIds.stream()
	                .map(id -> "?")
	                .collect(Collectors.joining(","));

	        String sql =
	            "SELECT schedule_id, lecture_id, week_day, start_time, end_time, created_at " +
	            "FROM lecture_schedule " +
	            "WHERE lecture_id IN (" + placeholders + ") " +
	            "ORDER BY lecture_id, FIELD(week_day,'MON','TUE','WED','THU','FRI','SAT','SUN'), start_time";

	        List<LectureScheduleDTO> list = new ArrayList<>();

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            int idx = 1;
	            for (Long id : lectureIds) {
	                pstmt.setLong(idx++, id);
	            }

	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    LectureScheduleDTO schedule = new LectureScheduleDTO();
	                    schedule.setScheduleId(rs.getLong("schedule_id"));
	                    schedule.setLectureId(rs.getLong("lecture_id"));

	                    String wd = rs.getString("week_day");
	                    schedule.setWeekDay(wd != null ? Week.valueOf(wd.trim().toUpperCase()) : null);

	                    Time st = rs.getTime("start_time");
	                    Time et = rs.getTime("end_time");
	                    schedule.setStartTime(st != null ? st.toLocalTime() : null);
	                    schedule.setEndTime(et != null ? et.toLocalTime() : null);

	                    list.add(schedule);
	                }
	            }

	        } catch (Exception e) {
	            throw new RuntimeException("findByLectureIds failed. size=" + lectureIds.size(), e);
	        }

	        return list;
	}
}