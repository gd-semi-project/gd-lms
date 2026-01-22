package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
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

    /* =================================================
     * 교수: 오늘 회차 생성 (출석 시작 버튼)
     * ================================================= */
    public long createTodaySession(long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            LocalDate today = LocalDate.now();

            // 1️⃣ 이미 오늘 회차가 있으면 재사용
            if (lectureSessionDAO.existsTodaySession(conn, lectureId, today)) {

                LectureSessionDTO todaySession =
                        lectureSessionDAO.findToday(conn, lectureId, today);

                return todaySession.getSessionId();
            }

            // 2️⃣ 새 회차 생성
            // 👉 시간은 "현재 ~ +2시간" (나중에 강의 시간표랑 연동 가능)
            LocalTime startTime = LocalTime.now();
            LocalTime endTime = startTime.plusHours(2);

            return lectureSessionDAO.insertSession(
                    conn,
                    lectureId,
                    today,
                    startTime,
                    endTime
            );

        } catch (Exception e) {
            throw new RuntimeException("회차 생성 실패", e);
        }
    }

    /* =================================================
     * 교수: 강의 전체 회차 조회
     * ================================================= */
    public List<LectureSessionDTO> getSessionsByLecture(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureSessionDAO.findByLecture(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("강의 회차 조회 실패", e);
        }
    }

    /* =================================================
     * 학생: 오늘 수업 조회
     * ================================================= */
    public LectureSessionDTO getTodaySession(
            long lectureId,
            LocalDate today
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureSessionDAO.findToday(conn, lectureId, today);
        } catch (Exception e) {
            throw new RuntimeException("오늘 수업 조회 실패", e);
        }
    }
}