package service;

import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import database.DBConnection;
import model.dao.AttendanceDAO;
import model.dao.LectureAttendanceStatusDAO;
import model.dao.LectureSessionDAO;
import model.dto.AttendanceDTO;
import model.dto.LectureSessionDTO;
import model.dto.SessionAttendanceDTO;
import model.enumtype.AttendanceStatus;
import utils.AppTime;

public class AttendanceService {

	private static final AttendanceService instance = new AttendanceService();

	public static AttendanceService getInstance() {
		return instance;
	}

	private final AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
	private final LectureSessionDAO lectureSessionDAO = LectureSessionDAO.getInstance();
	private final LectureAttendanceStatusDAO lectureAttendanceStatusDAO = LectureAttendanceStatusDAO.getInstance();

	private AttendanceService() {
	}

	// 출석 시작
	public long openAttendance(long lectureId) {

		try (Connection conn = DBConnection.getConnection()) {

			LocalDate today = AppTime.now().toLocalDate();
			LocalTime now = AppTime.now().toLocalTime();

			if (lectureSessionDAO.existsTodaySession(conn, lectureId, today)) {
				throw new IllegalStateException("이미 오늘 출석이 시작되었습니다.");
			}

			LocalTime end = now.plusMinutes(10);

			long sessionId = lectureSessionDAO.insertSession(conn, lectureId, today, now, end);

			lectureAttendanceStatusDAO.ensureRow(conn, sessionId);
			lectureAttendanceStatusDAO.openAttendance(conn, sessionId);

			attendanceDAO.insertAbsentForLecture(conn, sessionId, lectureId);

			return sessionId;

		} catch (IllegalArgumentException | IllegalStateException e) {
			// TODO : 409 Conflict (출석 시작 불가 상태)
		    throw e;
		} catch (Exception e) {
		    // TODO: 500 출석 시작 중 서버 오류
		    throw new RuntimeException("출석 시작 실패", e);
		}
	}

	// 출석 준비 (수강생 전원 기본 결석 생성)
	public void prepareAttendance(long sessionId, long lectureId) {
		try (Connection conn = DBConnection.getConnection()) {
			attendanceDAO.insertAbsentForLecture(conn, sessionId, lectureId);
		} catch (Exception e) {
			// TODO : 500 Internal Server Error
			throw new RuntimeException("출석 준비 실패", e);
		}
	}

	// 출석 종료
	public void closeAttendance(long sessionId) {
		try (Connection conn = DBConnection.getConnection()) {
			lectureAttendanceStatusDAO.ensureRow(conn, sessionId);
			lectureAttendanceStatusDAO.closeAttendance(conn, sessionId);
		} catch (Exception e) {
			// TODO : 500 Internal Server Error
			throw new RuntimeException("출석 종료 실패", e);
		}
	}

	// 오늘 회차 조회
	public LectureSessionDTO getTodaySession(long lectureId, LocalDate date) {
		try (Connection conn = DBConnection.getConnection()) {
			return lectureSessionDAO.findToday(conn, lectureId, date);
		} catch (Exception e) {
			// TODO : 500 Internal Server Error
			throw new RuntimeException("오늘 회차 조회 실패", e);
		}
	}

	// 강의 전체 회차 조회
	public List<LectureSessionDTO> getSessionsByLecture(long lectureId) {
		try (Connection conn = DBConnection.getConnection()) {
			return lectureSessionDAO.findByLecture(conn, lectureId);
		} catch (Exception e) {
			// TODO : 500 Internal Server Error
			throw new RuntimeException("회차 목록 조회 실패", e);
		}
	}

