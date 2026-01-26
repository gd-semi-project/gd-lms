package model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.dto.NoticeDTO;
import model.enumtype.NoticeType;

public class NoticeDAO {

    private static final NoticeDAO instance = new NoticeDAO();
    private NoticeDAO() {}
    public static NoticeDAO getInstance() { return instance; }

    private static final Set<String> ALLOWED_ITEMS = Set.of("title", "content", "notice_type", "all");

    private String buildSearchCondition(String items) {
        if ("all".equals(items)) {
            return "(title LIKE ? OR content LIKE ?)";
        }
        return items + " LIKE ?";
    }

    private boolean isValidSearch(String items, String text) {
        return items != null && text != null
                && !items.isBlank() && !text.isBlank()
                && ALLOWED_ITEMS.contains(items);
    }

    public int increaseViewCount(Connection conn, long noticeId) throws SQLException {
        String sql =
            "UPDATE notice " +
            "SET view_count = view_count + 1 " +
            "WHERE notice_id = ? AND is_deleted = 'N'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, noticeId);
            return pstmt.executeUpdate();
        }
    }

    // ========== 전체 공지사항만 (lecture_id IS NULL) ==========

    public int countOnlyGlobalNotices(Connection conn,String items, String text) throws SQLException{
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NULL"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx++, "%" + text + "%");
                } else {
                    pstmt.setString(idx++, "%" + text + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }

        }
    }

    public List<NoticeDTO> findPageOnlyGlobalNotices(Connection conn,int limit, int offset, String items, String text) throws SQLException{
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NULL"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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
            pstmt.setInt(idx++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDTO(rs));
            }

            return list;

        }
    }

    // ========== 모든 강의 공지사항 (lecture_id IS NOT NULL) ==========

    public int countAllLectureNotices(Connection conn,String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NOT NULL"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hasSearch) {
                if ("all".equals(items)) {
                    pstmt.setString(idx++, "%" + text + "%");
                    pstmt.setString(idx++, "%" + text + "%");
                } else {
                    pstmt.setString(idx++, "%" + text + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }

        }
    }

    public List<NoticeDTO> findPageAllLectureNotices(Connection conn,int limit, int offset, String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NOT NULL"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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
            pstmt.setInt(idx++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDTO(rs));
            }

            return list;

        }
    }

    // STUDENT용
    public int countAllLectureNoticesForStudent(Connection conn,Long userId, String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NOT NULL " +
            "AND lecture_id IN (" +
            "  SELECT lecture_id FROM enrollment " +
            "  WHERE user_id = ? AND status = 'ENROLLED'" +
            ")"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }

        }
    }

    public List<NoticeDTO> findPageAllLectureNoticesForStudent(Connection conn,Long userId, int limit, int offset, String items, String text) throws SQLException{
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NOT NULL " +
            "AND lecture_id IN (" +
            "  SELECT lecture_id FROM enrollment " +
            "  WHERE user_id = ? AND status = 'ENROLLED'" +
            ")"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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
            pstmt.setInt(idx++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDTO(rs));
            }

            return list;

        }
    }

    // INSTRUCTOR용
    public int countAllLectureNoticesForInstructor(Connection conn,Long userId, String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NOT NULL " +
            "AND lecture_id IN (" +
            "  SELECT lecture_id FROM lecture " +
            "  WHERE user_id = ? AND validation = 'CONFIRMED'" +
            ")"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }

        }
    }

    public List<NoticeDTO> findPageAllLectureNoticesForInstructor(Connection conn,Long userId, int limit, int offset, String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id IS NOT NULL " +
            "AND lecture_id IN (" +
            "  SELECT lecture_id FROM lecture " +
            "  WHERE user_id = ? AND validation = 'CONFIRMED'" +
            ")"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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
            pstmt.setInt(idx++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDTO(rs));
            }

            return list;

        }
    }

    // ========== 특정 강의 공지사항 ==========

    public int countByLecture(Connection conn,long lectureId, String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS cnt " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id = ?"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }

        }
    }

    public List<NoticeDTO> findPageByLecture(Connection conn,long lectureId, int limit, int offset, String items, String text) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notice " +
            "WHERE is_deleted = 'N' AND lecture_id = ?"
        );

        boolean hasSearch = isValidSearch(items, text);
        if (hasSearch) sql.append(" AND ").append(buildSearchCondition(items));

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        List<NoticeDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

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
            pstmt.setInt(idx++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDTO(rs));
            }

            return list;

        }
    }

    // ========== 단건 조회 ==========

    public NoticeDTO findById(Connection conn, long noticeId) throws SQLException {
        String sql =
            "SELECT notice_id, lecture_id, author_id, notice_type, title, content, " +
            "created_at, updated_at, view_count " +
            "FROM notice " +
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
            "SELECT n.notice_id, n.lecture_id, n.author_id, n.notice_type, n.title, n.content, " +
            "       n.created_at, n.updated_at, n.view_count, " +
            "       l.lecture_title " +
            "FROM notice n " +
            "JOIN lecture l ON n.lecture_id = l.lecture_id " +
            "WHERE n.is_deleted = 'N' " +
            "  AND n.notice_id = ? " +
            "  AND n.lecture_id = ?";

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
            "INSERT INTO notice (lecture_id, author_id, notice_type, title, content) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (notice.getLectureId() == null) pstmt.setNull(1, Types.BIGINT);
            else pstmt.setLong(1, notice.getLectureId());

            pstmt.setLong(2, notice.getAuthorId());

            // ✅ enum -> DB 문자열
            if (notice.getNoticeType() == null) {
                pstmt.setNull(3, Types.VARCHAR);
            } else {
                pstmt.setString(3, notice.getNoticeType().name());
            }

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
            "UPDATE notice " +
            "SET title = ?, content = ?, updated_at = NOW() " +
            "WHERE is_deleted = 'N' AND notice_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setLong(3, dto.getNoticeId());
            return pstmt.executeUpdate();
        }
    }

    public int softDelete(Connection conn, long noticeId, Long lectureId) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "UPDATE notice SET is_deleted = 'Y' WHERE is_deleted = 'N' AND notice_id = ?"
        );
        if (lectureId != null) sql.append(" AND lecture_id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            pstmt.setLong(idx++, noticeId);
            if (lectureId != null) pstmt.setLong(idx++, lectureId);
            return pstmt.executeUpdate();
        }
    }

    // ========== Helper ==========

    private NoticeDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        NoticeDTO n = new NoticeDTO();
        n.setNoticeId(rs.getLong("notice_id"));
        n.setLectureId(rs.getObject("lecture_id", Long.class));
        n.setAuthorId(rs.getLong("author_id"));

        // ✅ DB 문자열 -> enum
        String nt = rs.getString("notice_type");
        if (nt == null) {
            n.setNoticeType(null);
        } else {
            try {
                n.setNoticeType(NoticeType.valueOf(nt));
            } catch (IllegalArgumentException e) {
                // DB에 enum 범위를 벗어난 값이 들어가 있으면 여기서 터짐 (원인 추적용 메시지 강화)
                throw new SQLException("Invalid notice_type value in DB: " + nt, e);
            }
        }

        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setViewCount(rs.getInt("view_count"));

        Timestamp c = rs.getTimestamp("created_at");
        Timestamp u = rs.getTimestamp("updated_at");
        n.setCreatedAt(c != null ? c.toLocalDateTime() : null);
        n.setUpdatedAt(u != null ? u.toLocalDateTime() : null);
        try {
            n.setLectureTitle(rs.getString("lecture_title"));
        } catch (SQLException ignore) {
            n.setLectureTitle(null);
        }

        return n;
    }


}
