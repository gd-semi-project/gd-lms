package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import database.DBConnection;
import exception.BadRequestException;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import model.dao.*;
import model.dto.*;
import model.enumtype.*;
import utils.AppTime;

public class LectureRequestService {

    private static final LectureRequestService instance = new LectureRequestService();
    private LectureRequestService() {}
    public static LectureRequestService getInstance() { return instance; }

    private final LectureRequestDAO lectureDAO = LectureRequestDAO.getInstance();
    private final SchoolScheduleDAO scheduleDAO = SchoolScheduleDAO.getInstance();
    private final ScorePolicyDAO scorePolicyDAO = ScorePolicyDAO.getInstance();
    private final LectureScheduleDAO lectureScheduleDAO = LectureScheduleDAO.getInstance();

    // 신청 기간 여부
    public boolean isLectureRequestPeriod() {
        try (Connection conn = DBConnection.getConnection()) {
            LocalDate today = AppTime.now().toLocalDate();

            SchoolScheduleDTO schedule =
                scheduleDAO.findNearestSchedule(conn, ScheduleCode.LECTURE_OPEN_REQUEST, today);

            if (schedule == null) {
                return false;
            }

            return !today.isBefore(schedule.getStartDate())
                && !today.isAfter(schedule.getEndDate());

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("강의 신청 기간 확인 실패", e);
        }
    }

    public SchoolScheduleDTO getNearestLectureRequestPeriod() {
        try (Connection conn = DBConnection.getConnection()) {

            return scheduleDAO.findNearestSchedule(
                conn,
                ScheduleCode.LECTURE_OPEN_REQUEST,
                AppTime.now().toLocalDate()
            );

        } catch (Exception e) {
            throw new InternalServerException("강의 신청 기간 조회 실패", e);
        }
    }

    public List<LectureRequestDTO> getMyLectureRequests(Long instructorId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectByInstructor(conn, instructorId);
        } catch (Exception e) {
            throw new InternalServerException("강의 개설 신청 목록 조회 실패", e);
        }
    }

    public LectureRequestDTO getLectureRequestDetail(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            LectureRequestDTO dto = lectureDAO.selectByLectureId(conn, lectureId);
            if (dto == null) {
                throw new ResourceNotFoundException("강의 개설 신청 정보가 없습니다.");
            }
            return dto;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("강의 개설 신청 상세 조회 실패", e);
        }
    }

    public ScorePolicyDTO getScorePolicy(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            ScorePolicyDTO dto = scorePolicyDAO.findByLectureId(conn, lectureId);
            if (dto == null) {
                throw new ResourceNotFoundException("성적 배점 정보가 없습니다.");
            }
            return dto;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("성적 배점 조회 실패", e);
        }
    }

    public List<RoomDTO> getAllRooms() {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectAllRooms(conn);
        } catch (Exception e) {
            throw new InternalServerException("강의실 목록 조회 실패", e);
        }
    }

    public void createLectureRequest(Long instructorId, HttpServletRequest request) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            validateLecturePeriod(conn, request);

            Long lectureId = lectureDAO.insertLecture(conn, instructorId, request);
            lectureDAO.insertSchedule(conn, lectureId, request);

            ScorePolicyDTO policy = buildScorePolicy(lectureId, request);
            scorePolicyDAO.insert(conn, policy);

            conn.commit();

        } catch (BadRequestException e) {
            throw e; // 메시지 유지
        } catch (Exception e) {
            throw new InternalServerException("강의 개설 신청 실패", e);
        }
    }

    public void updateLectureRequest(Long lectureId, HttpServletRequest request) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            LectureValidation validation = lectureDAO.getValidation(conn, lectureId);
            if (validation == LectureValidation.CANCELED) {
                throw new BadRequestException("반려된 강의는 수정할 수 없습니다.");
            }

            validateLecturePeriod(conn, request);

            lectureDAO.updateLecture(conn, lectureId, request);

            ScorePolicyDTO policy = buildScorePolicy(lectureId, request);

            if (scorePolicyDAO.existsByLectureId(conn, lectureId)) {
                scorePolicyDAO.update(conn, policy);
            } else {
                scorePolicyDAO.insert(conn, policy);
            }

            conn.commit();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("강의 개설 수정 실패", e);
        }
    }

    public void deleteLectureRequest(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            LectureValidation validation = lectureDAO.getValidation(conn, lectureId);
            if (validation != LectureValidation.PENDING) {
                throw new BadRequestException("대기 상태인 강의만 삭제할 수 있습니다.");
            }

            scorePolicyDAO.deleteByLectureId(conn, lectureId);
            lectureScheduleDAO.deleteByLectureId(conn, lectureId.intValue());
            lectureDAO.deleteLecture(conn, lectureId);

            conn.commit();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("강의 삭제 실패", e);
        }
    }

    public List<LectureScheduleDTO> getLectureSchedules(Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureScheduleDAO.selectByLectureId(conn, lectureId);
        } catch (Exception e) {
            throw new InternalServerException("강의 일정 조회 실패", e);
        }
    }

    // ================= 내부 유틸 =================

    private ScorePolicyDTO buildScorePolicy(Long lectureId, HttpServletRequest request) {

        try {
            ScorePolicyDTO policy = new ScorePolicyDTO();
            policy.setLectureId(lectureId);
            policy.setAttendanceWeight(Integer.parseInt(request.getParameter("attendanceWeight")));
            policy.setAssignmentWeight(Integer.parseInt(request.getParameter("assignmentWeight")));
            policy.setMidtermWeight(Integer.parseInt(request.getParameter("midtermWeight")));
            policy.setFinalWeight(Integer.parseInt(request.getParameter("finalWeight")));

            if (policy.getTotalWeight() != 100) {
                throw new BadRequestException("성적 배점의 합은 100%여야 합니다.");
            }

            return policy;

        } catch (NumberFormatException e) {
            throw new BadRequestException("성적 배점 값이 올바르지 않습니다.");
        }
    }

    private void validateLecturePeriod(Connection conn, HttpServletRequest request) {

        try {
            String startParam = request.getParameter("startDate");
            String endParam   = request.getParameter("endDate");

            if (startParam == null || startParam.isBlank()
                || endParam == null || endParam.isBlank()) {
                throw new BadRequestException("강의 시작일/종료일이 필요합니다.");
            }

            LocalDate start = LocalDate.parse(startParam);
            LocalDate end   = LocalDate.parse(endParam);

            if (end.isBefore(start)) {
                throw new BadRequestException("강의 종료일은 시작일보다 빠를 수 없습니다.");
            }

            LocalDate today = AppTime.now().toLocalDate();

            LocalDate semesterStart =
                scheduleDAO.findNearestScheduleDate(conn, ScheduleCode.SEMESTER_START, today, true);

            if (semesterStart == null) {
                throw new ResourceNotFoundException("학기 시작 일정(Semester Start) 정보가 없습니다.");
            }

            LocalDate semesterEnd =
                scheduleDAO.findNearestScheduleDate(conn, ScheduleCode.SEMESTER_END, semesterStart, false);

            if (semesterEnd == null) {
                throw new ResourceNotFoundException("학기 종료 일정(Semester End) 정보가 없습니다.");
            }

            if (start.isBefore(semesterStart) || end.isAfter(semesterEnd)) {
                throw new BadRequestException(
                    "강의 기간은 학기 기간 내에 있어야 합니다. (학기: "
                    + semesterStart + " ~ " + semesterEnd + ")"
                );
            }

        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("강의 기간 검증 실패", e);
        }
    }
}