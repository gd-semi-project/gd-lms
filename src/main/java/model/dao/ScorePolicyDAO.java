package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.dto.ScorePolicyDTO;

public class ScorePolicyDAO {

	private static final ScorePolicyDAO instance = new ScorePolicyDAO();

	public static ScorePolicyDAO getInstance() {
		return instance;
	}

	private ScorePolicyDAO() {
	}

	// 배점 조회용
    public ScorePolicyDTO findByLecture(Connection conn, long lectureId) {
        String sql = """
            SELECT *
            FROM score_policy
            WHERE lecture_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("성적 배점 조회 실패", e);
        }

        return null;
    }

	// 배점 초기 값
    public void upsert(Connection conn, ScorePolicyDTO policy) {
        String sql = """
            INSERT INTO score_policy (
                lecture_id,
                attendance_weight,
                assignment_weight,
                midterm_weight,
                final_weight,
                is_confirmed
            )
            VALUES (?, ?, ?, ?, ?, FALSE)
            ON DUPLICATE KEY UPDATE
                attendance_weight = VALUES(attendance_weight),
                assignment_weight = VALUES(assignment_weight),
                midterm_weight = VALUES(midterm_weight),
                final_weight = VALUES(final_weight),
                updated_at = NOW()
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, policy.getLectureId());
            ps.setInt(2, policy.getAttendanceWeight());
            ps.setInt(3, policy.getAssignmentWeight());
            ps.setInt(4, policy.getMidtermWeight());
            ps.setInt(5, policy.getFinalWeight());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("성적 배점 저장 실패", e);
        }
    }

	// 배점 확정
    public void confirmPolicy(Connection conn, long lectureId) {
        String sql = """
            UPDATE score_policy
            SET is_confirmed = TRUE,
                updated_at = NOW()
            WHERE lecture_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("성적 배점 확정 실패", e);
        }
    }

	// 배점 확정 여부
    public boolean isConfirmed(Connection conn, long lectureId) {
        String sql = """
            SELECT is_confirmed
            FROM score_policy
            WHERE lecture_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_confirmed");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("배점 확정 여부 조회 실패", e);
        }

        return false;
    }
    
    
    private ScorePolicyDTO map(ResultSet rs) throws Exception {
        ScorePolicyDTO dto = new ScorePolicyDTO();
        dto.setScorePolicyId(rs.getLong("score_policy_id"));
        dto.setLectureId(rs.getLong("lecture_id"));

        dto.setAttendanceWeight(rs.getInt("attendance_weight"));
        dto.setAssignmentWeight(rs.getInt("assignment_weight"));
        dto.setMidtermWeight(rs.getInt("midterm_weight"));
        dto.setFinalWeight(rs.getInt("final_weight"));

        dto.setConfirmed(rs.getBoolean("is_confirmed"));
        return dto;
    }

}
