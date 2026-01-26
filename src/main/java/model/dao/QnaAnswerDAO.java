package model.dao;

import model.dto.QnaAnswerDTO;
import model.enumtype.IsDeleted;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QnaAnswerDAO {
    private static final QnaAnswerDAO instance = new QnaAnswerDAO();
    private QnaAnswerDAO() {}
    public static QnaAnswerDAO getInstance() { return instance; }

    
    
    
    // 답변 ID 기준 단건 조회
    public QnaAnswerDTO findById(Connection conn, long answerId) throws SQLException {
    	String sql =
    			  "SELECT a.answer_id, a.qna_id, a.instructor_id, u.name AS instructor_name, " +
    			  "       a.content, a.is_deleted, a.created_at, a.updated_at " +
    			  "FROM qna_answers a " +
    			  "JOIN user u ON u.user_id = a.instructor_id " +
    			  "WHERE a.answer_id = ? AND a.is_deleted = 'N'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
    
    // QnaId 기준 답변 목록 조회
    public List<QnaAnswerDTO> findByQnaId(Connection conn, long qnaId) throws SQLException {
    	String sql =
    			  "SELECT a.answer_id, a.qna_id, a.instructor_id, u.name AS instructor_name, " +
    			  "       a.content, a.is_deleted, a.created_at, a.updated_at " +
    			  "FROM qna_answers a " +
    			  "JOIN user u ON u.user_id = a.instructor_id " +
    			  "WHERE a.qna_id = ? AND a.is_deleted = 'N' " +
    			  "ORDER BY a.created_at ASC";

        List<QnaAnswerDTO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, qnaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ====== insert ======
    public long insert(Connection conn, QnaAnswerDTO dto) throws SQLException {
        String sql =
            "INSERT INTO qna_answers (qna_id, instructor_id, content, is_deleted) " +
            "VALUES (?, ?, ?, 'N')";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, dto.getQnaId());
            ps.setLong(2, dto.getInstructorId());
            ps.setString(3, dto.getContent());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0L;
            }
        }
    }



    public int update(Connection conn, QnaAnswerDTO dto) throws SQLException {
        String sql =
            "UPDATE qna_answers SET content = ?, updated_at = NOW() " +
            "WHERE answer_id = ? AND is_deleted = 'N'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getContent());
            ps.setLong(2, dto.getAnswerId());
            return ps.executeUpdate();
        }
    }

    public int softDelete(Connection conn, long answerId) throws SQLException {
        String sql = "UPDATE qna_answers SET is_deleted = 'Y', updated_at = NOW() WHERE answer_id = ? AND is_deleted = 'N'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            return ps.executeUpdate();
        }
    }

    public int countActiveAnswers(Connection conn, long qnaId) throws SQLException {
        String sql = "SELECT COUNT(*) cnt FROM qna_answers WHERE qna_id = ? AND is_deleted = 'N'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, qnaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    private QnaAnswerDTO map(ResultSet rs) throws SQLException {
        QnaAnswerDTO d = new QnaAnswerDTO();
        d.setAnswerId(rs.getLong("answer_id"));
        d.setQnaId(rs.getLong("qna_id"));
        d.setInstructorId(rs.getLong("instructor_id"));
        d.setContent(rs.getString("content"));

        // ★ String -> enum
        d.setIsDeleted(IsDeleted.fromDb(rs.getString("is_deleted")));
        d.setInstructorName(rs.getString("instructor_name"));

        Timestamp c = rs.getTimestamp("created_at");
        Timestamp u = rs.getTimestamp("updated_at");
        d.setCreatedAt(c != null ? c.toLocalDateTime() : null);
        d.setUpdatedAt(u != null ? u.toLocalDateTime() : null);
        return d;
    }
    

}
