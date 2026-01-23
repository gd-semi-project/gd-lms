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
			
			
			// 내가 신청한 강의
			List<EnrollmentDTO> enrollList = enrollmentService.getMyEnrollments(studentId);
			
			request.setAttribute("lectureList", lectureList);
			request.setAttribute("enrollList", enrollList);

			request.setAttribute("mypage", mypage);

			request.setAttribute("contentPage", "/WEB-INF/views/student/enrollmentPage.jsp");

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

			return;
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
