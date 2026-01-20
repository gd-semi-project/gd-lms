package model.dao;

import model.dto.QnaPostDTO;
import model.enumtype.QnaStatus;
import model.enumtype.isDeleted;
import model.enumtype.isPrivate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QnaPostDAO {
    private static final QnaPostDAO instance = new QnaPostDAO();
    private QnaPostDAO() {}
    public static QnaPostDAO getInstance() { return instance; }

    // ====== 목록: 강의별 ======
    public List<QnaPostDTO> findByLecture(Connection conn, long lectureId, int limit, int offset) throws SQLException {
        String sql =
            "SELECT qna_id, lecture_id, author_id, title, content, is_private, status, is_deleted, created_at, updated_at " +
            "FROM qna_posts " +
            "WHERE lecture_id = ? AND is_deleted = 'N' " +
            "ORDER BY created_at DESC " +
            "LIMIT ? OFFSET ?";

        List<QnaPostDTO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // STUDENT: 공개글 + 본인 비공개글만
    public List<QnaPostDTO> findByLectureForStudent(Connection conn, long lectureId, long studentId, int limit, int offset) throws SQLException {
        String sql =
            "SELECT qna_id, lecture_id, author_id, title, content, is_private, status, is_deleted, created_at, updated_at " +
            "FROM qna_posts " +
            "WHERE lecture_id = ? AND is_deleted = 'N' " +
            "  AND (is_private = 'N' OR author_id = ?) " +
            "ORDER BY created_at DESC " +
            "LIMIT ? OFFSET ?";

        List<QnaPostDTO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            ps.setLong(2, studentId);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int countByLecture(Connection conn, long lectureId) throws SQLException {
        String sql = "SELECT COUNT(*) cnt FROM qna_posts WHERE lecture_id = ? AND is_deleted = 'N'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    public int countByLectureForStudent(Connection conn, long lectureId, long studentId) throws SQLException {
        String sql =
            "SELECT COUNT(*) cnt " +
            "FROM qna_posts " +
            "WHERE lecture_id = ? AND is_deleted = 'N' " +
            "  AND (is_private = 'N' OR author_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    // ====== 단건 조회 ======
    public QnaPostDTO findById(Connection conn, long qnaId, long lectureId) throws SQLException {
        String sql =
            "SELECT qna_id, lecture_id, author_id, title, content, is_private, status, is_deleted, created_at, updated_at " +
            "FROM qna_posts " +
            "WHERE qna_id = ? AND lecture_id = ? AND is_deleted = 'N'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, qnaId);
            ps.setLong(2, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    // ====== CUD ======
    public long insert(Connection conn, QnaPostDTO dto) throws SQLException {
        String sql =
            "INSERT INTO qna_posts (lecture_id, author_id, title, content, is_private, status, is_deleted) " +
            "VALUES (?, ?, ?, ?, ?, 'OPEN', 'N')";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, dto.getLectureId());
            ps.setLong(2, dto.getAuthorId());
            ps.setString(3, dto.getTitle());
            ps.setString(4, dto.getContent());

            isPrivate priv = (dto.getIsPrivate() == null) ? isPrivate.N : dto.getIsPrivate();
            ps.setString(5, priv.toDb()); // ★ enum -> DB 문자열

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0L;
            }
        }
    }

    public int update(Connection conn, QnaPostDTO dto) throws SQLException {
        String sql =
            "UPDATE qna_posts " +
            "SET title = ?, content = ?, is_private = ?, updated_at = NOW() " +
            "WHERE qna_id = ? AND is_deleted = 'N'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());

            isPrivate priv = (dto.getIsPrivate() == null) ? isPrivate.N : dto.getIsPrivate();
            ps.setString(3, priv.toDb()); // ★

            ps.setLong(4, dto.getQnaId());
            return ps.executeUpdate();
        }
    }

    public int softDelete(Connection conn, long qnaId) throws SQLException {
        String sql = "UPDATE qna_posts SET is_deleted = 'Y', updated_at = NOW() WHERE qna_id = ? AND is_deleted = 'N'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, qnaId);
            return ps.executeUpdate();
        }
    }

    // ★ status도 enum으로 받도록 수정
    public int updateStatus(Connection conn, long qnaId, QnaStatus status) throws SQLException {
        String sql = "UPDATE qna_posts SET status = ?, updated_at = NOW() WHERE qna_id = ? AND is_deleted = 'N'";
        QnaStatus st = (status == null) ? QnaStatus.OPEN : status;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, st.name());
            ps.setLong(2, qnaId);
            return ps.executeUpdate();
        }
    }

    private QnaPostDTO map(ResultSet rs) throws SQLException {
        QnaPostDTO d = new QnaPostDTO();
        d.setQnaId(rs.getLong("qna_id"));
        d.setLectureId(rs.getLong("lecture_id"));
        d.setAuthorId(rs.getLong("author_id"));
        d.setTitle(rs.getString("title"));
        d.setContent(rs.getString("content"));

        // ★ String -> enum 변환
        d.setIsPrivate(isPrivate.fromDb(rs.getString("is_private")));
        d.setIsDeleted(isDeleted.fromDb(rs.getString("is_deleted")));

        String statusDb = rs.getString("status");
        d.setStatus(statusDb == null ? QnaStatus.OPEN : QnaStatus.valueOf(statusDb.trim().toUpperCase()));

        Timestamp c = rs.getTimestamp("created_at");
        Timestamp u = rs.getTimestamp("updated_at");
        d.setCreatedAt(c != null ? c.toLocalDateTime() : null);
        d.setUpdatedAt(u != null ? u.toLocalDateTime() : null);
        return d;
    }
    

 
    
    
    
    
}
