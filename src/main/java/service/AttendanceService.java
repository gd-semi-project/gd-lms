package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import database.DBConnection;
import model.dao.AttendanceDAO;
import model.dao.LectureSessionDAO;
import model.dto.AttendanceDTO;
import model.dto.LectureSessionDTO;
import model.dto.SessionAttendanceDTO;
import model.enumtype.AttendanceStatus;

public class AttendanceService {

    private static final AttendanceService instance = new AttendanceService();
    public static AttendanceService getInstance() {
        return instance;
    }

    private AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
    private LectureSessionDAO lectureSessionDAO = LectureSessionDAO.getInstance();

    private AttendanceService() {}

    /* =================================================
     * 교수: 출석 시작
     * (회차 생성 + 기본 결석 생성)
     * ================================================= */
    public long openAttendance(long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            if (lectureSessionDAO.existsTodaySession(
                    conn, lectureId, LocalDate.now())) {
                throw new IllegalStateException("이미 오늘 출석이 시작되었습니다.");
            }

            long sessionId = lectureSessionDAO.insertSession(
                    conn,
                    lectureId,
                    LocalDate.now(),
                    LocalTime.now(),
                    LocalTime.now().plusHours(1)
            );

            attendanceDAO.insertAbsentForLecture(
                    conn, sessionId, lectureId
            );

            return sessionId;
        }
        catch (Exception e) {
            throw new RuntimeException("출석 시작 실패", e);
        }
    }

    /* =================================================
     * ✅ 추가 1: 출석 준비 (컨트롤러용 분리 메서드)
     * ================================================= */
    public void prepareAttendance(long sessionId, long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            attendanceDAO.insertAbsentForLecture(
                    conn, sessionId, lectureId
            );
        } catch (Exception e) {
            throw new RuntimeException("출석 준비 실패", e);
        }
    }

    /* =================================================
     * 학생: 출석 체크
     * ================================================= */
    public void checkAttendance(long sessionId, long studentId) {

        try (Connection conn = DBConnection.getConnection()) {

            LectureSessionDTO session =
                    lectureSessionDAO.findById(conn, sessionId);

            if (session == null) {
                throw new IllegalArgumentException("회차가 존재하지 않습니다.");
            }

            if (attendanceDAO.isAlreadyChecked(conn, sessionId, studentId)) {
                throw new IllegalStateException("이미 출석 처리되었습니다.");
            }

            LocalTime now = LocalTime.now();
            LocalTime start = session.getStartTime();

            AttendanceStatus status;
            if (now.isBefore(start.plusMinutes(10))) {
                status = AttendanceStatus.PRESENT;
            } else if (now.isBefore(start.plusMinutes(30))) {
                status = AttendanceStatus.LATE;
            } else {
                throw new IllegalStateException("출석 가능 시간이 지났습니다.");
            }

            attendanceDAO.markAttendance(
                    conn, sessionId, studentId, status
            );
        }
        catch (Exception e) {
            throw new RuntimeException("출석 처리 실패", e);
        }
    }

    /* =================================================
     * ✅ 추가 2: 이미 출석했는지 여부
     * ================================================= */
    public boolean isAlreadyChecked(long sessionId, long studentId) {
        try (Connection conn = DBConnection.getConnection()) {
            return attendanceDAO.isAlreadyChecked(
                    conn, sessionId, studentId
            );
        } catch (Exception e) {
            return false;
        }
    }

    /* =================================================
     * 교수: 회차별 출석부
     * ================================================= */
    public List<SessionAttendanceDTO> getSessionAttendance(long sessionId) {
        try (Connection conn = DBConnection.getConnection()) {
            return attendanceDAO.findBySession(conn, sessionId);
        }
        catch (Exception e) {
            throw new RuntimeException("출석부 조회 실패", e);
        }
    }

    /* =================================================
     * 교수: 출결 수정
     * ================================================= */
    public void updateAttendance(
            long attendanceId,
            AttendanceStatus status
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            attendanceDAO.updateStatusById(conn, attendanceId, status);
        }
        catch (Exception e) {
            throw new RuntimeException("출결 수정 실패", e);
        }
    }

    /* =================================================
     * 학생: 출석 이력
     * ================================================= */
    public List<AttendanceDTO> getStudentAttendance(
            long lectureId,
            long studentId
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            return attendanceDAO.findByStudent(conn, lectureId, studentId);
        }
        catch (Exception e) {
            throw new RuntimeException("출석 이력 조회 실패", e);
        }
    }
}