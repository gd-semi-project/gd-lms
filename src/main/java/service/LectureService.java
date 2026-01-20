package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.LectureDAO;
import model.dto.LectureDTO;
import model.dto.LectureStudentDTO;

public class LectureService {	// 이미 개설된 강의에 기준

	private static final LectureService instance = new LectureService();

	private LectureService() {
	}

	public static LectureService getInstance() {
		return instance;
	}

	private final LectureDAO lectureDAO = LectureDAO.getInstance();

	// 강의 목록
	public List<LectureDTO> getLecturesByInstructor(Long instructorId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectLecturesByInstructor(conn, instructorId);
        } catch (Exception e) {
            throw new RuntimeException("강의 목록 조회 실패", e);
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

}