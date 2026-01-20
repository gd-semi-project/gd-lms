package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.AdminService;
import service.SchoolCalendarService;
import service.LoginService;

import java.io.IOException;
import java.time.LocalDate;

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
			
			String dpt = request.getParameter("departmentId");
			Long departmentId = (dpt != null && !dpt.isEmpty()) ? Long.parseLong(dpt) : null;
			request.setAttribute("pendingLectureList",service.getPendingLectureList(departmentId));
			request.setAttribute("canceledLectureList",service.getCanceledLectureList(departmentId));
			request.setAttribute("confirmedLectureList",service.getConfirmedLectureList(departmentId));
			request.setAttribute("departmentList", service.getDepartmentList());
			break;
			
		case "/noticeList":
			contentPage = "/WEB-INF/views/admin/adminNoticeList.jsp";
			break;
			
		case "/calendar":
			
			break;
			
		case "/campus":
			contentPage = "/WEB-INF/views/admin/adminCampusMap.jsp";
			break;
		
		// 고희권 추가
		case "/registUser":
			contentPage = "/WEB-INF/views/admin/registUser.jsp";
			break;	
		// registUser
			
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
					Long lectureId = Long.parseLong(request.getParameter("lectureId"));
					service.LectureValidate(lectureId, action);
					
					response.sendRedirect(contextPath + "/admin/lectureRequest");
					break;
				} else if ("selectDepartment".equals(action)){
					if(request.getParameter("departmentId").equals("all")) {
						response.sendRedirect(contextPath + "/admin/lectureRequest");
					} else {
						Long departmentId = Long.parseLong(request.getParameter("departmentId"));
						response.sendRedirect(contextPath + "/admin/lectureRequest?departmentId=" + departmentId);
					}
					break;
				} else {
					response.sendRedirect(contextPath + "/admin/lectureRequest");
					break;
				}
			}
			case "/registUserRequest": {
				UserDTO userDTO = new UserDTO();
				userDTO.setLoginId(request.getParameter("loginId"));
				userDTO.setPassword(request.getParameter("password"));
				userDTO.setName(request.getParameter("name"));
				userDTO.setEmail(request.getParameter("email"));
				userDTO.setBirthDate(LocalDate.parse(request.getParameter("birthDate")));
				
				Role role = Role.fromLabel(request.getParameter("role"));
				userDTO.setRole(role);
				
				LoginService ls = LoginService.getInstance();
				ls.RegistUser(userDTO);
				
				// response.sendRedirect("/gd-lms/login.jsp");
				String contentPage = "/WEB-INF/views/admin/DashBoard.jsp";
				
				request.setAttribute("contentPage", contentPage);
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
				rd.forward(request, response);
			}
			default: break;
		}
		
		
	}

}
