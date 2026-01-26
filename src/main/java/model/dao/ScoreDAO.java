package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.dto.ScoreDTO;

public class ScoreDAO {

	private static final ScoreDAO instance = new ScoreDAO();

	private ScoreDAO() {
	}

	public static ScoreDAO getInstance() {
		return instance;
	}

	// 성적/출석용 수강생 기준 score row 최초 생성
	public void insertInitialScores(Connection conn, Long lectureId) {

		String sql = """
				    INSERT INTO score (lecture_id, student_id)
				    SELECT ?, st.student_id
				    FROM enrollment e
				    JOIN student st
				        ON st.user_id = e.user_id
				    WHERE e.lecture_id = ?
				      AND e.status = 'ENROLLED'
				      AND NOT EXISTS (
				          SELECT 1
				          FROM score s
				          WHERE s.lecture_id = ?
				            AND s.student_id = st.student_id
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

	// 강의별 성적 목록 조회
	public List<ScoreDTO> selectScoresByLecture(Connection conn, Long lectureId) {

		String sql = """
				         SELECT
				    s.score_id,
				    s.lecture_id,
				    s.student_id,

				    u.name AS student_name,
				    st.student_number,
				    st.student_grade,

				    s.assignment_score,
				    s.midterm_score,
				    s.final_score,
				    s.total_score,
				    s.grade_letter,

				    s.is_completed,
				    s.is_confirmed
				FROM score s
				         JOIN student st
				             ON st.student_id = s.student_id
				         JOIN user u
				             ON u.user_id = st.user_id
				         WHERE s.lecture_id = ?
				         ORDER BY st.student_number
				     """;

		List<ScoreDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ScoreDTO dto = new ScoreDTO();

					dto.setScoreId(rs.getLong("score_id"));
					dto.setLectureId(rs.getLong("lecture_id"));
					dto.setStudentId(rs.getLong("student_id"));

					dto.setStudentName(rs.getString("student_name"));
					dto.setStudentNumber(rs.getLong("student_number"));
					dto.setStudentGrade(rs.getInt("student_grade"));

					dto.setAssignmentScore(rs.getInt("assignment_score"));
					if (rs.wasNull())
						dto.setAssignmentScore(null);

					dto.setMidtermScore(rs.getInt("midterm_score"));
					if (rs.wasNull())
						dto.setMidtermScore(null);

					dto.setFinalScore(rs.getInt("final_score"));
					if (rs.wasNull())
						dto.setFinalScore(null);

					dto.setTotalScore(rs.getInt("total_score"));
					if (rs.wasNull())
						dto.setTotalScore(null);

					dto.setGradeLetter(rs.getString("grade_letter"));

					dto.setCompleted(rs.getBoolean("is_completed"));
					dto.setConfirmed(rs.getBoolean("is_confirmed"));

					list.add(dto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("성적 목록 조회 실패", e);
		}

		return list;
	}

	// 성적 저장
	public void updateScore(Connection conn, ScoreDTO dto) {

		String sql = """
				    UPDATE score
				    SET
				        assignment_score = ?,
				        midterm_score = ?,
				        final_score = ?,
				        is_completed = ?
				    WHERE lecture_id = ?
				      AND student_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			if (dto.getAssignmentScore() != null)
				pstmt.setInt(1, dto.getAssignmentScore());
			else
				pstmt.setNull(1, java.sql.Types.INTEGER);

			if (dto.getMidtermScore() != null)
				pstmt.setInt(2, dto.getMidtermScore());
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);

			if (dto.getFinalScore() != null)
				pstmt.setInt(3, dto.getFinalScore());
			else
				pstmt.setNull(3, java.sql.Types.INTEGER);

			pstmt.setBoolean(4, dto.isCompleted());
			pstmt.setLong(5, dto.getLectureId());
			pstmt.setLong(6, dto.getStudentId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("성적 저장 실패", e);
		}
	}

	// 총점 / 학점 계산 결과 저장
	public void updateTotalAndGrade(Connection conn, Long scoreId, int totalScore, String gradeLetter) {

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

	// 학생 본인 성적 조회
	public ScoreDTO selectScoreByLectureAndStudent(Connection conn, Long lectureId, Long studentId) {

		String sql = """
				    SELECT
				        s.score_id,
				        s.lecture_id,
				        s.student_id,
				        s.assignment_score,
				        s.midterm_score,
				        s.final_score
				    FROM score s
				    WHERE s.lecture_id = ?
				      AND s.student_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, lectureId);
			pstmt.setLong(2, studentId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					ScoreDTO dto = new ScoreDTO();
					dto.setScoreId(rs.getLong("score_id"));
					dto.setLectureId(rs.getLong("lecture_id"));
					dto.setStudentId(rs.getLong("student_id"));

					dto.setAssignmentScore(rs.getObject("assignment_score", Integer.class));
					dto.setMidtermScore(rs.getObject("midterm_score", Integer.class));
					dto.setFinalScore(rs.getObject("final_score", Integer.class));

					return dto;
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("학생 성적 조회 실패", e);
		}

		return null;
	}

	// 학생 본인의 전체성적 페이지 조회
	public List<ScoreDTO> selectMytotScore(Connection conn, Long userId) {

		String sql = """
				    SELECT
				    s.score_id,
				    s.lecture_id,
				    s.student_id,

				    l.lecture_title,
				    l.lecture_round,
				    l.section,
				    l.start_date,
				    l.end_date,

				    s.attendance_score,
				    s.assignment_score,
				    s.midterm_score,
				    s.final_score,
				    s.total_score,
				    s.grade_letter,

				    s.is_completed,
				    s.is_confirmed
				FROM score s
				JOIN student st
				    ON st.student_id = s.student_id
				JOIN lecture l
				    ON l.lecture_id = s.lecture_id
				WHERE st.user_id = ?
				AND s.is_confirmed = TRUE
				ORDER BY
				    l.start_date DESC,
				    l.lecture_title
				""";

		List<ScoreDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {

					ScoreDTO dto = new ScoreDTO();

					dto.setScoreId(rs.getLong("score_id"));
					dto.setLectureId(rs.getLong("lecture_id"));
					dto.setStudentId(rs.getLong("student_id"));

					// 과목 정보
					dto.setLectureTitle(rs.getString("lecture_title"));
					dto.setLectureRound(rs.getInt("lecture_round"));
					dto.setSection(rs.getString("section"));
					dto.setStartDate(rs.getDate("start_date").toLocalDate());

					// 점수
					dto.setAttendanceScore(rs.getObject("attendance_score", Integer.class));
					dto.setAssignmentScore(rs.getObject("assignment_score", Integer.class));
					dto.setMidtermScore(rs.getObject("midterm_score", Integer.class));
					dto.setFinalScore(rs.getObject("final_score", Integer.class));
					dto.setTotalScore(rs.getObject("total_score", Integer.class));

					dto.setGradeLetter(rs.getString("grade_letter"));
					dto.setCompleted(rs.getBoolean("is_completed"));
					dto.setConfirmed(rs.getBoolean("is_confirmed"));

					list.add(dto);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("학생 개인 전체 성적 조회 실패", e);
		}

		return list;
	}

}