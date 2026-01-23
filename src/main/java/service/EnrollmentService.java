package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dto.EnrollmentDTO;
import model.dto.LectureForEnrollDTO;

public class EnrollmentService {

	private LectureDAO lectureDAO = LectureDAO.getInstance();
	private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();

	// 수강신청 가능한 강의목록 조회
	public List<LectureForEnrollDTO> getAvailableLecturesForEnroll() {
		return lectureDAO.findAvailableLecturesForEnroll();
	}

	// 내가 신청한 수강신청 내역 조회
	public List<EnrollmentDTO> getMyEnrollments(long studentId) {
		return enrollmentDAO.findByStudentId(studentId);
	}
	
	// 숨김용
	public List<Long> getMyLectureId(long studentId) {
		return enrollmentDAO.findMyLectureId(studentId);
	}

	// 수강신청
	public void apply(long studentId, long lectureId) {
		Connection conn = null;

		// 트랜잭션 처리
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			// 중복 신청 체크(요일이 달라도 같은 수업이면 중복신청 불가)
			if (enrollmentDAO.dpCheck(conn, studentId, lectureId)) {
				throw new IllegalStateException("이미 신청한 강의입니다.");
			}
			// 정원 체크(정원이 모두 차면 신청불가)
			if (!lectureDAO.checkCapacity(conn, lectureId)) {
				throw new IllegalStateException("정원이 초과되었습니다.");
			}
			// 시간표 겹침 체크(같은 시간대에 시간표가 겹쳐서는 안됨)
			if (enrollmentDAO.checkSchedule(conn, studentId, lectureId)) {
				throw new IllegalStateException("강의 시간표가 겹칩니다.");
			}

			// 수강신청 등록
			enrollmentDAO.insertLecture(conn, studentId, lectureId);

			conn.commit();
		} catch (Exception e) {
			if (conn != null) {
				try {
					// 예외 발생 시 db의 모든 변경사항 롤백
					conn.rollback();
				} catch (Exception ex) {
				}
			}
			throw new RuntimeException(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// 수강취소
	public void cancel(long studentId, long lectureId) {
		Connection conn = null;

		// 트랜잭션 처리
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			// 신청 상태 확인
			boolean enrolled = enrollmentDAO.isEnrolled(conn, studentId, lectureId);
			if(!enrolled) {
				throw new IllegalStateException("이미 취소되었거나 신청하지 않은 강의입니다.");
			}
			
			// 수강 취소(DROP처리)
			enrollmentDAO.dropLecture(conn, studentId, lectureId);
			
			conn.commit();
		} catch (Exception e) {
			if (conn != null) {
				try {
					// 예외 발생 시 db의 모든 변경사항 롤백
					conn.rollback();
				} catch (Exception ex) {
				}
			}
			throw new RuntimeException(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
