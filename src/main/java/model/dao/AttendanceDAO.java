package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.dto.AttendanceDTO;
import model.dto.AttendanceSummaryDTO;
import model.dto.SessionAttendanceDTO;
import model.enumtype.AttendanceStatus;

public class AttendanceDAO {

    private static final AttendanceDAO instance = new AttendanceDAO();
    private AttendanceDAO() {}

    public static AttendanceDAO getInstance() {	// 출결 데이터 처리
        return instance;
    }

    
    // 교수 출석 시작 시 학생 전원 결석 처리로 시작
    public void insertAbsentForLecture(
            Connection conn,
            long sessionId,
            long lectureId
    ) throws Exception {

        String sql = """
            INSERT INTO attendance (session_id, student_id, status)
            SELECT ?, e.user_id, 'ABSENT'
            FROM enrollment e
            WHERE e.lecture_id = ?
              AND e.status = 'ENROLLED'
            ON DUPLICATE KEY UPDATE attendance_id = attendance_id
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, sessionId);
            pstmt.setLong(2, lectureId);
            pstmt.executeUpdate();
        }
    }

    // 학생 출석 버튼
    public void markAttendance(
            Connection conn,
            long sessionId,
            long studentId,
            AttendanceStatus status
    ) throws Exception {

        String sql = """
            UPDATE attendance
            SET status = ?, checked_at = NOW()
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

    // 이미 출석 했는지 확인
    public boolean isAlreadyChecked(
            Connection conn,
            long sessionId,
            long studentId
    ) throws Exception {

        String sql = """
            SELECT 1
            FROM attendance
            WHERE session_id = ?
              AND student_id = ?
              AND status <> 'ABSENT'
            LIMIT 1
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, sessionId);
            pstmt.setLong(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 강의 출석 이력
    public List<AttendanceDTO> findByStudent(
            Connection conn,
            long lectureId,
            long studentId
    ) throws Exception {

        String sql = """
            SELECT
                a.attendance_id,
                a.session_id,
                a.student_id,
                a.status,
                a.checked_at,
                ls.session_date,
                ls.start_time,
                ls.end_time
            FROM attendance a
            JOIN lecture_session ls ON ls.session_id = a.session_id
            WHERE ls.lecture_id = ?
              AND a.student_id = ?
            ORDER BY ls.session_date DESC, ls.start_time DESC
        """;

        List<AttendanceDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setLong(2, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceDTO dto = new AttendanceDTO();
                    dto.setAttendanceId(rs.getLong("attendance_id"));
                    dto.setSessionId(rs.getLong("session_id"));
                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setStatus(
                            AttendanceStatus.valueOf(rs.getString("status"))
                    );

                    Timestamp ts = rs.getTimestamp("checked_at");
                    dto.setCheckedAt(ts != null ? ts.toLocalDateTime() : null);

                    dto.setSessionDate(rs.getDate("session_date").toLocalDate());
                    dto.setStartTime(rs.getTime("start_time").toLocalTime());
                    dto.setEndTime(rs.getTime("end_time").toLocalTime());

                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 회차별 출석부
    public List<SessionAttendanceDTO> findBySession(
            Connection conn,
            long sessionId
    ) throws Exception {

        String sql = """
            SELECT
                a.attendance_id,
                a.student_id,
                u.name AS student_name,
                s.student_number,
                s.student_grade,
                a.status,
                a.checked_at
            FROM attendance a
            JOIN user u ON u.user_id = a.student_id
            LEFT JOIN student s ON s.user_id = a.student_id
            WHERE a.session_id = ?
            ORDER BY s.student_number IS NULL, s.student_number, u.name
        """;

        List<SessionAttendanceDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SessionAttendanceDTO dto = new SessionAttendanceDTO();
                    dto.setAttendanceId(rs.getLong("attendance_id"));
                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setStudentName(rs.getString("student_name"));

                    int sn = rs.getInt("student_number");
                    dto.setStudentNumber(rs.wasNull() ? null : sn);

                    int sg = rs.getInt("student_grade");
                    dto.setStudentGrade(rs.wasNull() ? null : sg);

                    dto.setStatus(
                            AttendanceStatus.valueOf(rs.getString("status"))
                    );

                    Timestamp ts = rs.getTimestamp("checked_at");
                    dto.setCheckedAt(ts != null ? ts.toLocalDateTime() : null);

                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 출결 자동 수정 : 교수
    public void updateStatusById(
            Connection conn,
            long attendanceId,
            AttendanceStatus status
    ) throws Exception {

        String sql = """
            UPDATE attendance
            SET status = ?, checked_at = NOW()
            WHERE attendance_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setLong(2, attendanceId);
            pstmt.executeUpdate();
        }
    }
    
    
    // 출석 합계
    public AttendanceSummaryDTO getAttendanceSummary(
            Connection conn,
            Long lectureId,
            Long studentId
    ) {

        String sql = """
            SELECT
                COUNT(ls.session_id) AS total_sessions,
                SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) AS late_count
            FROM lecture_session ls
            JOIN student s
                ON s.student_id = ?
            LEFT JOIN attendance a
                ON a.session_id = ls.session_id
               AND a.student_id = s.user_id
            WHERE ls.lecture_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);
            pstmt.setLong(2, lectureId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                AttendanceSummaryDTO dto = new AttendanceSummaryDTO();
                dto.setStudentId(studentId);

                int total = rs.getInt("total_sessions");
                int present = rs.getInt("present_count");
                int late = rs.getInt("late_count");

                dto.setTotalSessionCount(total);
                dto.setPresentCount(present);
                dto.setLateCount(late);

                calculateAttendance(dto);
                return dto;
            }
        } catch (Exception e) {
            throw new RuntimeException("출석 요약 조회 실패", e);
        }

        return null;
    }
    
    private void calculateAttendance(AttendanceSummaryDTO dto) {

        int total = dto.getTotalSessionCount();
        int present = dto.getPresentCount();
        int late = dto.getLateCount();

        // 지각 3회 = 결석 1회
        int latePenalty = late / 3;

        // 실제 출석 인정 횟수
        int effectiveAttend =
                present + late - latePenalty;

        if (effectiveAttend < 0) effectiveAttend = 0;

        int absent = total - effectiveAttend;
        if (absent < 0) absent = 0;

        int score = 0;
        if (total > 0) {
            score = (int) Math.round(
                effectiveAttend * 100.0 / total
            );
        }

        dto.setEffectiveAttendCount(effectiveAttend);
        dto.setAbsentCount(absent);
        dto.setAttendanceScore(score);
    }
    
    
}