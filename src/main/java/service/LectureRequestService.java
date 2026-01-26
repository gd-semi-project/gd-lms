package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import database.DBConnection;
import jakarta.servlet.http.HttpServletRequest;
import model.dao.LectureRequestDAO;
import model.dao.LectureScheduleDAO;
import model.dao.SchoolScheduleDAO;
import model.dao.ScorePolicyDAO;
import model.dto.LectureRequestDTO;
import model.dto.LectureScheduleDTO;
import model.dto.RoomDTO;
import model.dto.SchoolScheduleDTO;
import model.dto.ScorePolicyDTO;
import model.enumtype.LectureValidation;
import model.enumtype.ScheduleCode;
import utils.AppTime;

public class LectureRequestService {

    private static final LectureRequestService instance =
        new LectureRequestService();

    private LectureRequestService() {}

    public static LectureRequestService getInstance() {
        return instance;
    }

    private final LectureRequestDAO lectureDAO =
        LectureRequestDAO.getInstance();

    private final SchoolScheduleDAO scheduleDAO =
        SchoolScheduleDAO.getInstance();

    private final ScorePolicyDAO scorePolicyDAO =
        ScorePolicyDAO.getInstance();

    private final LectureScheduleDAO lectureScheduleDAO =
        LectureScheduleDAO.getInstance();

    // 강의 개설 신청 기간 여부
    public boolean isLectureRequestPeriod() {

        try (Connection conn = DBConnection.getConnection()) {

            LocalDate today = AppTime.now().toLocalDate();

            SchoolScheduleDTO schedule =
                scheduleDAO.findNearestSchedule(
                    conn,
                    ScheduleCode.LECTURE_OPEN_REQUEST,
                    today
                );

            if (schedule == null) return false;

            return !today.isBefore(schedule.getStartDate())
                && !today.isAfter(schedule.getEndDate());

        } catch (Exception e) {
        	 // TODO : 500 Internal Server Error (DB 조회 실패)
            throw new RuntimeException(
                "강의 개설 신청 기간 확인 실패", e
            );
        }
    }

