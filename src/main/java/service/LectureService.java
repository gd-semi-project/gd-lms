package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import database.DBConnection;
import exception.BadRequestException;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import model.dao.LectureDAO;
import model.dao.LectureScheduleDAO;
import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureScheduleDTO;
import model.dto.LectureStudentDTO;
import model.dto.MyLectureDTO;
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
	
	public List<LectureDTO> getMyLectures(AccessDTO access, String status) {

        if (access == null) {
            throw new BadRequestException("로그인 정보가 없습니다.");
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (access.getRole() == Role.INSTRUCTOR) {
                return lectureDAO.selectLecturesByInstructor(
                        conn, access.getUserId(), status);
            }

            if (access.getRole() == Role.STUDENT) {
                return lectureDAO.selectLecturesByStudent(
                        conn, access.getUserId());
            }

            return List.of();

        } catch (Exception e) {
            throw new InternalServerException("내 강의 목록 조회 실패", e);
        }
    }

    // 강의 상세
    public LectureDTO getLectureDetail(Long lectureId) {

        if (lectureId == null || lectureId <= 0) {
            throw new BadRequestException("강의 ID가 올바르지 않습니다.");
        }

        try (Connection conn = DBConnection.getConnection()) {

            LectureDTO lecture = lectureDAO.selectLectureById(conn, lectureId);

            if (lecture == null) {
                throw new ResourceNotFoundException("존재하지 않는 강의입니다.");
            }

            return lecture;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("강의 상세 조회 실패", e);
        }
    }

    // 수강생 목록
    public List<LectureStudentDTO> getLectureStudents(Long lectureId) {

        if (lectureId == null || lectureId <= 0) {
            throw new BadRequestException("강의 ID가 올바르지 않습니다.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectLectureStudents(conn, lectureId);
        } catch (Exception e) {
            throw new InternalServerException("강의 수강생 조회 실패", e);
        }
    }

	
	// 강의 개설 요청 기간 종료 후 PENDING → CANCELED
	public int cancelExpiredLectureRequest() {
		return lectureDAO.cancelExpiredLectureRequest();
	}
	
	// 강의 상태 동기화 (PLANNED → ONGOING → ENDED)
	public int[] syncLectureStatusByDate(LocalDate today) {
		int ongoingCount = lectureDAO.markOnGoing(today);
		int endedCount = lectureDAO.markEnded(today);
		return new int[] {ongoingCount, endedCount};
	}

	// 학과별 강의 조회
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

	// 강의별 수강 인원 수
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

	// 전체 강의 조회 (관리자용)
	public List<LectureDTO> getAllLecture() {
		return lectureDAO.getAllLecture();
	}
	
	
	// 과거 수강한 과목, 현재수강한 과목 버튼으로 볼수있게
	public List<MyLectureDTO> getMyOngoingLectures(Long userId) {
	    return lectureDAO.selectMyEnrollmentedLecture(userId);
	}

	public List<MyLectureDTO> getMyEndedLectures(Long userId) {
	    return lectureDAO.selectMyEndedLecture(userId);
	}

	

}