package controller;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.LectureSessionDTO;
import model.enumtype.AttendanceStatus;
import model.enumtype.Role;
import service.AttendanceService;
import service.LectureSessionService;

@WebServlet("/attendance/*")
public class AttendanceController extends HttpServlet {

    private final AttendanceService attendanceService =
            AttendanceService.getInstance();

    private final LectureSessionService lectureSessionService =
            LectureSessionService.getInstance();

    /* =================================================
     * POST
     * ================================================= */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);
        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

        String action =
                request.getRequestURI()
                       .substring(ctx.length() + "/attendance".length());

        try {

            /* =====================================
             * 교수: 출석 시작
             * → 회차 생성 + 기본 ABSENT 생성
             * POST /attendance/open
             * ===================================== */
            if ("/open".equals(action)) {

                if (access.getRole() != Role.INSTRUCTOR) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                long lectureId =
                        Long.parseLong(request.getParameter("lectureId"));

                // 1️⃣ 오늘 회차 생성
                long sessionId =
                        lectureSessionService.createTodaySession(lectureId);

                // 2️⃣ 수강생 전원 기본 결석 생성
                attendanceService.prepareAttendance(sessionId, lectureId);

                response.sendRedirect(
                        ctx + "/attendance/view?lectureId="
                                + lectureId
                                + "&sessionId="
                                + sessionId
                );
                return;
            }

            /* =====================================
             * 학생: 출석 체크
             * POST /attendance/check
             * ===================================== */
            if ("/check".equals(action)) {

                if (access.getRole() != Role.STUDENT) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                long sessionId =
                        Long.parseLong(request.getParameter("sessionId"));
                long lectureId =
                        Long.parseLong(request.getParameter("lectureId"));

                attendanceService.checkAttendance(
                        sessionId,
                        access.getUserId()
                );

                response.sendRedirect(
                        ctx + "/attendance/view?lectureId=" + lectureId
                );
                return;
            }

            /* =====================================
             * 교수: 출결 수동 수정
             * POST /attendance/update
             * ===================================== */
            if ("/update".equals(action)) {

                if (access.getRole() != Role.INSTRUCTOR) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                long attendanceId =
                        Long.parseLong(request.getParameter("attendanceId"));
                AttendanceStatus status =
                        AttendanceStatus.valueOf(
                                request.getParameter("status")
                        );

                long lectureId =
                        Long.parseLong(request.getParameter("lectureId"));
                long sessionId =
                        Long.parseLong(request.getParameter("sessionId"));

                attendanceService.updateAttendance(attendanceId, status);

                response.sendRedirect(
                        ctx + "/attendance/view?lectureId="
                                + lectureId
                                + "&sessionId="
                                + sessionId
                );
                return;
            }

        } catch (Exception e) {
            session.setAttribute("flashMsg", e.getMessage());
            response.sendRedirect(
                    ctx + "/attendance/view?lectureId="
                            + request.getParameter("lectureId")
            );
        }
    }

    /* =================================================
     * GET : 출석 화면
     * ================================================= */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);
        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

        long lectureId =
                Long.parseLong(request.getParameter("lectureId"));

        request.setAttribute("lectureId", lectureId);
        request.setAttribute("activeTab", "attendance");

        /* ---------- 학생 ---------- */
        if (access.getRole() == Role.STUDENT) {

            LectureSessionDTO today =
                    lectureSessionService.getTodaySession(
                            lectureId,
                            LocalDate.now()
                    );

            request.setAttribute("todaySession", today);

            if (today != null) {
                request.setAttribute(
                        "alreadyChecked",
                        attendanceService.isAlreadyChecked(
                                today.getSessionId(),
                                access.getUserId()
                        )
                );
            }

            request.setAttribute(
                    "attendanceList",
                    attendanceService.getStudentAttendance(
                            lectureId,
                            access.getUserId()
                    )
            );
        }

        /* ---------- 교수 ---------- */
        if (access.getRole() == Role.INSTRUCTOR) {

            request.setAttribute(
                    "sessions",
                    lectureSessionService.getSessionsByLecture(lectureId)
            );

            String sessionIdParam =
                    request.getParameter("sessionId");

            if (sessionIdParam != null && !sessionIdParam.isBlank()) {

                long sessionId =
                        Long.parseLong(sessionIdParam);

                request.setAttribute("selectedSessionId", sessionId);
                request.setAttribute(
                        "sessionAttendance",
                        attendanceService.getSessionAttendance(sessionId)
                );
            }
        }

        request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/attendance.jsp"
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/layout/layout.jsp"
        ).forward(request, response);
    }
}