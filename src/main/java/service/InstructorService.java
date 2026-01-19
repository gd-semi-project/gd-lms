package service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DBConnection;
import model.dao.InstructorDAO;
import model.dao.LectureDAO;
import model.dao.UserDAO;
import model.dto.InstructorDTO;
import model.dto.LectureDTO;
import model.dto.UserDTO;

public class InstructorService {

	private static final InstructorService instance = new InstructorService();

	private InstructorDAO instructorDAO = InstructorDAO.getInstance();
	private LectureDAO lectureDAO = LectureDAO.getInstance();
	private UserDAO userDAO = UserDAO.getInstance();

	private InstructorService() {
	}

	public static InstructorService getInstance() {
		return instance;
	}

	// 강사 본인 정보 조회
	public InstructorDTO getInstructorInfo(long userId) {
		try (Connection conn = DBConnection.getConnection()) {
			return instructorDAO.selectInstructorInfo(conn, userId);
		} catch (Exception e) {
			throw new RuntimeException("강사 정보 조회 실패", e);
		}
	}

	public List<LectureDTO> getMyLectures(long instructorId) {
		try (Connection conn = DBConnection.getConnection()) {
			return lectureDAO.selectLecturesByInstructor(conn, instructorId);
		} catch (Exception e) {
			throw new RuntimeException("강사 강의 목록 조회 실패", e);
		}
	}

	public Map<String, Object> getInstructorProfile(long userId, String loginId) {

		try (Connection conn = DBConnection.getConnection()) {

			InstructorDTO instructor = instructorDAO.selectInstructorInfo(conn, userId);

			UserDTO user = userDAO.SelectUsersById(loginId);

			Map<String, Object> result = new HashMap<>();
			result.put("instructor", instructor);
			result.put("user", user);

			return result;

		} catch (Exception e) {
			throw new RuntimeException("강사 프로필 조회 실패", e);
		}
	}

}