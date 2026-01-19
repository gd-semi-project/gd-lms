package service;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import database.DBConnection;

import model.dao.AttendanceDAO;
import model.dao.LectureSessionDAO;

import model.dto.AttendanceDTO;
import model.dto.LectureSessionDTO;
import model.dto.SessionAttendanceDTO;
import model.dto.StudentAttendanceDTO;

import model.enumtype.AttendanceStatus;

public class AttendanceService {

    private static final AttendanceService instance = new AttendanceService();

    public static AttendanceService getInstance() {
        return instance;
    }

    private AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
    private LectureSessionDAO lectureSessionDAO = LectureSessionDAO.getInstance();

    private AttendanceService() {}

    /* ================= 학생 출석 ================= */
    public void checkAttendance(long sessionId, long studentId) {

        try (Connection conn = DBConnection.getConnection()) {

            LectureSessionDTO session =
                    lectureSessionDAO.selectById(conn, sessionId);

            LocalTime startTime = session.getStartTime();
            LocalTime now = LocalTime.now();

            AttendanceStatus status;

            if (now.isBefore(startTime.plusMinutes(10))) {
                status = AttendanceStatus.PRESENT;
            } else if (now.isBefore(startTime.plusMinutes(30))) {
                status = AttendanceStatus.LATE;
            } else {
                throw new IllegalStateException("출석 시간이 지났습니다.");
            }

            AttendanceDTO dto = new AttendanceDTO();
            dto.setSessionId(sessionId);
            dto.setStudentId(studentId);
            dto.setStatus(status);
            dto.setCheckedAt(LocalDateTime.now());

            attendanceDAO.insertAttendance(conn, dto);
        }
        catch (Exception e) {
            throw new RuntimeException("출석 처리 실패", e);
        }
    }

    /* ================= 자동 결석 ================= */
    public void autoMarkAbsent(long sessionId) {
        try (Connection conn = DBConnection.getConnection()) {
            attendanceDAO.insertAbsentIfNotExists(conn, sessionId);
        } catch (Exception e) {
            throw new RuntimeException("자동 결석 처리 실패", e);
        }
    }

    /* ================= 교수 출석부 ================= */
    public List<SessionAttendanceDTO> getSessionAttendance(long sessionId) {
        try (Connection conn = DBConnection.getConnection()) {
            return attendanceDAO.selectBySession(conn, sessionId);
        } catch (Exception e) {
            throw new RuntimeException("출석부 조회 실패", e);
        }
    }

    /* ================= 교수 수정 ================= */
    public void updateAttendanceStatus(
            long sessionId,
            long studentId,
            AttendanceStatus status
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            attendanceDAO.updateStatus(conn, sessionId, studentId, status);
        } catch (Exception e) {
            throw new RuntimeException("출결 수정 실패", e);
        }
    }

    /* ================= 학생 이력 ================= */
    public List<StudentAttendanceDTO> getStudentAttendance(
            long studentId,
            long lectureId
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            return attendanceDAO.selectByStudent(conn, studentId, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("학생 출석 조회 실패", e);
        }
    }
}