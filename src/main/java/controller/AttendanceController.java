package controller;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.LectureDTO;
import model.dto.LectureSessionDTO;
import model.dto.UserDTO;
import model.enumtype.AttendanceStatus;
import model.enumtype.Role;

import service.AttendanceService;
import service.LectureService;
import service.LectureSessionService;

@WebServlet("/attendance/*")
public class AttendanceController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AttendanceService attendanceService = AttendanceService.getInstance();
    private LectureService lectureService = LectureService.getInstance();
    private LectureSessionService lectureSessionService = LectureSessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("UserInfo");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String lectureIdParam = request.getParameter("id");
        if (lectureIdParam == null || lectureIdParam.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId 누락");
            return;
        }

        long lectureId = Long.parseLong(lectureIdParam);

        LectureDTO lecture = lectureService.getLectureDetail(lectureId);
        request.setAttribute("lecture", lecture);
        request.setAttribute("activeTab", "attendance");

        /* ================= 학생 ================= */
        if (loginUser.getRole() == Role.STUDENT) {

            long studentId = loginUser.getUser_id();

            LectureSessionDTO todaySession =
                    lectureSessionService.getTodaySession(
                            lectureId,
                            LocalDate.now()
                    );

            request.setAttribute("todaySession", todaySession);
            request.setAttribute(
                    "attendanceList",
                    attendanceService.getStudentAttendance(studentId, lectureId)
            );
        }

        /* ================= 교수 ================= */
        if (loginUser.getRole() == Role.INSTRUCTOR) {

            request.setAttribute(
                    "sessions",
                    lectureSessionService.getSessionsByLecture(lectureId)
            );

            String sessionIdParam = request.getParameter("sessionId");
            if (sessionIdParam != null && !sessionIdParam.isBlank()) {

                long sessionId = Long.parseLong(sessionIdParam);

                // 자동 경석 처리
                attendanceService.autoMarkAbsent(sessionId);

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

        request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        /* ================= 학생 출석 ================= */
        if (uri.endsWith("/check")) {

            long sessionId = Long.parseLong(request.getParameter("sessionId"));
            long lectureId = Long.parseLong(request.getParameter("lectureId"));

            UserDTO user = (UserDTO) request.getSession().getAttribute("UserInfo");

            try {
                attendanceService.checkAttendance(sessionId, user.getUser_id());
            } catch (IllegalStateException e) {
                request.getSession().setAttribute("alertMsg", e.getMessage());
            }

            response.sendRedirect(
                    request.getContextPath() + "/attendance?id=" + lectureId
            );
        }

        /* ================= 교수 출결 수정 ================= */
        if (uri.endsWith("/update")) {

            long sessionId = Long.parseLong(request.getParameter("sessionId"));
            long studentId = Long.parseLong(request.getParameter("studentId"));
            AttendanceStatus status =
                    AttendanceStatus.valueOf(request.getParameter("status"));

            attendanceService.updateAttendanceStatus(sessionId, studentId, status);
            response.sendRedirect(request.getHeader("Referer"));
        }
    }
}