	// 풀석 체크
	public void checkAttendance(long sessionId, long studentId) {

		try (Connection conn = DBConnection.getConnection()) {

			LectureSessionDTO session = lectureSessionDAO.findById(conn, sessionId);

			if (session == null) {
				// TODO : 404 Not Found
				throw new IllegalArgumentException("회차가 존재하지 않습니다.");
			}

			if (!lectureAttendanceStatusDAO.isOpen(conn, sessionId)) {
				throw new IllegalStateException("출석 시간이 종료되었습니다.");
			}

			LocalTime now = AppTime.now().toLocalTime();
			LocalTime start = session.getStartTime();

			if (now.isAfter(start.plusMinutes(10))) {
				lectureAttendanceStatusDAO.closeAttendance(conn, sessionId);
				throw new IllegalStateException("출석 시간이 종료되었습니다.");
			}

			if (attendanceDAO.isAlreadyChecked(conn, sessionId, studentId)) {
				throw new IllegalStateException("이미 출석 처리되었습니다.");
			}

			AttendanceStatus status;

			if (now.isBefore(start.plusMinutes(10))) {
				status = AttendanceStatus.PRESENT;
			} else {
				status = AttendanceStatus.LATE;
			}

			attendanceDAO.markAttendance(conn, sessionId, studentId, status);

		} catch (IllegalArgumentException e) {
		    // TODO : 400 Bad Request (잘못된 요청 값)
		    throw e;
		} catch (IllegalStateException e) {
		    // TODO : 409 Conflict (상태 충돌)
		    throw e;
		} catch (Exception e) {
		    // TODO: 500 출석 체크 중 서버 오류
		    throw new RuntimeException("출석 처리 실패", e);
		}
	}

	// 이미 출석했는지?
	public boolean isAlreadyChecked(long sessionId, long studentId) {
		try (Connection conn = DBConnection.getConnection()) {
			return attendanceDAO.isAlreadyChecked(conn, sessionId, studentId);
		} catch (Exception e) {
			return false;
		}
	}

	// 출석부
	public List<SessionAttendanceDTO> getSessionAttendance(long sessionId) {
		try (Connection conn = DBConnection.getConnection()) {
			return attendanceDAO.findBySession(conn, sessionId);
		} catch (Exception e) {
			throw new RuntimeException("출석부 조회 실패", e);
		}
	}

	// 출결 수정
	public void updateAttendance(long attendanceId, AttendanceStatus status) {
		try (Connection conn = DBConnection.getConnection()) {
			attendanceDAO.updateStatusById(conn, attendanceId, status);
		} catch (Exception e) {
			throw new RuntimeException("출결 수정 실패", e);
		}
	}

	// 학생 출석 이력
	public List<AttendanceDTO> getStudentAttendance(long lectureId, long studentId) {
		try (Connection conn = DBConnection.getConnection()) {
			return attendanceDAO.findByStudent(conn, lectureId, studentId);
		} catch (Exception e) {
			throw new RuntimeException("출석 이력 조회 실패", e);
		}
	}

	// 출석 열람 여부
	public boolean isAttendanceOpen(long sessionId) {
		try (Connection conn = DBConnection.getConnection()) {

			LectureSessionDTO session = lectureSessionDAO.findById(conn, sessionId);

			if (session == null)
				// TODO : 404 Not Found
				return false;

			LocalTime now = AppTime.now().toLocalTime();
			LocalTime start = session.getStartTime();

			// ⏱ 10분 초과 → 자동 종료
			if (now.isAfter(start.plusMinutes(10))) {
				lectureAttendanceStatusDAO.closeAttendance(conn, sessionId);
				return false;
			}

			return lectureAttendanceStatusDAO.isOpen(conn, sessionId);

		} catch (Exception e) {
			// TODO : 500 Internal Server Error
			return false;
		}
	}

	// 이미 출석 했는지(교수
	public boolean hasTodaySession(long lectureId) {
		try (Connection conn = DBConnection.getConnection()) {
			LocalDate today = AppTime.now().toLocalDate();
			return lectureSessionDAO.existsTodaySession(conn, lectureId, today);
		} catch (Exception e) {
			// TODO : 500 Internal Server Error
			return false;
		}
	}
}