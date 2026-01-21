package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import jakarta.servlet.http.HttpServletRequest;
import model.dao.LectureRequestDAO;
import model.dto.LectureRequestDTO;
import model.enumtype.LectureValidation;

public class LectureRequestService {	// 강의 신청/반려/승인 기준

    private static final LectureRequestService instance = new LectureRequestService();
    private LectureRequestService() {}

    public static LectureRequestService getInstance() {
        return instance;
    }

    private final LectureRequestDAO dao = LectureRequestDAO.getInstance();

    // 목록 조회
    public List<LectureRequestDTO> getMyLectureRequests(Long instructorId) {
        try (Connection conn = DBConnection.getConnection()) {
            return dao.selectByInstructor(conn, instructorId);
        } catch (Exception e) {
            throw new RuntimeException("강의 개설 신청 목록 조회 실패", e);
        }
    }

    // 신청
    public void createLectureRequest(Long instructorId, HttpServletRequest request) {
        try (Connection conn = DBConnection.getConnection()) {
            Long lectureId = dao.insertLecture(conn, instructorId, request);
            dao.insertSchedule(conn, lectureId, request);
        } catch (Exception e) {
            throw new RuntimeException("강의 개설 신청 실패", e);
        }
    }
    
    // 수정 폼 기준
    public LectureRequestDTO getLectureRequestDetail(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return dao.selectByLectureId(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("강의 개설 신청 상세 조회 실패", e);
        }
    }

    // 수정
    public void updateLectureRequest(Long lectureId, HttpServletRequest request) {
        try (Connection conn = DBConnection.getConnection()) {

            LectureValidation validation = dao.getValidation(conn, lectureId);

            if (validation == LectureValidation.CANCELED) {
                throw new IllegalStateException("반려된 강의는 수정할 수 없습니다.");
            }

            dao.updateLecture(conn, lectureId, request);

        } catch (Exception e) {
            throw new RuntimeException("강의 개설 수정 실패", e);
        }
    }

    // 삭제
    public void deleteLectureRequest(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {

            LectureValidation validation = dao.getValidation(conn, lectureId);

            if (validation != LectureValidation.PENDING) {
                throw new IllegalStateException("승인된 강의는 삭제할 수 없습니다.");
            }

            dao.deleteLecture(conn, lectureId);

        } catch (Exception e) {
            throw new RuntimeException("강의 개설 삭제 실패", e);
        }
    }
}