    // 가장 가까운 강의 개설 신청 기간
    public SchoolScheduleDTO getNearestLectureRequestPeriod() {

        try (Connection conn = DBConnection.getConnection()) {
            return scheduleDAO.findNearestSchedule(
                conn,
                ScheduleCode.LECTURE_OPEN_REQUEST,
                AppTime.now().toLocalDate()
            );
        } catch (Exception e) {
        	// TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 신청 기간 조회 실패", e
            );
        }
    }
    // 내 강의 개설 신청 목록
    public List<LectureRequestDTO> getMyLectureRequests(Long instructorId) {

        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectByInstructor(conn, instructorId);
        } catch (Exception e) {
        	// TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 개설 신청 목록 조회 실패", e
            );
        }
    }

    // 강의 개설 신청 상세
    public LectureRequestDTO getLectureRequestDetail(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectByLectureId(conn, lectureId);
        } catch (Exception e) {
        	// TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 개설 신청 상세 조회 실패", e
            );
        }
    }

    // 성적 배점 조회
    public ScorePolicyDTO getScorePolicy(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {
            return scorePolicyDAO.findByLectureId(conn, lectureId);
        } catch (Exception e) {
        	// TODO : 500 Internal Server Error
            throw new RuntimeException(
                "성적 배점 조회 실패", e
            );
        }
    }

    // 강의실 목록
    public List<RoomDTO> getAllRooms() {

        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectAllRooms(conn);
        } catch (Exception e) {
        	// TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의실 목록 조회 실패", e
            );
        }
    }

    // 신규 강의 개설 신청
    public void createLectureRequest(
            Long instructorId,
            HttpServletRequest request
    ) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            validateLecturePeriod(conn, request);

            Long lectureId =
                lectureDAO.insertLecture(conn, instructorId, request);

            lectureDAO.insertSchedule(conn, lectureId, request);

            ScorePolicyDTO policy =
                buildScorePolicy(lectureId, request);

            scorePolicyDAO.insert(conn, policy);

            conn.commit();

        } catch (IllegalArgumentException | IllegalStateException e) {
            // TODO : 400 Bad Request (비즈니스 규칙 위반)
            throw e;
        } catch (Exception e) {
            // TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 개설 신청 실패", e
            );
        }
    }

    // 강의 개설 신청 수정 
    public void updateLectureRequest(
            Long lectureId,
            HttpServletRequest request
    ) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            LectureValidation validation =
                lectureDAO.getValidation(conn, lectureId);

            if (validation == LectureValidation.CANCELED) {
                throw new IllegalStateException(
                    "반려된 강의는 수정할 수 없습니다."
                );
            }

            validateLecturePeriod(conn, request);
            lectureDAO.updateLecture(conn, lectureId, request);

            ScorePolicyDTO policy =
                buildScorePolicy(lectureId, request);

            if (scorePolicyDAO.existsByLectureId(conn, lectureId)) {
                scorePolicyDAO.update(conn, policy);
            } else {
                scorePolicyDAO.insert(conn, policy);
            }

            conn.commit();

        } catch (IllegalArgumentException | IllegalStateException e) {
            // TODO : 400 / 409 비즈니스 예외
            throw e;
        } catch (Exception e) {
            // TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 개설 수정 실패", e
            );
        }
    }

    // 강의 개설 신청 삭제 
    public void deleteLectureRequest(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            LectureValidation validation =
                lectureDAO.getValidation(conn, lectureId);

            if (validation != LectureValidation.PENDING) {
                throw new IllegalStateException(
                    "대기 상태인 강의만 삭제할 수 있습니다."
                );
            }

            scorePolicyDAO.deleteByLectureId(conn, lectureId);

            lectureScheduleDAO.deleteByLectureId(conn, lectureId.intValue());

            lectureDAO.deleteLecture(conn, lectureId);

            conn.commit();

        } catch (IllegalStateException e) {
            // TODO : 409 Conflict
            throw e;
        } catch (Exception e) {
            // TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 삭제 실패", e
            );
        }
    }

    // 강의 요일/시간 조회
    public List<LectureScheduleDTO> getLectureSchedules(Long lectureId) {

        try (Connection conn = DBConnection.getConnection()) {
            return lectureScheduleDAO.selectByLectureId(conn, lectureId);
        } catch (Exception e) {
        	// TODO : 500 Internal Server Error
            throw new RuntimeException(
                "강의 요일/시간 조회 실패", e
            );
        }
    }

    // 내부 유틸 성적 배점 생성 + 검증
    private ScorePolicyDTO buildScorePolicy(
            Long lectureId,
            HttpServletRequest request
    ) {

        ScorePolicyDTO policy = new ScorePolicyDTO();
        policy.setLectureId(lectureId);
        policy.setAttendanceWeight(
            Integer.parseInt(request.getParameter("attendanceWeight"))
        );
        policy.setAssignmentWeight(
            Integer.parseInt(request.getParameter("assignmentWeight"))
        );
        policy.setMidtermWeight(
            Integer.parseInt(request.getParameter("midtermWeight"))
        );
        policy.setFinalWeight(
            Integer.parseInt(request.getParameter("finalWeight"))
        );

        if (policy.getTotalWeight() != 100) {
            throw new IllegalArgumentException(
                "성적 배점의 합은 100%여야 합니다."
            );
        }

        return policy;
    }

    /* ==================================================
     * 내부 유틸 강의 기간 검증
     * ================================================== */
    private void validateLecturePeriod(
            Connection conn,
            HttpServletRequest request
    ) {

        LocalDate start =
            LocalDate.parse(request.getParameter("startDate"));
        LocalDate end =
            LocalDate.parse(request.getParameter("endDate"));

        if (end.isBefore(start)) {
            throw new IllegalArgumentException(
                "강의 종료일은 시작일보다 빠를 수 없습니다."
            );
        }

        LocalDate today = AppTime.now().toLocalDate();

        LocalDate semesterStart =
            scheduleDAO.findNearestScheduleDate(
                conn,
                ScheduleCode.SEMESTER_START,
                today,
                true
            );

        LocalDate semesterEnd =
            scheduleDAO.findNearestScheduleDate(
                conn,
                ScheduleCode.SEMESTER_END,
                semesterStart,
                false
            );

        if (start.isBefore(semesterStart) ||
            end.isAfter(semesterEnd)) {

            throw new IllegalArgumentException(
                "강의 기간은 학기 기간 내에 있어야 합니다."
            );
        }
    }
}