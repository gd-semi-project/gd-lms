package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import database.DBConnection;

import model.dao.LectureSessionDAO;
import model.dto.LectureSessionDTO;

public class LectureSessionService {

    private static final LectureSessionService instance =
            new LectureSessionService();

    public static LectureSessionService getInstance() {
        return instance;
    }

    private LectureSessionDAO lectureSessionDAO =
            LectureSessionDAO.getInstance();

    private LectureSessionService() {}

    // 교수용 전체 회차
    public List<LectureSessionDTO> getSessionsByLecture(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureSessionDAO.selectByLectureId(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("강의 회차 조회 실패", e);
        }
    }

    // 학생용 오늘 수업
    public LectureSessionDTO getTodaySession(long lectureId, LocalDate today) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureSessionDAO.selectByDate(conn, lectureId, today);
        } catch (Exception e) {
            throw new RuntimeException("오늘 수업 조회 실패", e);
        }
    }
}