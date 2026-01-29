package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import database.DBConnection;
import exception.BadRequestException;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import model.dao.AttendanceDAO;
import model.dao.SchoolScheduleDAO;
import model.dao.ScoreDAO;
import model.dao.ScorePolicyDAO;
import model.dto.AttendanceSummaryDTO;
import model.dto.SchoolScheduleDTO;
import model.dto.ScoreDTO;
import model.dto.ScorePolicyDTO;
import model.enumtype.ScheduleCode;
import utils.AppDateTime;
import utils.AppTime;

public class ScoreService {

	private static final ScoreService instance = new ScoreService();

	private final ScoreDAO scoreDAO = ScoreDAO.getInstance();
	private final AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
	private final ScorePolicyDAO scorePolicyDAO = ScorePolicyDAO.getInstance();
	private final SchoolScheduleDAO schoolScheduleDAO = SchoolScheduleDAO.getInstance();

	private ScoreService() {
	}

	public static ScoreService getInstance() {
		return instance;
	}

	// 성적 조회
	public List<ScoreDTO> getScoreList(Long lectureId) {

		try (Connection conn = DBConnection.getConnection()) {

			scoreDAO.insertInitialScores(conn, lectureId);

			List<ScoreDTO> scores = scoreDAO.selectScoresByLecture(conn, lectureId);

			for (ScoreDTO dto : scores) {
				AttendanceSummaryDTO summary = attendanceDAO.getAttendanceSummary(conn, lectureId, dto.getStudentId());

				int attendanceScore = summary != null ? summary.getAttendanceScore() : 0;

				dto.setAttendanceScore(attendanceScore);
			}

			return scores;

		} catch (Exception e) {
            throw new InternalServerException("성적 목록 조회 실패", e);
        }
	}

