package service;

import java.util.List;

import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dto.EnrollmentDTO;
import model.dto.LectureDTO;

public class EnrollmentService {

	private LectureDAO lectureDAO = LectureDAO.getInstance();
	private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
	
	// 수강신청 가능한 강의목록 조회
	 public List<LectureDTO> getAvailableLecturesForEnroll() {
	        return lectureDAO.findAvailableLecturesForEnroll();
	    }
	
	// 내가 신청한 수강신청 내역 조회
	 public List<EnrollmentDTO> getMyEnrollments(long studentId) {
	        return enrollmentDAO.findByStudentId(studentId);
	    }
	 // 수강신청
	 public void apply(long studentId, long lectureId) {
		 // 중복 신청 체크(요일이 달라도 같은 수업이면 중복신청 불가)
		 
		 // 정원 체크(정원이 모두 차면 신청불가)
		 
		 // 시간표 겹침 체크(같은 시간대에 시간표가 겹쳐서는 안됨)
		 
		 // 트랜잭션 처리
	 }
	 
	 // 수강취소
	 public void cancel(long studentId, long lectureId ) {
		 // 신청 상태 확인
		 
		 // 취소 처리
		 
		 
	 }
	 
	 
}
