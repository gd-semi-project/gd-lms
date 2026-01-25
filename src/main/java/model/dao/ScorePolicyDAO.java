package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.dto.ScorePolicyDTO;

public class ScorePolicyDAO {

    private static final ScorePolicyDAO instance =
            new ScorePolicyDAO();

    private ScorePolicyDAO() {}

    public static ScorePolicyDAO getInstance() {
        return instance;
    }
    
    // 존재여부 판단
    public boolean existsByLectureId(Connection conn, Long lectureId)
            throws SQLException {

        String sql = "SELECT 1 FROM score_policy WHERE lecture_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /* =========================
     * 배점 최초 등록
     * ========================= */
    public void insert(
            Connection conn,
            ScorePolicyDTO dto
    ) throws Exception {

        String sql = """
            INSERT INTO score_policy (
                lecture_id,
                attendance_weight,
                assignment_weight,
                midterm_weight,
                final_weight
            ) VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setLong(1, dto.getLectureId());
            pstmt.setInt(2, dto.getAttendanceWeight());
            pstmt.setInt(3, dto.getAssignmentWeight());
            pstmt.setInt(4, dto.getMidtermWeight());
            pstmt.setInt(5, dto.getFinalWeight());

            pstmt.executeUpdate();
        }
    }

    /* =========================
     * 강의별 배점 조회
     * ========================= */
    public ScorePolicyDTO findByLectureId(
            Connection conn,
            Long lectureId
    ) throws Exception {

        String sql = """
            SELECT *
            FROM score_policy
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                ScorePolicyDTO dto = new ScorePolicyDTO();
                dto.setScorePolicyId(
                        rs.getLong("score_policy_id"));
                dto.setLectureId(
                        rs.getLong("lecture_id"));

                dto.setAttendanceWeight(
                        rs.getInt("attendance_weight"));
                dto.setAssignmentWeight(
                        rs.getInt("assignment_weight"));
                dto.setMidtermWeight(
                        rs.getInt("midterm_weight"));
                dto.setFinalWeight(
                        rs.getInt("final_weight"));

                dto.setConfirmed(
                        rs.getBoolean("is_confirmed"));

                return dto;
            }
        }
    }

    /* =========================
     * 배점 수정
     * ========================= */
    public void update(
            Connection conn,
            ScorePolicyDTO dto
    ) throws Exception {

        String sql = """
            UPDATE score_policy
            SET
                attendance_weight = ?,
                assignment_weight = ?,
                midterm_weight = ?,
                final_weight = ?
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getAttendanceWeight());
            pstmt.setInt(2, dto.getAssignmentWeight());
            pstmt.setInt(3, dto.getMidtermWeight());
            pstmt.setInt(4, dto.getFinalWeight());
            pstmt.setLong(5, dto.getLectureId());

            pstmt.executeUpdate();
        }
    }
    
    public void deleteByLectureId(Connection conn, Long lectureId)
            throws SQLException {

        String sql = "DELETE FROM score_policy WHERE lecture_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            ps.executeUpdate();
        }
    }

    /* =========================
     * 배점 확정
     * ========================= */
    public void confirm(
            Connection conn,
            Long lectureId
    ) throws Exception {

        String sql = """
            UPDATE score_policy
            SET is_confirmed = TRUE
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setLong(1, lectureId);
            pstmt.executeUpdate();
        }
    }
}