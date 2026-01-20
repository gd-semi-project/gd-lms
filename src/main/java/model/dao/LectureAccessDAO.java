package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LectureAccessDAO {

    private static final LectureAccessDAO instance = new LectureAccessDAO();
    private LectureAccessDAO() {}
    public static LectureAccessDAO getInstance() { return instance; }

    // ====== 강의 존재 여부 ======
    public boolean lectureExists(Connection conn, long lectureId) throws SQLException {
        String sql = "SELECT 1 FROM lecture WHERE lecture_id = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ====== 교수: 본인 강의인지 ======
    // lecture.user_id = 강사 식별자(DDL 기준)
    public boolean isInstructorOfLecture(Connection conn, long instructorUserId, long lectureId) throws SQLException {
        String sql = "SELECT 1 FROM lecture WHERE lecture_id = ? AND user_id = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setLong(2, instructorUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ====== 학생: 수강중인지 ======
    // enrollment.student_id, status ENUM('ENROLLED','DROPPED')
    public boolean isEnrolledStudent(Connection conn, long studentUserId, long lectureId) throws SQLException {
        String sql =
            "SELECT 1 " +
            "FROM enrollment " +
            "WHERE lecture_id = ? AND student_id = ? AND status = 'ENROLLED' " +
            "LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setLong(2, studentUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
}
