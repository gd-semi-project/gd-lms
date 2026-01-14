package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminService;

import java.io.IOException;

/**
 * Servlet implementation class AdminController
 */
@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AdminService service = AdminService.getInstance();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String action = command.substring("/admin".length());
		
		if (action.isEmpty()) action = "/";
		
		String contentPage = "";
		
		switch(action) {
		
		case "/dashboard":
			contentPage = "/WEB-INF/testViewSihyeon/dashboardTest.jsp";
			request.setAttribute("lectureCount", service.getLectureCount());
			request.setAttribute("totalLectureCount", service.getTotalLectureCount());
			request.setAttribute("lectureFillRate", service.getLectureFillRate());
			request.setAttribute("lowFillRateLecture", service.getLowFillRateLecture());
			request.setAttribute("totalLectureCapacity", service.getTotalLectureCapacity());
			request.setAttribute("totalEnrollment", service.getTotalEnrollment());
			request.setAttribute("lectureRequestCount", service.getLectureRequestCount());
			break;
			
		case "/lectureRequest":
			contentPage = "/WEB-INF/testViewSihyeon/adminLectureRequestTest.jsp";
			break;
			
		case "/noticeList":
			contentPage = "/WEB-INF/testViewSihyeon/adminNoticeListTest.jsp";
			break;
			
		case "/calendar":
			contentPage = "/WEB-INF/testViewSihyeon/adminCalendarManagement.jsp";
			break;
			
		case "/campus":
			contentPage = "/WEB-INF/testViewSihyeon/adminCampusMap.jsp";
			break;
			
		default:
			break;
		}
		
		request.setAttribute("contentPage", contentPage);
		
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/testViewSihyeon/layoutTest.jsp");
		rd.forward(request, response);
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
