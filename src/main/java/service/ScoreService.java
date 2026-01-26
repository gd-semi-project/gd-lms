package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.AttendanceDAO;
import model.dao.ScoreDAO;
import model.dao.ScorePolicyDAO;
import model.dto.AttendanceSummaryDTO;
import model.dto.ScoreDTO;
import model.dto.ScorePolicyDTO;

public class ScoreService {

    private static final ScoreService instance = new ScoreService();

    private final ScoreDAO scoreDAO = ScoreDAO.getInstance();
    private final AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
    private final ScorePolicyDAO scorePolicyDAO =
            ScorePolicyDAO.getInstance();

    private ScoreService() {}

    public static ScoreService getInstance() {
        return instance;
    }

    /* ==================================================
     * 1. 성적 조회
     * ================================================== */
    public List<ScoreDTO> getScoreList(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            scoreDAO.insertInitialScores(conn, lectureId);

            List<ScoreDTO> scores =
                    scoreDAO.selectScoresByLecture(conn, lectureId);

            for (ScoreDTO dto : scores) {
                AttendanceSummaryDTO summary =
                        attendanceDAO.getAttendanceSummary(
                                conn,
                                lectureId,
                                dto.getStudentId()
                        );

                int attendanceScore =
                        summary != null ? summary.getAttendanceScore() : 0;

                dto.setAttendanceScore(attendanceScore);
            }

            return scores;

        } catch (Exception e) {
            throw new RuntimeException("성적 목록 조회 실패", e);
        }
    }

    /* ==================================================
     * 2. 성적 저장 (부분 저장 허용)
     * ================================================== */
    public void saveScores(
            Long lectureId,
            List<ScoreDTO> scores
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            boolean hasAnyAssignment = false;
            boolean hasAnyMidterm = false;
            boolean hasAnyFinal = false;

            boolean hasEmptyAssignment = false;
            boolean hasEmptyMidterm = false;
            boolean hasEmptyFinal = false;

            // 1️⃣ 전체 상태 스캔
            for (ScoreDTO dto : scores) {

                if (dto.getAssignmentScore() != null)
                    hasAnyAssignment = true;
                else
                    hasEmptyAssignment = true;

                if (dto.getMidtermScore() != null)
                    hasAnyMidterm = true;
                else
                    hasEmptyMidterm = true;

                if (dto.getFinalScore() != null)
                    hasAnyFinal = true;
                else
                    hasEmptyFinal = true;
            }

            // 2️⃣ 컬럼 단위 검증
            if (hasAnyAssignment && hasEmptyAssignment) {
                throw new IllegalStateException(
                    "과제 점수는 모든 학생에게 입력해야 저장할 수 있습니다."
                );
            }

            if (hasAnyMidterm && hasEmptyMidterm) {
                throw new IllegalStateException(
                    "중간고사 점수는 모든 학생에게 입력해야 저장할 수 있습니다."
                );
            }

            if (hasAnyFinal && hasEmptyFinal) {
                throw new IllegalStateException(
                    "기말고사 점수는 모든 학생에게 입력해야 저장할 수 있습니다."
                );
            }

            // 3️⃣ 통과하면 저장
            for (ScoreDTO dto : scores) {
                scoreDAO.updateScore(conn, dto);
            }

            conn.commit();

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("성적 저장 실패", e);
        }
    }

    /* ==================================================
     * 3. 학점 계산
     * ================================================== */
    public void calculateGrade(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            ScorePolicyDTO policy =
                    scorePolicyDAO.findByLectureId(conn, lectureId);

            if (policy == null) {
                throw new IllegalStateException(
                    "성적 배점이 설정되지 않았습니다."
                );
            }

            List<ScoreDTO> list =
                    scoreDAO.selectScoresByLecture(conn, lectureId);

            // 🔥 1차 검증: 미입력 존재 여부
            for (ScoreDTO dto : list) {
                if (dto.getAssignmentScore() == null
                    || dto.getMidtermScore() == null
                    || dto.getFinalScore() == null) {

                    throw new IllegalStateException(
                        "모든 학생의 과제 / 중간 / 기말 점수를 입력해야 합니다."
                    );
                }
            }

            // 🔥 2차 계산
            for (ScoreDTO dto : list) {

                AttendanceSummaryDTO attendance =
                        attendanceDAO.getAttendanceSummary(
                                conn,
                                lectureId,
                                dto.getStudentId()
                        );

                int totalSessions =
                        attendance != null
                        ? attendance.getTotalSessionCount()
                        : 0;

                int effectiveAttend =
                        attendance != null
                        ? attendance.getEffectiveAttendCount()
                        : 0;

                double attendanceRate =
                        totalSessions > 0
                        ? (double) effectiveAttend / totalSessions
                        : 0;

                // 출석 미달 → F
                if (attendanceRate < 0.8) {
                    scoreDAO.updateTotalAndGrade(
                            conn,
                            dto.getScoreId(),
                            0,
                            "F"
                    );
                    continue;
                }

                int attendanceScore =
                        dto.getAttendanceScore() != null
                        ? dto.getAttendanceScore() : 0;

                int assignment = dto.getAssignmentScore();
                int midterm = dto.getMidtermScore();
                int finals = dto.getFinalScore();

                double weighted =
                      attendanceScore * policy.getAttendanceWeight() / 100.0
                    + assignment       * policy.getAssignmentWeight() / 100.0
                    + midterm          * policy.getMidtermWeight() / 100.0
                    + finals           * policy.getFinalWeight() / 100.0;

                int total = (int) Math.round(weighted);
                String grade = convertGrade(total);

                scoreDAO.updateTotalAndGrade(
                        conn,
                        dto.getScoreId(),
                        total,
                        grade
                );
            }

        } catch (IllegalStateException e) {
            throw e; // ⚠ Controller에서 잡아서 경고 처리
        } catch (Exception e) {
            throw new RuntimeException("학점 계산 실패", e);
        }
    }
    
    
 // 학생 본인 성적 조회
    public ScoreDTO getMyScore(
            Long lectureId,
            Long studentId
    ) {

        try (Connection conn = DBConnection.getConnection()) {

            // 혹시 score row 없으면 생성
            scoreDAO.insertInitialScores(conn, lectureId);

            ScoreDTO dto =
                    scoreDAO.selectScoreByLectureAndStudent(
                            conn,
                            lectureId,
                            studentId
                    );

            if (dto == null) return null;

            AttendanceSummaryDTO summary =
                    attendanceDAO.getAttendanceSummary(
                            conn,
                            lectureId,
                            studentId
                    );

            int attendanceScore =
                    summary != null ? summary.getAttendanceScore() : 0;

            dto.setAttendanceScore(attendanceScore);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("내 성적 조회 실패", e);
        }
    }

    /* ==================================================
     * 내부 유틸
     * ================================================== */
    private String convertGrade(int total) {
        if (total >= 95) return "A+";
        if (total >= 90) return "A";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "C+";
        if (total >= 70) return "C";
        if (total >= 60) return "D";
        return "F";
    }
    
    // 학생 본인의 전체성적 조회
    public List<ScoreDTO> getMytotScore(Long userId){
    	try {
			Connection conn = DBConnection.getConnection();
				List<ScoreDTO> list = scoreDAO.selectMytotScore(conn, userId);
				return list;
			
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("학생 전체 성적 조회 실패", e);
		}
    	
    }
    
}





