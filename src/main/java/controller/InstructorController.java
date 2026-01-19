package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.LectureDTO;
import service.InstructorService;

@WebServlet("/instructor/*")
public class InstructorController extends HttpServlet {

	private InstructorService instructorService = InstructorService.getInstance();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		String ctx = request.getContextPath();

		// 1️⃣ 로그인 체크
		if (session == null || session.getAttribute("userId") == null) {
			response.sendRedirect(ctx + "/login");
			return;
		}

		// 2️⃣ 권한 체크
		String role = (String) session.getAttribute("role");
		if (!"INSTRUCTOR".equals(role)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Long instructorId = (Long) session.getAttribute("userId");

		String uri = request.getRequestURI();
		String action = uri.substring(ctx.length() + "/instructor".length());

		if (action.isEmpty())
			action = "/lectures";

		switch (action) {

		// ✅ 강사 프로필
		case "/profile": {
			Map<String, Object> profile = instructorService.getInstructorProfile(instructorId,
					(String) session.getAttribute("userName") // or loginId
			);

			request.setAttribute("instructor", profile.get("instructor"));
			request.setAttribute("user", profile.get("user"));
			request.setAttribute("contentPage", "/WEB-INF/views/instructor/profile.jsp");
			break;
		}

		// ✅ 내 강의 목록
		case "/lectures": {
			List<LectureDTO> lectures = instructorService.getMyLectures(instructorId);

			request.setAttribute("lectures", lectures);
			request.setAttribute("contentPage", "/WEB-INF/views/instructor/lectureList.jsp");
			break;
		}

		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
	}
}