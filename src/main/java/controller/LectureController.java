package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.Role;
import service.InstructorService;
import service.LectureRequestService;
import service.LectureService;
import service.ScorePolicyService;

@WebServlet("/lecture/*")
public class LectureController extends HttpServlet {
	
	private final LectureService lectureService = LectureService.getInstance();
	private final LectureRequestService lectureRequestService =
		    LectureRequestService.getInstance();

		private final ScorePolicyService scorePolicyService =
		    ScorePolicyService.getInstance();

		private final InstructorService instructorService =
		    InstructorService.getInstance();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String ctx = request.getContextPath();
		String uri = request.getRequestURI();
		String action = uri.substring(ctx.length() + "/lecture".length());
		if (action == null || action.isBlank())
			action = "/detail";
		
		HttpSession session = request.getSession(false);
		AccessDTO accessInfo = (AccessDTO) session.getAttribute("AccessInfo");

		Long userId = accessInfo.getUserId();
		if (userId == null) {
		    session.invalidate();
		    session = request.getSession();
		    session.setAttribute("errorMessage", "세션 정보가 유효하지 않습니다.");
		    response.sendRedirect(ctx + "/error?errorCode=401");
		    return;
		}

		switch (action) {
		case "/detail": {
		    Long lectureId = parseLong(request.getParameter("lectureId"));
		    
		    if (lectureId == null) {
		    	// TODO : 400 Bad Request
		    	session.setAttribute("errorMessage", "접근 권한이 없습니다.");
		        response.sendRedirect(ctx + "/error?errorCode=403");
		        return;
		    }

		    LectureDTO lecture = lectureService.getLectureDetail(lectureId);
		    if (lecture == null) {
		    	// TODO : 404 Not Found
		        response.sendError(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 강의입니다.");
		        return;
		    }

		    // 강의 기본
		    request.setAttribute("lecture", lecture);

		    // 요일/시간
		    request.setAttribute(
		        "schedules",
		        lectureRequestService.getLectureSchedules(lectureId)
		    );

		    // 배점
		    request.setAttribute(
		        "scorePolicy",
		        scorePolicyService.getPolicy(lectureId)
		    );

		    // 담당 강사
		    var profile =
		        instructorService.getInstructorProfile(lecture.getUserId());
		    request.setAttribute("instructor", profile.get("instructor"));
		    request.setAttribute("user", profile.get("user"));

		    request.setAttribute("activeTab", "detail");
		    request.setAttribute(
		        "contentPage",
		        "/WEB-INF/views/lecture/detail.jsp"
		    );
		    break;
		}

		case "/students": {
			Long lectureId = parseLong(request.getParameter("lectureId"));
			if (lectureId == null) {
				// TODO : 400 Bad Request
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "강의 정보가 올바르지 않습니다.");
				return;
			}
			List<LectureStudentDTO> students = lectureService.getLectureStudents(lectureId);
			
			LectureDTO lecture = lectureService.getLectureDetail(lectureId);
		    if (lecture == null) {
		    	// TODO : 404 Not Found
		        response.sendError(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 강의입니다.");
		        return;
		    }
			
		    request.setAttribute("lecture", lecture);
			request.setAttribute("students", students);
			request.setAttribute("lectureId", lectureId);
			request.setAttribute("activeTab", "students");
			request.setAttribute("contentPage", "/WEB-INF/views/lecture/students.jsp");
			break;
		}

		default:
			// TODO : 404 Not Found
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "요청하신 페이지를 찾을 수 없습니다.");
			return;
		}
		request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
	}

	private Long parseLong(String s) {
		try {
			return (s == null || s.isBlank()) ? null : Long.parseLong(s);
		} catch (Exception e) {
			return null;
		}
	}
}