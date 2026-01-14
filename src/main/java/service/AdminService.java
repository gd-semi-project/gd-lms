package service;

import model.dao.LectureDAO;

public class AdminService {
	private LectureDAO lectureDAO= LectureDAO.getInstance();
	
	
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
	
	
	
	
	
	
	
	
	
	
}
