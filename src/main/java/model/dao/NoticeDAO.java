package model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import database.DBConnection;
import model.dto.NoticeDTO;

public class NoticeDAO {
	
	// 싱글톤 패턴
    private static final NoticeDAO instance = new NoticeDAO();
    private NoticeDAO() {}
    public static NoticeDAO getInstance() { return instance; }

    // 검색 허용 컬럼만
    private static final Set<String> ALLOWED_ITEMS = Set.of("title", "content", "notice_type", "all");
    
    // 검색 조건 생성 헬퍼
    private String buildSearchCondition(String items) {
        if ("all".equals(items)) {
            return "(title LIKE ? OR content LIKE ?)";
        }
        return items + " LIKE ?";
    }
    
    // 검색 조건 유효성 검증
    private boolean isValidSearch(String items, String text) {
        return items != null && text != null
                && !items.isBlank() && !text.isBlank()
                && ALLOWED_ITEMS.contains(items);
    }
    
    // 조회수 증가
    public int increaseViewCount(Connection conn, long noticeId) throws SQLException {
        String sql =
            "UPDATE notices " +
            "SET view_count = view_count + 1 " +
            "WHERE notice_id = ? AND is_deleted = 'N'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, noticeId);
            return pstmt.executeUpdate();
        }
    }

    // ========== 전체 공지사항 조회 (관리자/교수용) ==========
    
    public int countAll(String items, String text) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notices " +
            "WHERE is_deleted = 'N'"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) {
            sql.append(" AND ").append(buildSearchCondition(items));
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx, "%" + text + "%");
                } else {
                    pstmt.setString(idx, "%" + text + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.countAll error", e);
        }
    }

    public List<NoticeDTO> findPageAll(int limit, int offset, String items, String text) {
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notices " +
            "WHERE is_deleted = 'N'"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) {
            sql.append(" AND ").append(buildSearchCondition(items));
        }

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx++, "%" + text + "%");
                } else {
                    pstmt.setString(idx++, "%" + text + "%");
                }
            }

            pstmt.setInt(idx++, limit);
            pstmt.setInt(idx, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.findPageAll error", e);
        }
    }

    // ========== 학생용 전체 공지사항 조회 (전체공지 + 본인 수강강의 공지만) ==========
    
    public int countAllForStudent(Long userId, String items, String text) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt FROM notices " +
            "WHERE is_deleted = 'N' AND (" +
            "  lecture_id IS NULL " +  // 전체 공지
            "  OR lecture_id IN (" +     // 또는 본인이 수강하는 강의의 공지
            "    SELECT lecture_id FROM enrollments " +
            "    WHERE user_id = ? AND status = 'ACTIVE'" +
            "  )" +
            ")"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) {
            sql.append(" AND ").append(buildSearchCondition(items));
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            pstmt.setLong(idx++, userId);
            
            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx, "%" + text + "%");
                } else {
                    pstmt.setString(idx, "%" + text + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.countAllForStudent error", e);
        }
    }

    public List<NoticeDTO> findPageAllForStudent(Long userId, int limit, int offset, String items, String text) {
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notices " +
            "WHERE is_deleted = 'N' AND (" +
            "  lecture_id IS NULL " +
            "  OR lecture_id IN (" +
            "    SELECT lecture_id FROM enrollments " +
            "    WHERE user_id = ? AND status = 'ACTIVE'" +
            "  )" +
            ")"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) {
            sql.append(" AND ").append(buildSearchCondition(items));
        }

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            pstmt.setLong(idx++, userId);

            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx++, "%" + text + "%");
                } else {
                    pstmt.setString(idx++, "%" + text + "%");
                }
            }

            pstmt.setInt(idx++, limit);
            pstmt.setInt(idx, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.findPageAllForStudent error", e);
        }
    }

    // ========== 특정 강의 공지사항 조회 ==========
    
    public int countByLecture(long lectureId, String items, String text) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notices " +
            "WHERE is_deleted = 'N' AND lecture_id = ?"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) {
            sql.append(" AND ").append(buildSearchCondition(items));
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            pstmt.setLong(idx++, lectureId);
            
            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx, "%" + text + "%");
                } else {
                    pstmt.setString(idx, "%" + text + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.countByLecture error", e);
        }
    }

    public List<NoticeDTO> findPageByLecture(long lectureId, int limit, int offset, String items, String text) {
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notices " +
            "WHERE is_deleted = 'N' AND lecture_id = ?"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) {
            sql.append(" AND ").append(buildSearchCondition(items));
        }

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            pstmt.setLong(idx++, lectureId);

            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx++, "%" + text + "%");
                } else {
                    pstmt.setString(idx++, "%" + text + "%");
                }
            }

            pstmt.setInt(idx++, limit);
            pstmt.setInt(idx, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.findPageByLecture error", e);
        }
    }

    // ========== 단건 조회 ==========
    
    public NoticeDTO findById(Connection conn, long noticeId) throws SQLException {
        String sql =
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notices " +
            "WHERE is_deleted = 'N' AND notice_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, noticeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                return mapResultSetToDTO(rs);
            }
        }
    }
    
    public NoticeDTO findByIdAndLecture(Connection conn, long noticeId, long lectureId) throws SQLException {
        String sql =
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notices " +
            "WHERE is_deleted = 'N' AND notice_id = ? AND lecture_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, noticeId);
            pstmt.setLong(2, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                return mapResultSetToDTO(rs);
            }
        }
    }

    // ========== CUD 작업 ==========
    
    public long insert(Connection conn, NoticeDTO notice) throws SQLException {
        String sql =
            "INSERT INTO notices (lecture_id, author_id, notice_type, title, content) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (notice.getLectureId() == null) {
                pstmt.setNull(1, Types.BIGINT);
            } else {
                pstmt.setLong(1, notice.getLectureId());
            }

            pstmt.setLong(2, notice.getAuthorId());
            pstmt.setString(3, notice.getNoticeType());
            pstmt.setString(4, notice.getTitle());
            pstmt.setString(5, notice.getContent());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0;
            }
        }
    }

    public int update(Connection conn, NoticeDTO dto) throws SQLException {
        String sql =
            "UPDATE notices " +
            "SET notice_type = ?, title = ?, content = ?, updated_at = NOW() " +
            "WHERE is_deleted = 'N' AND notice_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getNoticeType());
            pstmt.setString(2, dto.getTitle());
            pstmt.setString(3, dto.getContent());
            pstmt.setLong(4, dto.getNoticeId());
            return pstmt.executeUpdate();
        }
    }

    public int softDelete(Connection conn, long noticeId, Long lectureId) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "UPDATE notices SET is_deleted = 'Y' WHERE is_deleted = 'N' AND notice_id = ?"
        );
        if (lectureId != null) {
            sql.append(" AND lecture_id = ?");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setLong(1, noticeId);
            if (lectureId != null) {
                pstmt.setLong(2, lectureId);
            }
            return pstmt.executeUpdate();
        }
    }

    // ========== Helper 메서드 ==========
    
    private NoticeDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        NoticeDTO n = new NoticeDTO();
        n.setNoticeId(rs.getLong("notice_id"));
        n.setLectureId(rs.getObject("lecture_id", Long.class));
        n.setAuthorId(rs.getLong("author_id"));
        n.setNoticeType(rs.getString("notice_type"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setViewCount(rs.getInt("view_count"));

        Timestamp c = rs.getTimestamp("created_at");
        Timestamp u = rs.getTimestamp("updated_at");
        n.setCreatedAt(c != null ? c.toLocalDateTime() : null);
        n.setUpdatedAt(u != null ? u.toLocalDateTime() : null);

        return n;
    }
    
    public NoticeDTO findById(long noticeId) {
        try (Connection conn = DBConnection.getConnection()) {
            return findById(conn, noticeId);
        } catch (Exception e) {
            throw new RuntimeException("NoticeDAO.findById error", e);
        }
    }
}