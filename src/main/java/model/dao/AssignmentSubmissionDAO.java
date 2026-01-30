// AssignmentSubmissionDAO.java
package model.dao;

import model.dto.AssignmentSubmissionDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionDAO {
    private static final AssignmentSubmissionDAO instance = new AssignmentSubmissionDAO();
    private AssignmentSubmissionDAO() {}
    public static AssignmentSubmissionDAO getInstance() { return instance; }

    // 제출 목록 (교수용)
    public List<AssignmentSubmissionDTO> selectByAssignment(Connection conn, long assignmentId) throws SQLException {
    	String sql = """
    		    SELECT s.submission_id, s.assignment_id, s.student_id, s.content,
    		           s.score, s.feedback, s.submitted_at, s.graded_at,
    		           u.name AS student_name
    		    FROM assignment_submission s
    		    JOIN user u ON s.student_id = u.user_id
    		    WHERE s.assignment_id = ?
    		    ORDER BY s.submitted_at DESC
    		    """;
        
        List<AssignmentSubmissionDTO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
                    dto.setSubmissionId(rs.getLong("submission_id"));
                    dto.setAssignmentId(rs.getLong("assignment_id"));
                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setScore(rs.getObject("score") != null ? rs.getInt("score") : null);
                    dto.setFeedback(rs.getString("feedback"));
                    
                    Timestamp subTs = rs.getTimestamp("submitted_at");
                    dto.setSubmittedAt(subTs != null ? subTs.toLocalDateTime() : null);

                    Timestamp gradedTs = rs.getTimestamp("graded_at");
                    dto.setGradedAt(gradedTs != null ? gradedTs.toLocalDateTime() : null);
                    dto.setStudentName(rs.getString("student_name"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 본인 제출 조회 (학생용)
    public AssignmentSubmissionDTO selectByStudentAndAssignment(Connection conn, long studentId, long assignmentId) throws SQLException {
        String sql = """
            SELECT submission_id, assignment_id, student_id, content,
                   score, feedback, submitted_at, graded_at
            FROM assignment_submission
            WHERE student_id = ? AND assignment_id = ?
            """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
                    dto.setSubmissionId(rs.getLong("submission_id"));
                    dto.setAssignmentId(rs.getLong("assignment_id"));
                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setScore(rs.getObject("score") != null ? rs.getInt("score") : null);
                    dto.setFeedback(rs.getString("feedback"));
                    
                    Timestamp subTs = rs.getTimestamp("submitted_at");
                    dto.setSubmittedAt(subTs != null ? subTs.toLocalDateTime() : null);

                    Timestamp gradedTs = rs.getTimestamp("graded_at");
                    dto.setGradedAt(gradedTs != null ? gradedTs.toLocalDateTime() : null);
                    
                    return dto;
                }
            }
        }
        return null;
    }

    // 과제 제출 등록
    public long insert(Connection conn, AssignmentSubmissionDTO dto) throws SQLException {
        String sql = """
            INSERT INTO assignment_submission (assignment_id, student_id, content)
            VALUES (?, ?, ?)
            """;
        
        // _GENERATED_KEYS => DB가 생성한 키 값(Generated Key)을 ResultSet으로 반환
        // =>AUTO_INCREMENT 컬럼 값 submission_id
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, dto.getAssignmentId());
            ps.setLong(2, dto.getStudentId());
            ps.setString(3, dto.getContent());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    // 제출 수정 (재제출)
    public int update(Connection conn, AssignmentSubmissionDTO dto) throws SQLException {
        String sql = """
            UPDATE assignment_submission
            SET content = ?, submitted_at = CURRENT_TIMESTAMP
            WHERE submission_id = ? AND student_id = ?
            """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getContent());
            ps.setLong(2, dto.getSubmissionId());
            ps.setLong(3, dto.getStudentId());
            return ps.executeUpdate();
        }
    }

    // 채점
    public int updateGrade(Connection conn, long submissionId, int score, String feedback) throws SQLException {
        String sql = """
            UPDATE assignment_submission
            SET score = ?, feedback = ?, graded_at = CURRENT_TIMESTAMP
            WHERE submission_id = ?
            """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setString(2, feedback);
            ps.setLong(3, submissionId);
            return ps.executeUpdate();
        }
    }
}