package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.dto.LectureStudentDTO;
import model.enumtype.EnrollmentStatus;

public class EnrollmentDAO {

    private static final EnrollmentDAO instance = new EnrollmentDAO();
    private EnrollmentDAO() {}

    public static EnrollmentDAO getInstance() {
        return instance;
    }

    /**
     * 🔥 강의별 수강생 목록 조회
     */
    public List<LectureStudentDTO> findStudentsByLectureId(long lectureId) {

        String sql = """
            SELECT
                s.student_id,
                u.user_id,
                u.name,
                s.student_number,
                s.student_grade,
                e.status,
                e.applied_at
            FROM enrollment e
            JOIN student s ON e.student_id = s.student_id
            JOIN users u ON s.user_id = u.user_id
            WHERE e.lecture_id = ?
            ORDER BY s.student_number
        """;

        List<LectureStudentDTO> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureStudentDTO dto = new LectureStudentDTO();
                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setUserId(rs.getLong("user_id"));
                    dto.setStudentName(rs.getString("name"));
                    dto.setStudentNumber(rs.getInt("student_number"));
                    dto.setStudenGrade(rs.getInt("student_grade"));
                    dto.setEnrollmentStatus(
                        EnrollmentStatus.valueOf(rs.getString("status"))
                    );
                    dto.setAppliedAt(
                        rs.getTimestamp("applied_at").toLocalDateTime()
                    );

                    list.add(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("수강생 목록 조회 실패", e);
        }

        return list;
    }
}