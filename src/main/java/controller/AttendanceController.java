package controller;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureSessionDTO;
import model.enumtype.AttendanceStatus;
import model.enumtype.Role;
import service.AttendanceService;
import service.LectureService;
import utils.AppTime;

@WebServlet("/attendance/*")
public class AttendanceController extends HttpServlet {

	private final AttendanceService attendanceService = AttendanceService.getInstance();
	private final LectureService lectureService = LectureService.getInstance();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ctx = request.getContextPath();
		HttpSession session = request.getSession(false);
		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

		if (access == null) {
			response.sendRedirect(ctx + "/login");
			return;
		}

		String action = request.getRequestURI().substring(ctx.length() + "/attendance".length());

		try {

			if ("/open".equals(action)) {

				if (access.getRole() != Role.INSTRUCTOR) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}

				long lectureId = Long.parseLong(request.getParameter("lectureId"));

				long sessionId = attendanceService.openAttendance(lectureId);

				// 수강생 전원 기본 결석 생성
				attendanceService.prepareAttendance(sessionId, lectureId);

				response.sendRedirect(ctx + "/attendance/view" + "?lectureId=" + lectureId + "&sessionId=" + sessionId);
				return;
			}

			if ("/check".equals(action)) {

				if (access.getRole() != Role.STUDENT) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}

				long lectureId = Long.parseLong(request.getParameter("lectureId"));
				long sessionId = Long.parseLong(request.getParameter("sessionId"));

				attendanceService.checkAttendance(sessionId, access.getUserId());

				response.sendRedirect(ctx + "/attendance/view?lectureId=" + lectureId);
				return;
			}

			if ("/update".equals(action)) {

				if (access.getRole() != Role.INSTRUCTOR) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}

				long attendanceId = Long.parseLong(request.getParameter("attendanceId"));
				AttendanceStatus status = AttendanceStatus.valueOf(request.getParameter("status"));

				long lectureId = Long.parseLong(request.getParameter("lectureId"));
				long sessionId = Long.parseLong(request.getParameter("sessionId"));

				attendanceService.updateAttendance(attendanceId, status);

				response.sendRedirect(ctx + "/attendance/view" + "?lectureId=" + lectureId + "&sessionId=" + sessionId);
				return;
			}

			if ("/close".equals(action)) {

				if (access.getRole() != Role.INSTRUCTOR) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}

				long lectureId = Long.parseLong(request.getParameter("lectureId"));
				long sessionId = Long.parseLong(request.getParameter("sessionId"));

				attendanceService.closeAttendance(sessionId);

				response.sendRedirect(ctx + "/attendance/view?lectureId=" + lectureId);
				return;
			}

		} catch (Exception e) {
			session.setAttribute("flashMsg", e.getMessage());
			response.sendRedirect(ctx + "/attendance/view?lectureId=" + request.getParameter("lectureId"));
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ctx = request.getContextPath();
		HttpSession session = request.getSession(false);
		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

		if (access == null) {
			response.sendRedirect(ctx + "/login");
			return;
		}

		long lectureId;
		try {
			lectureId = Long.parseLong(request.getParameter("lectureId"));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required");
			return;
		}

		LectureDTO lecture = lectureService.getLectureDetail(lectureId);
		if (lecture == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		LocalDate todayDate = AppTime.now().toLocalDate();

		request.setAttribute("lecture", lecture);
		request.setAttribute("lectureId", lectureId);
		request.setAttribute("activeTab", "attendance");

		if (access.getRole() == Role.STUDENT) {

			LectureSessionDTO today = attendanceService.getTodaySession(lectureId, todayDate);

			request.setAttribute("todaySession", today);

			if (today != null) {
				request.setAttribute("alreadyChecked",
						attendanceService.isAlreadyChecked(today.getSessionId(), access.getUserId()));
			}

			request.setAttribute("attendanceList",
					attendanceService.getStudentAttendance(lectureId, access.getUserId()));
		}

		if (access.getRole() == Role.INSTRUCTOR) {

			LectureSessionDTO today = attendanceService.getTodaySession(lectureId, todayDate);

			request.setAttribute("todaySession", today);

			boolean attendanceOpen = false;
			if (today != null) {
				attendanceOpen = attendanceService.isAttendanceOpen(today.getSessionId());
			}

			boolean alreadyOpenedToday = attendanceService.hasTodaySession(lectureId);

			request.setAttribute("attendanceOpen", attendanceOpen);

			request.setAttribute("sessions", attendanceService.getSessionsByLecture(lectureId));

			request.setAttribute("alreadyOpenedToday", alreadyOpenedToday);

			String sessionIdParam = request.getParameter("sessionId");

			if (sessionIdParam != null && !sessionIdParam.isBlank()) {

				long sessionId = Long.parseLong(sessionIdParam);

				request.setAttribute("selectedSessionId", sessionId);
				request.setAttribute("sessionAttendance", attendanceService.getSessionAttendance(sessionId));
			}
		}

		request.setAttribute("contentPage", "/WEB-INF/views/lecture/attendance.jsp");

		request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
	}
}