	// 성적 저장
	public void saveScores(Long lectureId, List<ScoreDTO> scores) {
	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        boolean midtermOpen = isMidtermInputOpen();
	        boolean finalOpen = isFinalInputOpen();

	        boolean hasAnyMidterm = false;
	        boolean hasAnyFinal = false;
	        boolean hasEmptyMidterm = false;
	        boolean hasEmptyFinal = false;

	        for (ScoreDTO dto : scores) {

	            if (dto.getMidtermScore() != null) hasAnyMidterm = true;
	            else hasEmptyMidterm = true;

	            if (dto.getFinalScore() != null) hasAnyFinal = true;
	            else hasEmptyFinal = true;
	        }

	        if (hasAnyMidterm && hasEmptyMidterm) {
	            throw new BadRequestException("중간고사 점수는 모든 학생에게 입력해야 저장할 수 있습니다.");
	        }

	        if (hasAnyFinal && hasEmptyFinal) {
	            throw new BadRequestException("기말고사 점수는 모든 학생에게 입력해야 저장할 수 있습니다.");
	        }

	        for (ScoreDTO dto : scores) {

	            ScoreDTO origin =
	                scoreDAO.selectScoreByLectureAndStudent(conn, lectureId, dto.getStudentId());

	            if (!midtermOpen) {
	                dto.setMidtermScore(origin.getMidtermScore());
	            }

	            if (!finalOpen) {
	                dto.setFinalScore(origin.getFinalScore());
	            }

	            scoreDAO.updateScore(conn, dto);
	        }

	        conn.commit();

	    } catch (BadRequestException e) {
	        throw e;
	    } catch (Exception e) {
	        throw new InternalServerException("성적 저장 실패", e);
	    }
	}

	// 학점 계산
	public void calculateGrade(Long lectureId) {

		try (Connection conn = DBConnection.getConnection()) {

			ScorePolicyDTO policy = scorePolicyDAO.findByLectureId(conn, lectureId);

			if (policy == null) {
                throw new ResourceNotFoundException("성적 배점이 설정되지 않았습니다.");
            }

			List<ScoreDTO> list = scoreDAO.selectScoresByLecture(conn, lectureId);

			// 1차 검증: 미입력 존재 여부
			for (ScoreDTO dto : list) {
				if (dto.getAssignmentScore() == null || dto.getMidtermScore() == null || dto.getFinalScore() == null) {

					throw new BadRequestException("모든 학생의 점수를 입력해야 학점 계산이 가능합니다.");
				}
			}

			// 2차 계산
			for (ScoreDTO dto : list) {

			    AttendanceSummaryDTO attendance =
			        attendanceDAO.getAttendanceSummary(conn, lectureId, dto.getStudentId());

			    int totalSessions = attendance != null ? attendance.getTotalSessionCount() : 0;
			    int effectiveAttend = attendance != null ? attendance.getEffectiveAttendCount() : 0;

			    double attendanceRate = totalSessions > 0 ? (double) effectiveAttend / totalSessions : 0;

			    // 출석 미달 → F
			    if (attendanceRate < 0.8) {
			        scoreDAO.updateTotalAndGrade(conn, dto.getScoreId(), 0, "F");
			        continue;
			    }

			    int attendanceScore = attendance != null ? attendance.getAttendanceScore() : 0;

			    int assignment = dto.getAssignmentScore();
			    int midterm = dto.getMidtermScore();
			    int finals = dto.getFinalScore();

			    double weighted =
			          attendanceScore * policy.getAttendanceWeight() / 100.0
			        + assignment * policy.getAssignmentWeight() / 100.0
			        + midterm * policy.getMidtermWeight() / 100.0
			        + finals * policy.getFinalWeight() / 100.0;

			    int total = (int) Math.round(weighted);
			    String grade = convertGrade(total);

			    scoreDAO.updateTotalAndGrade(conn, dto.getScoreId(), total, grade);
			}

        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("학점 계산 실패", e);
        }
	}

	// 학생 본인 성적 조회
	public ScoreDTO getMyScore(Long lectureId, Long studentId) {

		try (Connection conn = DBConnection.getConnection()) {

			scoreDAO.insertInitialScores(conn, lectureId);

			ScoreDTO dto = scoreDAO.selectScoreByLectureAndStudent(conn, lectureId, studentId);

			if (dto == null)
				return null;

			AttendanceSummaryDTO summary = attendanceDAO.getAttendanceSummary(conn, lectureId, studentId);

			int attendanceScore = summary != null ? summary.getAttendanceScore() : 0;

			dto.setAttendanceScore(attendanceScore);

			return dto;

		} catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("내 성적 조회 실패", e);
        }
	}

	// 내부 유틸
	private String convertGrade(int total) {
		if (total >= 95)
			return "A+";
		if (total >= 90)
			return "A";
		if (total >= 85)
			return "B+";
		if (total >= 80)
			return "B";
		if (total >= 75)
			return "C+";
		if (total >= 70)
			return "C";
		if (total >= 60)
			return "D";
		return "F";
	}

	// 학생 본인의 전체성적 조회
	public List<ScoreDTO> getMytotScore(Long userId) {
		try {
			Connection conn = DBConnection.getConnection();
			List<ScoreDTO> list = scoreDAO.selectMytotScore(conn, userId);
			return list;

		} catch (Exception e) {
			throw new RuntimeException("학생 전체 성적 조회 실패", e);
		}

	}

	// 중간고사 입력 가능 여부
	public boolean isMidtermInputOpen() {
	    try (Connection conn = DBConnection.getConnection()) {
	        LocalDate today = AppDateTime.today();

	        return schoolScheduleDAO.isWithinPeriod(
	            conn,
	            ScheduleCode.MIDTERM_GRADE_APPEAL,
	            today
	        );

	    } catch (Exception e) {
	        throw new InternalServerException("중간고사 입력 기간 확인 실패", e);
	    }
	}

	// 기말고사 입력 가능 여부
	public boolean isFinalInputOpen() {
	    try (Connection conn = DBConnection.getConnection()) {
	        LocalDate today = AppDateTime.today();

	        return schoolScheduleDAO.isWithinPeriod(
	            conn,
	            ScheduleCode.GRADE_INPUT_INSTRUCTOR,
	            today
	        );

	    } catch (Exception e) {
	        throw new InternalServerException("기말고사 입력 기간 확인 실패", e);
	    }
	}

	// 학점 계산 가능 여부
	public boolean isGradeCalcOpen() {
	    try (Connection conn = DBConnection.getConnection()) {
	        LocalDate today = AppDateTime.today();

	        return schoolScheduleDAO.isWithinPeriod(
	            conn,
	            ScheduleCode.GRADE_INPUT_INSTRUCTOR,
	            today
	        );

	    } catch (Exception e) {
	        throw new InternalServerException("학점 계산 기간 확인 실패", e);
	    }
	}

	// 중간고사 기간 DTO
	public SchoolScheduleDTO getMidtermPeriod() {
	    try (Connection conn = DBConnection.getConnection()) {
	        return schoolScheduleDAO.findNearestSchedule(
	            conn,
	            ScheduleCode.MIDTERM_GRADE_APPEAL,
	            AppDateTime.today()
	        );
	    } catch (Exception e) {
	        throw new InternalServerException("중간고사 기간 조회 실패", e);
	    }
	}

	// 기말고사 기간 DTO
	public SchoolScheduleDTO getFinalPeriod() {
	    try (Connection conn = DBConnection.getConnection()) {
	    	return schoolScheduleDAO.findNearestSchedule(
	                conn,
	                ScheduleCode.GRADE_INPUT_INSTRUCTOR,
	                AppDateTime.today()
	            );
	    } catch (Exception e) {
	        throw new InternalServerException("기말고사 기간 조회 실패", e);
	    }
	}

	// 학점 계산 기간 DTO
	public SchoolScheduleDTO getGradeCalcPeriod() {
	    try (Connection conn = DBConnection.getConnection()) {
	        return schoolScheduleDAO.findNearestSchedule(
	            conn,
	            ScheduleCode.GRADE_INPUT_INSTRUCTOR,
	            AppDateTime.today()
	        );
	    } catch (Exception e) {
	        throw new InternalServerException("학점 계산 기간 조회 실패", e);
	    }
	}

}
