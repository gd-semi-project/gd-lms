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

    /* ==================================================
     * 1. 성적 페이지 진입
     *    - score row 자동 생성
     *    - 출석 점수는 "조회 시 계산"
     * ================================================== */
    public List<ScoreDTO> getScoreList(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            // score row 자동 생성
            scoreDAO.insertInitialScores(conn, lectureId);

            List<ScoreDTO> scores =
                    scoreDAO.selectScoresByLecture(conn, lectureId);

            // 🔥 출석 점수는 여기서만 계산
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
     * 2. 중간 / 기말 입력 가능 여부
     * ================================================== */
    public boolean isMidtermOpen() {
        try (Connection conn = DBConnection.getConnection()) {
            return scheduleDAO.isWithinPeriod(
                    conn,
                    ScheduleCode.MIDTERM_EXAM,
                    LocalDate.now()
            );
        } catch (Exception e) {
            throw new RuntimeException("중간고사 기간 조회 실패", e);
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
            throw new RuntimeException("기말고사 기간 조회 실패", e);
        }
    }

    /* ==================================================
     * 3. 성적 저장
     *    - 출석은 검증용으로만 계산
     *    - DB 저장 ❌
     * ================================================== */
    public void saveScores(
            Long lectureId,
            List<ScoreDTO> scores,
            boolean midtermDisabled,
            boolean finalDisabled
    ) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (ScoreDTO dto : scores) {

                // 🔍 출석 계산 (검증/참고용)
                attendanceDAO.getAttendanceSummary(
                        conn,
                        lectureId,
                        dto.getStudentId()
                );

                // 입력 누락 검증
                if (hasNullScore(dto, midtermDisabled, finalDisabled)) {
                    throw new IllegalStateException(
                            "모든 학생의 점수를 입력해주세요."
                    );
                }

                dto.setCompleted(true);

                // 🔥 출석 점수는 저장하지 않는다
                scoreDAO.updateScore(conn, dto);
            }

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException("성적 저장 실패", e);
        }
    }

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

                // 🔥 출석 요약 다시 조회
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

                // 🔥 출석률 계산
                double attendanceRate =
                        totalSessions > 0
                        ? (double) effectiveAttend / totalSessions
                        : 0;

                // 🚨 출석 70% 미만 → 자동 F
                if (attendanceRate < 0.8) {

                    scoreDAO.updateTotalAndGrade(
                            conn,
                            dto.getScoreId(),
                            0,
                            "F"
                    );
                    continue; // 다음 학생
                }

                // 정상 학생만 총점 계산
                int attendanceScore =
                        dto.getAttendanceScore() != null
                        ? dto.getAttendanceScore() : 0;
                int assignment =
                        dto.getAssignmentScore() != null
                        ? dto.getAssignmentScore() : 0;
                int midterm =
                        dto.getMidtermScore() != null
                        ? dto.getMidtermScore() : 0;
                int finals =
                        dto.getFinalScore() != null
                        ? dto.getFinalScore() : 0;

                int total =
                        attendanceScore
                      + assignment
                      + midterm
                      + finals;

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

    /* ==================================================
     * 내부 유틸
     * ================================================== */

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