package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import database.DBConnection;
import model.dao.LectureDAO;
import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.Role;

public class LectureService {	// 이미 개설된 강의에 기준

	private static final LectureService instance = new LectureService();

	private LectureService() {
	}

	public static LectureService getInstance() {
		return instance;
	}

	private final LectureDAO lectureDAO = LectureDAO.getInstance();

	public List<LectureDTO> getMyLectures(AccessDTO access) {	// 학생/교수 강의 리스트 
	    if (access == null) {
	        throw new IllegalArgumentException("AccessInfo is null");
	    }

	    try (Connection conn = DBConnection.getConnection()) {

	        if (access.getRole() == Role.INSTRUCTOR) {
	            return lectureDAO.selectLecturesByInstructor(
	                conn,
	                access.getUserId()
	            );
	        }

	        if (access.getRole() == Role.STUDENT) {
	            return lectureDAO.selectLecturesByStudent(
	                conn,
	                access.getUserId()
	            );
	        }

	        return List.of(); // ADMIN 등

	    } catch (Exception e) {
	        throw new RuntimeException("내 강의 목록 조회 실패", e);
	    }
	}

	// 강의 상세
	public LectureDTO getLectureDetail(Long lectureId) {
		if (lectureId == null || lectureId <= 0) {
			throw new IllegalArgumentException("lectureId is required.");
		}

		try (Connection conn = DBConnection.getConnection()) {
			return lectureDAO.selectLectureById(conn, lectureId);
		} catch (Exception e) {
			throw new RuntimeException("강의 상세 조회 실패", e);
		}
	}

	// 수강생 조회
	public List<LectureStudentDTO> getLectureStudents(Long lectureId) {
		try (Connection conn = DBConnection.getConnection()) {
			return lectureDAO.selectLectureStudents(conn, lectureId);
		} catch (Exception e) {
			throw new RuntimeException("강의 수강생 조회 실패", e);
		}
	}
	
	// 강의 개설 요청 종료 후 PENDING 상태인 요청 일괄 CANCELD 처리
	
	public int cancelExpiredLectureRequest() {
		final String sql = """
				UPDATE lecture
				SET validation = 'CANCELED'
				WHERE validation = 'PENDING'
				""";
		
		try (	Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)
				){
			
			return pstmt.executeUpdate(); // 취소된 건수 반환
					
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		
	}
	
	
	
	
	
	
	

}