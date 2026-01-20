package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.dao.DepartmentDAO;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dto.DepartmentDTO;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.LectureScheduleDTO;

public class AdminService {
	private LectureDAO lectureDAO= LectureDAO.getInstance();
	private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
	private DepartmentDAO departmentDAO = DepartmentDAO.getInstance();
	
	private static final AdminService instance = new AdminService();
	private AdminService() {}
	
	
	public static AdminService getInstance() {
		return instance;
	}

	public int getLectureCount() {
		return lectureDAO.getLectureCount();
	}
	
	public int getTotalLectureCount() {
		return lectureDAO.getTotalLectureCount();
	}
	
	public int getLectureFillRate() {
		return lectureDAO.getLectureFillRate();
	}
	
	public int getLowFillRateLecture() {
		return lectureDAO.getLowFillRateLecture();
	}
	
	public int getTotalLectureCapacity() {
		return lectureDAO.getTotalLectureCapacity();
	}
	
	public int getTotalEnrollment() {
		return lectureDAO.getTotalEnrollment();
	}
	
	public int getLectureRequestCount() {
		return lectureDAO.getLectureRequestCount();
	}
	
	
	public ArrayList<LectureRequestDTO> getPendingLectureList(Long departmentId){
		
		String validation = "PENDING";
		
		ArrayList<LectureRequestDTO> list = enrollmentDAO.getLectureList(validation, departmentId);
		
		return list;
	}
	
	public ArrayList<LectureRequestDTO> getCanceledLectureList(Long departmentId){
		
		String validation = "CANCELED";
		
		ArrayList<LectureRequestDTO> list = enrollmentDAO.getLectureList(validation, departmentId);
		
		return list;
	}
	
	public ArrayList<LectureRequestDTO> getConfirmedLectureList(Long departmentId){
		
		String validation = "CONFIRMED";
		
		ArrayList<LectureRequestDTO> list = enrollmentDAO.getLectureList(validation, departmentId);
		
		return list;
	}
	
	
	public void LectureValidate(Long lectureId, String validation) {
		
		lectureDAO.setLectureValidation(validation, lectureId);
	}


	public ArrayList<DepartmentDTO> getDepartmentList() {
		return departmentDAO.getDepartmentList();
	}
}
