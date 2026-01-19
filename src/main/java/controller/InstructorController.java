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

	private InstructorService instructorService = InstructorService.getInstance();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uri = request.getRequestURI();
		String ctx = request.getContextPath();
		String action = uri.substring(ctx.length() + "/instructor".length());
		if (action.isEmpty())
			action = "/";

		/*
		 * ============================== 🔥 개발용 강사 세션 주입 ==============================
		 */
		HttpSession session = request.getSession(true);

		if (session.getAttribute("UserInfo") == null) {
			UserDTO devInstructor = new UserDTO();
			devInstructor.setUser_id(1L); // lecture.user_id 와 맞추기
			devInstructor.setLogin_id("dev_instructor");
			devInstructor.setName("개발용 강사");
			devInstructor.setRole(Role.INSTRUCTOR);

			session.setAttribute("UserInfo", devInstructor);

			System.out.println("🔥 [DEV] 강사 세션 주입 완료");
		}

		UserDTO loginUser = (UserDTO) session.getAttribute("UserInfo");

		// 권한 체크
		if (loginUser.getRole() != Role.INSTRUCTOR) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		long instructorId = loginUser.getUser_id();
		
		switch (action) {

		// 강사 프로필
		case "/profile": {
			Map<String, Object> profile = instructorService.getInstructorProfile(instructorId, loginUser.getLogin_id());

			request.setAttribute("instructor", profile.get("instructor"));
			request.setAttribute("user", profile.get("user"));
			request.setAttribute("contentPage", "/WEB-INF/views/instructor/profile.jsp");
			break;
		}

		// 내 강의 목록
		case "/lectures": {
			request.setAttribute("lectures", instructorService.getMyLectures(instructorId));
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