package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import database.DBConnection;
import model.dao.LectureDAO;
import model.dao.LectureScheduleDAO;
import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureScheduleDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.LectureStatus;
import model.enumtype.Role;

public class LectureService {	// 이미 개설된 강의에 기준

	private static final LectureService instance = new LectureService();

	private LectureService() {
	}

	public static LectureService getInstance() {
		return instance;
	}

	private final LectureDAO lectureDAO = LectureDAO.getInstance();
	private final LectureScheduleDAO lectureScheduleDAO = LectureScheduleDAO.getInstance();
	
	// 학생/교수 강의 리스트 
	public List<LectureDTO> getMyLectures(AccessDTO access, String status) {	
	    if (access == null) {
	        throw new IllegalArgumentException("AccessInfo is null");
	    }
	    

	    try (Connection conn = DBConnection.getConnection()) {

	        if (access.getRole() == Role.INSTRUCTOR) {
	            return lectureDAO.selectLecturesByInstructor(
	                conn,
	                access.getUserId(),
	                status
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
		return lectureDAO.cancelExpiredLectureRequest();
	}

	
	public int[] syncLectureStatusByDate(LocalDate today) {
		int ongoingCount = lectureDAO.markOnGoing(today);
		int endedCount = lectureDAO.markEnded(today);
		return new int[] {ongoingCount, endedCount};
	}

	public List<LectureDTO> getAllLectureByDepartment(long departmentId, String lectureStatus) {
	  	if (lectureStatus == null || lectureStatus.isBlank()) lectureStatus = "ALL";
	  	
	  	lectureStatus = lectureStatus.trim().toUpperCase();
		
        if (!("ALL".equals(lectureStatus)
                || "ONGOING".equals(lectureStatus)
                || "PLANNED".equals(lectureStatus)
                || "ENDED".equals(lectureStatus))) {
                 lectureStatus = "ALL";
             }
        
        List<LectureDTO> lectureList;
		
        if ("ALL".equals(lectureStatus)) {
        	lectureList = lectureDAO.findLectureByDepartment(departmentId);
        } else {
        	lectureList = lectureDAO.findByDepartmentAndStatus(departmentId, lectureStatus);
        }
        
        if(lectureList == null || lectureList.isEmpty()) {
        	return lectureList;
        }
        
        List<Long> lectureIds = lectureList.stream()
        					.map(LectureDTO::getLectureId)
        					.filter(Objects::nonNull)
        					.toList();
        
        List<LectureScheduleDTO> schedules = lectureScheduleDAO.findByLectureIds(lectureIds);
        
        Map<Long, List<LectureScheduleDTO>> map = new HashMap<Long, List<LectureScheduleDTO>>();
        
        for (LectureScheduleDTO s : schedules) {
        	map.computeIfAbsent(s.getLectureId(), k-> new ArrayList<>()).add(s);
        }
        
        for (LectureDTO l : lectureList) {
        	l.setSchedules(map.getOrDefault(l.getLectureId(), Collections.emptyList()));
        }
		
        return lectureList;
	}

	public Map<Long, Integer> getEnrollCountByLectureId(List<LectureDTO> lectureList) {
	    if (lectureList == null || lectureList.isEmpty()) {
	        return Collections.emptyMap();
	    }
	    

	    List<Long> lectureIds = lectureList.stream()
	            .map(LectureDTO::getLectureId)
	            .filter(Objects::nonNull)
	            .toList();

	    if (lectureIds.isEmpty()) {
	        return Collections.emptyMap();
	    }

	    return lectureDAO.selectEnrollCountMapByLectureIds(lectureIds);
	    
	    
	}

	public List<LectureDTO> getAllLecture() {
		return lectureDAO.getAllLecture();
	}
	
	
	
	
	
	

}