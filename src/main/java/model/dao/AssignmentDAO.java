// AssignmentDAO.java
package model.dao;

import model.dto.AssignmentDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {
    private static final AssignmentDAO instance = new AssignmentDAO();
    private AssignmentDAO() {}
    public static AssignmentDAO getInstance() { return instance; }

    // 강의별 과제 목록
    public List<AssignmentDTO> selectByLecture(Connection conn, long lectureId) throws SQLException {
        String sql = """
            SELECT assignment_id, lecture_id, title, content, due_date, 
                   max_score, created_at, updated_at
            FROM assignment
            WHERE lecture_id = ? AND is_deleted = 'N'
            ORDER BY due_date DESC
            """;
        
        List<AssignmentDTO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssignmentDTO dto = new AssignmentDTO();
                    dto.setAssignmentId(rs.getLong("assignment_id"));
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));                 
                    Timestamp dueTs = rs.getTimestamp("due_date");
                    dto.setDueDate(dueTs != null ? dueTs.toLocalDateTime() : null);

                    Timestamp createdTs = rs.getTimestamp("created_at");
                    dto.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
                    
                    dto.setMaxScore(rs.getInt("max_score"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 과제 단건 상세 조회
    public AssignmentDTO selectById(Connection conn, long assignmentId, long lectureId) throws SQLException {
        String sql = """
            SELECT assignment_id, lecture_id, title, content, due_date,
                   max_score, created_at, updated_at
            FROM assignment
            WHERE assignment_id = ? AND lecture_id = ? AND is_deleted = 'N'
            """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, assignmentId);
            ps.setLong(2, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AssignmentDTO dto = new AssignmentDTO();
                    dto.setAssignmentId(rs.getLong("assignment_id"));
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    
                    Timestamp dueTs = rs.getTimestamp("due_date");
                    dto.setDueDate(dueTs != null ? dueTs.toLocalDateTime() : null);
                    
                    Timestamp createdTs = rs.getTimestamp("created_at");
                    dto.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
                    
                    dto.setMaxScore(rs.getInt("max_score"));
                   
                    return dto;
                }
            }
        }
        return null;
    }

    // 과제 생성
    public long insert(Connection conn, AssignmentDTO dto) throws SQLException {
        String sql = """
            INSERT INTO assignment (lecture_id, title, content, due_date, max_score)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, dto.getLectureId());
            ps.setString(2, dto.getTitle());
            ps.setString(3, dto.getContent());
            
            if (dto.getDueDate() == null) {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(4, Timestamp.valueOf(dto.getDueDate()));
            }
            
            ps.setInt(5, dto.getMaxScore() != null ? dto.getMaxScore() : 100);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    // 과제 수정
    public int update(Connection conn, AssignmentDTO dto) throws SQLException {
        String sql = """
            UPDATE assignment
            SET title = ?, content = ?, due_date = ?, max_score = ?
            WHERE assignment_id = ? AND lecture_id = ?
            """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());
            
            if (dto.getDueDate() == null) {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(3, Timestamp.valueOf(dto.getDueDate()));
            }
            
            ps.setInt(4, dto.getMaxScore());
            ps.setLong(5, dto.getAssignmentId());
            ps.setLong(6, dto.getLectureId());
            return ps.executeUpdate();
        }
    }

    // 과제 삭제
    public int softDelete(Connection conn, long assignmentId) throws SQLException {
        String sql = "UPDATE assignment SET is_deleted = 'Y' WHERE assignment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, assignmentId);
            return ps.executeUpdate();
        }
    }
}