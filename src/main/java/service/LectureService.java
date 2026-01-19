package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dao.LectureScheduleDAO;
import model.dao.LectureSessionDAO;
import model.dto.LectureDTO;
import model.dto.LectureScheduleDTO;
import model.dto.LectureStudentDTO;

/**
 * 강의 도메인 전용 Service
 * - 강의 목록
 * - 강의 상세
 * - 강의 시간표
 * - 강의 수강생
 * - 강의 회차 생성
 */
public class LectureService {

    private static final LectureService instance = new LectureService();
    public static LectureService getInstance() {
        return instance;
    }

    private LectureDAO lectureDAO = LectureDAO.getInstance();
    private LectureScheduleDAO lectureScheduleDAO = LectureScheduleDAO.getInstance();
    private LectureSessionDAO lectureSessionDAO = LectureSessionDAO.getInstance();
    private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();

    private LectureService() {}

    /* ==========================
     * 강의 목록 (강사)
     * ========================== */
    public List<LectureDTO> getLecturesByInstructor(long instructorId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectLecturesByInstructor(conn, instructorId);
        } catch (Exception e) {
            throw new RuntimeException("교수 강의 목록 조회 실패", e);
        }
    }

    /* ==========================
     * 강의 상세
     * ========================== */
    public LectureDTO getLectureDetail(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectLectureById(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("강의 상세 조회 실패", e);
        }
    }

    /* ==========================
     * 강의 시간표
     * ========================== */
    public List<LectureScheduleDTO> getLectureSchedules(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureScheduleDAO.selectByLectureId(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("강의 시간표 조회 실패", e);
        }
    }

    /* ==========================
     * 강의 수강생 조회
     * ========================== */
    public List<LectureStudentDTO> getLectureStudents(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return enrollmentDAO.selectStudentsByLectureId(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("강의 수강생 조회 실패", e);
        }
    }

    /* ==========================
     * 강의 회차 생성
     * - 강의 승인 시 1회 호출
     * ========================== */
    public void generateLectureSessions(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {

            LectureDTO lecture =
                lectureDAO.selectLectureById(conn, lectureId);

            List<LectureScheduleDTO> schedules =
                lectureScheduleDAO.selectByLectureId(conn, lectureId);

            lectureSessionDAO.generateSessions(
                conn,
                lecture,
                schedules
            );

        } catch (Exception e) {
            throw new RuntimeException("강의 회차 생성 실패", e);
        }
    }
}