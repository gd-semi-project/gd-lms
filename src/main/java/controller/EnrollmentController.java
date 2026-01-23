package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.dto.EnrollmentDTO;
import model.dto.LectureForEnrollDTO;
import model.dto.MypageDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.EnrollmentService;
import service.MyPageService;

import java.io.IOException;
import java.util.List;

// 학생 수강신청 관련 컨트롤러
@WebServlet("/enroll/*")
public class EnrollmentController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private EnrollmentService enrollmentService = new EnrollmentService();
	private MyPageService myPageService = new MyPageService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
		if (access == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}
		// loginId 확보
		String loginId = (String) session.getAttribute("loginId");
		if (loginId == null) {
			// 세션 꼬임 방어
			session.invalidate();
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}
		// ROLE이 STUDENT인경우(학생)만 접근가능
		if (access.getRole() != Role.STUDENT) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Long studentId = (Long) session.getAttribute("userId");
		if (studentId == null) {
			session.invalidate();
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		String action = request.getPathInfo();

		if ("/enrollment".equals(action)) {
			MypageDTO mypage = myPageService.getMypageDTO(loginId);
			if (mypage == null) {
				response.sendRedirect(request.getContextPath() + "/login");
				return;
			}
			// 수강신청 가능한 강의목록
			List<LectureForEnrollDTO> lectureList = enrollmentService.getAvailableLecturesForEnroll();

			// 내가 신청한 강의(수강신청 내역)
			List<EnrollmentDTO> enrollList = enrollmentService.getMyEnrollments(studentId);
			// 이미 신청한 강의(신청한 강의는 숨기기위해)
			List<Long> myLectureId = enrollmentService.getMyLectureId(studentId);

			request.setAttribute("lectureList", lectureList);
			request.setAttribute("enrollList", enrollList);
			request.setAttribute("myLectureId", myLectureId);

			request.setAttribute("mypage", mypage);

			request.setAttribute("contentPage", "/WEB-INF/views/student/enrollmentPage.jsp");

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

			return;
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Long studentId = (Long) session.getAttribute("userId");

		String action = request.getPathInfo();

		try {
			if ("/apply".equals(action)) {
				long lectureId = Long.parseLong(request.getParameter("lectureId"));
				enrollmentService.apply(studentId, lectureId);

				session.setAttribute("alertMsg", "수강신청이 완료되었습니다.");
			}

			else if ("/cancel".equals(action)) {
				long lectureId = Long.parseLong(request.getParameter("lectureId"));
				enrollmentService.cancel(studentId, lectureId);

				session.setAttribute("alertMsg", "수강취소가 완료되었습니다.");
			}

		} catch (RuntimeException e) {
			// 서비스에서 던진 메시지 그대로 알림
			session.setAttribute("alertMsg", e.getMessage());
		}

		// ⭐ 무조건 목록 화면으로 복귀
		response.sendRedirect(request.getContextPath() + "/enroll/enrollment");
	}

}
