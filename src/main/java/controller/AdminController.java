package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.ScheduleUiPolicyDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import model.enumtype.ScheduleCode;
import service.AdminService;
import service.SchoolCalendarService;
import utils.AppTime;
import service.LoginService;
import service.SchedulePolicyService;

import java.io.IOException;
import java.time.LocalDate;

import automation.schedule.SchoolScheduleDAOImpl;

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
		SchedulePolicyService schedulePolicyService = new SchedulePolicyService(new SchoolScheduleDAOImpl());
		
		if (actionPath.isEmpty()) actionPath = "/";
		
		String contentPage = "";
		
		switch(actionPath) {
		
		case "/dashboard":{
			contentPage = "/WEB-INF/views/admin/adminDashboard.jsp";
			ScheduleUiPolicyDTO policy = schedulePolicyService.buildUiPolicyAnyOf(
					"현재는 수강신청 기간이 아닙니다.",
					AppTime.now(), 
					ScheduleCode.COURSE_REG_FRESHMAN.name(), 
					ScheduleCode.COURSE_REG_ENROLLED.name(), 
					ScheduleCode.COURSE_ADD_DROP.name() 
					);
			
			request.setAttribute("policy", policy);
			if(policy.isAvailable()) {
				request.setAttribute("lectureCount", service.getLectureCount());
				request.setAttribute("totalLectureCount", service.getTotalLectureCount());
				request.setAttribute("lectureFillRate", service.getLectureFillRate());
				request.setAttribute("lowFillRateLecture", service.getLowFillRateLecture());
				request.setAttribute("totalLectureCapacity", service.getTotalLectureCapacity());
				request.setAttribute("totalEnrollment", service.getTotalEnrollment());
				request.setAttribute("lectureRequestCount", service.getLectureRequestCount());
			}
			
			break;
		}
		case "/lectureRequest":{
			contentPage = "/WEB-INF/views/admin/adminLectureRequest.jsp";
			ScheduleUiPolicyDTO policy = schedulePolicyService.buildUiPolicyAnyOf(
					"현재는 강의개설 요청 기간이 아닙니다.",
					AppTime.now(), 
					ScheduleCode.LECTURE_OPEN_APPROVAL_ADMIN.name(), 
					ScheduleCode.LECTURE_OPEN_REVISION_WINDOW.name(), 
					ScheduleCode.LECTURE_OPEN_REVIEW_DEPT.name(),
					ScheduleCode.LECTURE_OPEN_REQUEST.name()
					);
			
			request.setAttribute("policy", policy);
			
			if(policy.isAvailable()) {
				String dpt = request.getParameter("departmentId");
				Long departmentId = (dpt != null && !dpt.isEmpty()) ? Long.parseLong(dpt) : null;
				request.setAttribute("pendingLectureList",service.getPendingLectureList(departmentId));
				request.setAttribute("canceledLectureList",service.getCanceledLectureList(departmentId));
				request.setAttribute("confirmedLectureList",service.getConfirmedLectureList(departmentId));
				request.setAttribute("departmentList", service.getDepartmentList());
			}
			break;
		}
		
		case "/departmentManage":
			contentPage = "/WEB-INF/views/admin/adminDepartmentManage.jsp";
			request.setAttribute("departmentList", service.getDepartmentList());
			
			String selectedDept = request.getParameter("departmentId");
			String status = request.getParameter("status");
			if (selectedDept != null && !selectedDept.isBlank()) {
				try {
					long departmentId = Long.parseLong(selectedDept);
					request.setAttribute("selectedDepartment", service.getDepartmentById(departmentId));
					request.setAttribute("instructorList", service.getAllInstructorByDepartment(departmentId, status));
					request.setAttribute("studentList", service.getAllStudentByDepartment(departmentId, status));
				} catch (NumberFormatException ignore) {
					System.out.println("이게 에러나면 진짜 신기할듯");
				}
			}
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
