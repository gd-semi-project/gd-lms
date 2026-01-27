package controller;

import java.io.IOException;
import java.time.LocalDate;

import exception.AccessDeniedException;
import exception.BadRequestException;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureSessionDTO;
import model.enumtype.AttendanceStatus;
import model.enumtype.Role;
import service.AttendanceService;
import service.LectureAccessService;
import service.LectureService;
import utils.AppTime;

@WebServlet("/attendance/*")
public class AttendanceController extends HttpServlet {

    private final AttendanceService attendanceService = AttendanceService.getInstance();
    private final LectureService lectureService = LectureService.getInstance();
    private final LectureAccessService lectureAccessService = new LectureAccessService();

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);
        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

        if (access == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

        String action = request.getRequestURI()
                .substring(ctx.length() + "/attendance".length());

        try {

            long lectureId = parseLongOrThrow(request.getParameter("lectureId"));
            Role role = access.getRole();

            // 🔐 기본 권한 체크 (수강 여부 / 담당 강의 여부)
            lectureAccessService.assertCanAccessLecture(
                    access.getUserId(), lectureId, role
            );

            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                throw new ResourceNotFoundException("강의가 존재하지 않습니다.");
            }

            // ✅ 학생만 승인+진행중 체크
            if (role == Role.STUDENT) {
                lectureAccessService.assertLectureIsOpen(lecture);
            }

            if ("/open".equals(action)) {

                long sessionId = attendanceService.openAttendance(lectureId);
                attendanceService.prepareAttendance(sessionId, lectureId);

                response.sendRedirect(ctx + "/attendance/view?lectureId="
                        + lectureId + "&sessionId=" + sessionId);
                return;
            }

            if ("/check".equals(action)) {

                long sessionId = parseLongOrThrow(request.getParameter("sessionId"));

                attendanceService.checkAttendance(sessionId, access.getUserId());

                response.sendRedirect(ctx + "/attendance/view?lectureId=" + lectureId);
                return;
            }

            if ("/update".equals(action)) {

                long attendanceId = parseLongOrThrow(request.getParameter("attendanceId"));
                long sessionId = parseLongOrThrow(request.getParameter("sessionId"));

                AttendanceStatus status =
                        AttendanceStatus.valueOf(request.getParameter("status"));

                attendanceService.updateAttendance(attendanceId, status);

                response.sendRedirect(ctx + "/attendance/view?lectureId="
                        + lectureId + "&sessionId=" + sessionId);
                return;
            }

            if ("/close".equals(action)) {

                long sessionId = parseLongOrThrow(request.getParameter("sessionId"));

                attendanceService.closeAttendance(sessionId);

                response.sendRedirect(ctx + "/attendance/view?lectureId=" + lectureId);
                return;
            }

            throw new ResourceNotFoundException("요청하신 작업을 처리할 수 없습니다.");

        } catch (BadRequestException e) {
            session.setAttribute("flashMsg", e.getMessage());
            response.sendRedirect(ctx + "/attendance/view?lectureId="
                    + request.getParameter("lectureId"));

        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (ResourceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (InternalServerException e) {
            throw e;
        }
    }

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);
        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

        if (access == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

        try {

            long lectureId = parseLongOrThrow(request.getParameter("lectureId"));
            Role role = access.getRole();

            // 🔐 기본 권한 체크
            lectureAccessService.assertCanAccessLecture(
                    access.getUserId(), lectureId, role
            );

            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                throw new ResourceNotFoundException("강의가 존재하지 않습니다.");
            }

            // ✅ 학생만 승인 + 진행중 체크
            if (role == Role.STUDENT) {
                lectureAccessService.assertLectureIsOpen(lecture);
            }

            LocalDate todayDate = AppTime.now().toLocalDate();

            request.setAttribute("lecture", lecture);
            request.setAttribute("lectureId", lectureId);
            request.setAttribute("activeTab", "attendance");

            // ================= 학생 =================
            if (role == Role.STUDENT) {

                LectureSessionDTO today =
                        attendanceService.getTodaySession(lectureId, todayDate);

                request.setAttribute("todaySession", today);

                if (today != null) {
                    request.setAttribute("alreadyChecked",
                            attendanceService.isAlreadyChecked(
                                    today.getSessionId(),
                                    access.getUserId()));
                }

                request.setAttribute("attendanceList",
                        attendanceService.getStudentAttendance(
                                lectureId,
                                access.getUserId()));
            }

            // ================= 강사 =================
            if (role == Role.INSTRUCTOR) {

                LectureSessionDTO today =
                        attendanceService.getTodaySession(lectureId, todayDate);

                request.setAttribute("todaySession", today);

                boolean attendanceOpen = false;
                if (today != null) {
                    attendanceOpen =
                            attendanceService.isAttendanceOpen(today.getSessionId());
                }

                boolean alreadyOpenedToday =
                        attendanceService.hasTodaySession(lectureId);

                request.setAttribute("attendanceOpen", attendanceOpen);
                request.setAttribute("alreadyOpenedToday", alreadyOpenedToday);
                request.setAttribute("sessions",
                        attendanceService.getSessionsByLecture(lectureId));

                String sessionIdParam = request.getParameter("sessionId");

                if (sessionIdParam != null && !sessionIdParam.isBlank()) {

                    long sessionId = parseLongOrThrow(sessionIdParam);

                    request.setAttribute("selectedSessionId", sessionId);
                    request.setAttribute("sessionAttendance",
                            attendanceService.getSessionAttendance(sessionId));
                }
            }

            request.setAttribute("contentPage",
                    "/WEB-INF/views/lecture/attendance.jsp");

            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (ResourceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (BadRequestException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("contentPage",
                    "/WEB-INF/views/lecture/attendance.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (InternalServerException e) {
            throw e;
        }
    }

    // ===================== 유틸 =====================
    private long parseLongOrThrow(String param) {
        try {
            return Long.parseLong(param);
        } catch (Exception e) {
            throw new BadRequestException("요청 파라미터 형식이 올바르지 않습니다.");
        }
    }
}