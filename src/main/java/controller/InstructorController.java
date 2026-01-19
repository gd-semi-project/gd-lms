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
import model.dto.InstructorDTO;
import model.dto.LectureDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.InstructorService;

@WebServlet("/instructor/*")
public class InstructorController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private InstructorService instructorService = InstructorService.getInstance();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uri = request.getRequestURI(); // /gd-lms/instructor/profile
		String ctx = request.getContextPath(); // /gd-lms
		String action = uri.substring(ctx.length() + "/instructor".length());

		if (action.isEmpty())
			action = "/";
		
		// 임시 세션
		HttpSession session = request.getSession(true);

		if (session.getAttribute("UserInfo") == null) {
			UserDTO testUser = new UserDTO();
			testUser.setUser_id(1L);
			testUser.setLogin_id("test_instructor");
			testUser.setName("테스트 강사");
			testUser.setRole(Role.INSTRUCTOR);

			session.setAttribute("UserInfo", testUser);
			System.out.println("🔥 테스트용 강사 세션 주입 완료");
		}

		UserDTO loginUser = (UserDTO) session.getAttribute("UserInfo");

		if (loginUser.getRole() != Role.INSTRUCTOR) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		long instructorId = loginUser.getUser_id();

		switch (action) {

		
		// 강사 정보 조회
		case "/profile": {

			Map<String, Object> profile = instructorService.getInstructorProfile(instructorId, loginUser.getLogin_id());

			request.setAttribute("instructor", profile.get("instructor"));
			request.setAttribute("user", profile.get("user"));

			request.setAttribute("contentPage", "/WEB-INF/views/instructor/profile.jsp");

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
			return;
		}

		// 담당 강의 목록
		case "/lectures": {
			List<LectureDTO> lectures = instructorService.getMyLectures(instructorId);

			request.setAttribute("lectures", lectures);
			request.setAttribute("contentPage", "/WEB-INF/views/instructor/lectureList.jsp");

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
			return;
		}

		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}