package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.dto.LectureDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.EnrollmentStatus;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;

public class LectureDAO {

    private static final LectureDAO instance = new LectureDAO();
    private LectureDAO() {}

    public static LectureDAO getInstance() {
        return instance;
    }

    // 교수 기준 강의 목록 : 지윤
    public List<LectureDTO> selectLecturesByInstructor(
            Connection conn, long instructorId) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                user_id,
                lecture_title,
                lecture_round,
                section,
                start_date,
                end_date,
                room,
                capacity,
                status,
                validation,
                created_at,
                updated_at
            FROM lecture
        	WHERE user_id = ?
        		AND validation = 'CONFIRMED'
        	ORDER BY start_date DESC
        """;

        List<LectureDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, instructorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLecture(rs));
                }
            }
        }
        return list;
    }

    // 강의별 수강생 목록 조회 : 지윤
    public List<LectureStudentDTO> selectLectureStudents(
            Connection conn, Long lectureId) throws SQLException {

        String sql = """
            SELECT
                s.student_id,
                u.user_id,
                u.name              AS student_name,
                s.student_number,
                s.student_grade,
                e.status            AS enrollment_status,
                e.applied_at
            FROM enrollment e
            JOIN student s ON e.student_id = s.student_id
            JOIN user u    ON s.user_id = u.user_id
            WHERE e.lecture_id = ?
              AND e.status = 'ENROLLED'
            ORDER BY s.student_number
        """;

        List<LectureStudentDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureStudentDTO dto = new LectureStudentDTO();

                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setUserId(rs.getLong("user_id"));
                    dto.setStudentName(rs.getString("student_name"));
                    dto.setStudentNumber(rs.getInt("student_number"));
                    dto.setStudenGrade(rs.getInt("student_grade"));

                    dto.setEnrollmentStatus(
                        EnrollmentStatus.valueOf(rs.getString("enrollment_status"))
                    );

                    Timestamp appliedAt = rs.getTimestamp("applied_at");
                    dto.setAppliedAt(
                        appliedAt != null ? appliedAt.toLocalDateTime() : null
                    );

                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 강의 상세 조회 : 지윤
    public LectureDTO selectLectureById(
            Connection conn, long lectureId) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                user_id,
                lecture_title,
                lecture_round,
                section,
                start_date,
                end_date,
                room,
                capacity,
                status,
                validation,
                created_at,
                updated_at
            FROM lecture
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                return mapResultSetToLecture(rs);
            }
        }
    }

    /* ======================================================
     *  ResultSet → LectureDTO 매핑
     * ====================================================== */
    private LectureDTO mapResultSetToLecture(ResultSet rs) throws SQLException {

        LectureDTO dto = new LectureDTO();

        dto.setLectureId(rs.getLong("lecture_id"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setLectureTitle(rs.getString("lecture_title"));
        dto.setLectureRound(rs.getInt("lecture_round"));
        dto.setSection(rs.getString("section"));

        Date startDate = rs.getDate("start_date");
        Date endDate = rs.getDate("end_date");
        dto.setStartDate(startDate != null ? startDate.toLocalDate() : null);
        dto.setEndDate(endDate != null ? endDate.toLocalDate() : null);

        dto.setRoom(rs.getString("room"));
        dto.setCapacity(rs.getInt("capacity"));

        String status = rs.getString("status");
        dto.setStatus(status != null ? LectureStatus.valueOf(status) : null);

        String validation = rs.getString("validation");
        dto.setValidation(validation != null ? LectureValidation.valueOf(validation) : null);

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        dto.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        dto.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

        return dto;
    }
}