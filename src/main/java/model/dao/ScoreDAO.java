package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.dto.ScoreDTO;

public class ScoreDAO {

    private static final ScoreDAO instance = new ScoreDAO();

    private ScoreDAO() {}

    public static ScoreDAO getInstance() {
        return instance;
    }

    public List<ScoreDTO> selectScoresByLecture(
            Connection conn,
            Long lectureId
    ) {

        String sql = """
            SELECT
                s.score_id,
                s.lecture_id,
                s.student_id,

                u.name AS student_name,
                st.student_number,
                st.student_grade,

                s.attendance_score,
                s.assignment_score,
                s.midterm_score,
                s.final_score,
                s.total_score,
                s.grade_letter,

                s.is_completed,
                s.is_confirmed
            FROM score s
            JOIN student st ON st.student_id = s.student_id
            JOIN user u ON u.user_id = st.user_id
            WHERE s.lecture_id = ?
            ORDER BY st.student_number
        """;

        List<ScoreDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ScoreDTO dto = new ScoreDTO();

                dto.setScoreId(rs.getLong("score_id"));
                dto.setLectureId(rs.getLong("lecture_id"));
                dto.setStudentId(rs.getLong("student_id"));

                dto.setStudentName(rs.getString("student_name"));
                dto.setStudentNumber(rs.getLong("student_number"));
                dto.setStudentGrade(rs.getInt("student_grade"));

                dto.setAttendanceScore(rs.getInt("attendance_score"));
                dto.setAssignmentScore(rs.getInt("assignment_score"));
                dto.setMidtermScore(rs.getInt("midterm_score"));
                dto.setFinalScore(rs.getInt("final_score"));

                dto.setTotalScore(rs.getInt("total_score"));
                dto.setGradeLetter(rs.getString("grade_letter"));

                dto.setCompleted(rs.getBoolean("is_completed"));
                dto.setConfirmed(rs.getBoolean("is_confirmed"));

                list.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("성적 목록 조회 실패", e);
        }

        return list;
    }

    public void updateScore(Connection conn, ScoreDTO dto) {

        String sql = """
            UPDATE score
            SET
                attendance_score = ?,
                assignment_score = ?,
                midterm_score = ?,
                final_score = ?,
                is_completed = ?
            WHERE score_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getAttendanceScore());

            if (dto.getAssignmentScore() != null)
                pstmt.setInt(2, dto.getAssignmentScore());
            else
                pstmt.setNull(2, java.sql.Types.INTEGER);

            if (dto.getMidtermScore() != null)
                pstmt.setInt(3, dto.getMidtermScore());
            else
                pstmt.setNull(3, java.sql.Types.INTEGER);

            if (dto.getFinalScore() != null)
                pstmt.setInt(4, dto.getFinalScore());
            else
                pstmt.setNull(4, java.sql.Types.INTEGER);

            pstmt.setBoolean(5, dto.isCompleted());
            pstmt.setLong(6, dto.getScoreId());

            pstmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("성적 저장 실패", e);
        }
    }

    public void updateTotalAndGrade(
            Connection conn,
            Long scoreId,
            int totalScore,
            String gradeLetter
    ) {

        String sql = """
            UPDATE score
            SET
                total_score = ?,
                grade_letter = ?,
                is_confirmed = TRUE
            WHERE score_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, totalScore);
            pstmt.setString(2, gradeLetter);
            pstmt.setLong(3, scoreId);

            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("총점/학점 저장 실패", e);
        }
    }

    public void insertInitialScores(Connection conn, Long lectureId) {

        String sql = """
            INSERT INTO score (lecture_id, student_id)
            SELECT ?, e.user_id
            FROM enrollment e
            WHERE e.lecture_id = ?
              AND e.status = 'ENROLLED'
              AND NOT EXISTS (
                  SELECT 1 FROM score s
                  WHERE s.lecture_id = ?
                    AND s.student_id = e.user_id
              )
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);
            pstmt.setLong(2, lectureId);
            pstmt.setLong(3, lectureId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("초기 성적 row 생성 실패", e);
        }
    }
}