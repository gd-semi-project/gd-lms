package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminService;
import service.SchoolCalendarService;

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
		String actionPath = command.substring("/admin".length());
		
		if (actionPath.isEmpty()) actionPath = "/";
		
		String contentPage = "";
		
		switch(actionPath) {
		
		case "/dashboard":
			contentPage = "/WEB-INF/views/admin/adminDashboard.jsp";
			request.setAttribute("lectureCount", service.getLectureCount());
			request.setAttribute("totalLectureCount", service.getTotalLectureCount());
			request.setAttribute("lectureFillRate", service.getLectureFillRate());
			request.setAttribute("lowFillRateLecture", service.getLowFillRateLecture());
			request.setAttribute("totalLectureCapacity", service.getTotalLectureCapacity());
			request.setAttribute("totalEnrollment", service.getTotalEnrollment());
			request.setAttribute("lectureRequestCount", service.getLectureRequestCount());
			break;
			
		case "/lectureRequest":
			contentPage = "/WEB-INF/views/admin/adminLectureRequest.jsp";
			request.setAttribute("pendingLectureList",service.getPendingLectureList());
			request.setAttribute("canceledLectureList",service.getCanceledLectureList());
			request.setAttribute("confirmedLectureList",service.getConfirmedLectureList());
			break;
			
		case "/noticeList":
			contentPage = "/WEB-INF/views/admin/adminNoticeList.jsp";
			break;
			
		case "/calendar":
			
			break;
			
		case "/campus":
			contentPage = "/WEB-INF/views/admin/adminCampusMap.jsp";
			break;
			
		default:
			break;
		}
		
		request.setAttribute("contentPage", contentPage);
		
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
		rd.forward(request, response);
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/admin".length());
		String action = request.getParameter("action");
		
		switch(actionPath) {
		
		case "/lectureRequest": {
			
			if("CONFIRMED".equals(action)||"CANCELED".equals(action)) {
				long lectureId = Long.parseLong(request.getParameter("lectureId"));
				service.LectureValidate(lectureId, action);
				
				response.sendRedirect(contextPath + "/admin/lectureRequest");
				return;
			} else {
				response.sendRedirect(contextPath + "/admin/lectureRequest");
				return;
			}
		}
		
		}
		
		
	}

}
