package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

  // 수강생 목록 조회 : 지윤
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

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LectureStudentDTO dto = new LectureStudentDTO();
					dto.setStudentId(rs.getLong("student_id"));
					dto.setUserId(rs.getLong("user_id"));
					dto.setStudentName(rs.getString("name"));
					dto.setStudentNumber(rs.getInt("student_number"));
					dto.setStudenGrade(rs.getInt("student_grade"));
					dto.setEnrollmentStatus(EnrollmentStatus.valueOf(rs.getString("status")));
					dto.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());

					list.add(dto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("수강생 목록 조회 실패", e);
		}

		return list;
	}


   // 학생ID로 특정강의ID 이수 확인 메소드
   public boolean isStudentEnrolled(Connection conn, long userId, long lectureId) throws SQLException {
        String sql = 
            "SELECT COUNT(*) AS cnt " +
            "FROM enrollment " +
            "WHERE user_id = ? AND lecture_id = ? AND status = 'ACTIVE'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0;
            }
        }
    }


   // 특정 학생이 듣는 모든 강의 리스트 메소드
   public List<Long> findEnrolledLectureIds(Connection conn, long userId) throws SQLException {
        String sql = 
            "SELECT lecture_id " +
            "FROM enrollment " +
            "WHERE user_id = ? AND status = 'ACTIVE'";

        List<Long> lectureIds = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lectureIds.add(rs.getLong("lecture_id"));
                }
            }
        }

        return lectureIds;
    }

   // 특정 강의를 수강 중인 학생 수
   public int countStudentsByLecture(Connection conn, long lectureId) throws SQLException {
          String sql = 
              "SELECT COUNT(*) AS cnt " +
              "FROM enrollment " +
              "WHERE lecture_id = ? AND status = 'ACTIVE'";

          try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
              pstmt.setLong(1, lectureId);

              try (ResultSet rs = pstmt.executeQuery()) {
                  return rs.next() ? rs.getInt("cnt") : 0;
              }
          }
      }



}