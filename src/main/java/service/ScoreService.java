package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import database.DBConnection;
import model.dao.AttendanceDAO;
import model.dao.ScoreDAO;
import model.dao.SchoolScheduleDAO;
import model.dto.AttendanceSummaryDTO;
import model.dto.ScoreDTO;
import model.enumtype.ScheduleCode;

public class ScoreService {

    private static final ScoreService instance = new ScoreService();

    private final ScoreDAO scoreDAO = ScoreDAO.getInstance();
    private final AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
    private final SchoolScheduleDAO scheduleDAO = SchoolScheduleDAO.getInstance();

    private ScoreService() {}

    public static ScoreService getInstance() {
        return instance;
    }

    /* =========================
     * 성적 페이지 진입용
     * ========================= */

    public List<ScoreDTO> getScoreList(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {

            // 최초 진입 시 score row 생성
            scoreDAO.insertInitialScores(conn, lectureId);

            return scoreDAO.selectScoresByLecture(conn, lectureId);

        } catch (Exception e) {
            throw new RuntimeException("성적 목록 조회 실패", e);
        }
    }

    /* =========================
     * 입력 가능 여부 체크
     * ========================= */

    public boolean isMidtermOpen() {
        try (Connection conn = DBConnection.getConnection()) {
            return scheduleDAO.isWithinPeriod(
                conn,
                ScheduleCode.MIDTERM_EXAM,
                LocalDate.now()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFinalOpen() {
        try (Connection conn = DBConnection.getConnection()) {
            return scheduleDAO.isWithinPeriod(
                conn,
                ScheduleCode.FINAL_EXAM,
                LocalDate.now()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* =========================
     * 성적 저장
     * ========================= */

    public void saveScores(
            Long lectureId,
            List<ScoreDTO> scores,
            boolean midtermDisabled,
            boolean finalDisabled
    ) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (ScoreDTO dto : scores) {

                // 출석 점수 계산
                AttendanceSummaryDTO attendance =
                        attendanceDAO.getAttendanceSummary(
                                conn,
                                lectureId,
                                dto.getStudentId()
                        );

                dto.setAttendanceScore(attendance.getAttendanceScore());

                // 입력 누락 검증 (disable 제외)
                if (hasNullScore(dto, midtermDisabled, finalDisabled)) {
                    throw new IllegalStateException(
                        "모든 학생의 점수를 입력해주세요."
                    );
                }

                dto.setCompleted(true);
                scoreDAO.updateScore(conn, dto);
            }

            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException("성적 저장 실패", e);
        }
    }

    /* =========================
     * 학점 계산
     * ========================= */

    public void calculateGrade(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            List<ScoreDTO> list =
                    scoreDAO.selectScoresByLecture(conn, lectureId);

            for (ScoreDTO dto : list) {

                if (!dto.isCompleted()) {
                    throw new IllegalStateException(
                        "모든 성적이 입력되지 않았습니다."
                    );
                }

                int total =
                        dto.getAttendanceScore()
                      + dto.getAssignmentScore()
                      + dto.getMidtermScore()
                      + dto.getFinalScore();

                String grade = convertGrade(total);

                scoreDAO.updateTotalAndGrade(
                        conn,
                        dto.getScoreId(),
                        total,
                        grade
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("학점 계산 실패", e);
        }
    }

    /* =========================
     * 내부 유틸
     * ========================= */

    private boolean hasNullScore(
            ScoreDTO dto,
            boolean midtermDisabled,
            boolean finalDisabled
    ) {
        if (dto.getAssignmentScore() == null) return true;
        if (!midtermDisabled && dto.getMidtermScore() == null) return true;
        if (!finalDisabled && dto.getFinalScore() == null) return true;
        return false;
    }

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
}