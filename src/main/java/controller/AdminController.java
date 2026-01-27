package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.LectureDAO;
import model.dao.LectureRequestDAO;
import model.dao.SchoolScheduleDAO;
import model.dto.CodeOptionDTO;
import model.dto.LectureCountByValidationDTO;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.ScheduleUiPolicyDTO;
import model.dto.SchoolScheduleDTO;
import model.dto.StudentInfoUpdateRequestDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import model.enumtype.ScheduleCode;
import service.AdminService;
import service.LectureService;
import service.SchoolCalendarService;
import utils.AppTime;
import service.LoginService;
import service.SchedulePolicyService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import automation.schedule.SchoolScheduleDAOImpl;

/**
 * Servlet implementation class AdminController
 */
@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AdminService service = AdminService.getInstance();
	private LectureService lectureService = LectureService.getInstance();
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
				
				request.setAttribute("departmentList", service.getDepartmentList());
				
				String status = request.getParameter("status");
				if (status == null || status.isBlank()) status = "ACTIVE";
				
				String lectureStatus = request.getParameter("lectureStatus");
				if (lectureStatus == null || lectureStatus.isBlank()) lectureStatus = "ALL";

				String selectedDept = request.getParameter("departmentId");
				if (selectedDept != null && !selectedDept.isBlank()) {
					try {
						long departmentId = Long.parseLong(selectedDept);
						request.setAttribute("selectedDepartment", service.getDepartmentById(departmentId));
						
						List<LectureDTO> lectureList = lectureService.getAllLectureByDepartment(departmentId, lectureStatus);
						Map<Long, Integer> enrollCountMap = lectureService.getEnrollCountByLectureId(lectureList);
						
						request.setAttribute("lectureList", lectureList);
						request.setAttribute("enrollCountMap", enrollCountMap);
						
						
						int capacitySum = 0;
			            int currentSum = 0;
			            for (var l : lectureList) {
			                capacitySum += l.getCapacity();
			                Integer cur = enrollCountMap.get(l.getLectureId());
			                currentSum += (cur == null ? 0 : cur);
			            }
			            request.setAttribute("capacitySum", capacitySum);
			            request.setAttribute("currentSum", currentSum);
						
						
					} catch (NumberFormatException ignore) {
						//TODO 이거 에러날 일 없어요
						System.out.println("이게 에러나면 진짜 신기할듯");
						return;
					}
				} else {
					request.setAttribute("lectureList", lectureService.getAllLecture());
					break;
				}
			} else break;
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
				
				LectureCountByValidationDTO lcbvDTO = service.getLectureCountByValidation();
				request.setAttribute("totalCount", lcbvDTO.getTotalCount());
				request.setAttribute("confirmedCount", lcbvDTO.getConfirmedCount());
				request.setAttribute("pendingCount", lcbvDTO.getPendingCount());
				request.setAttribute("canceledCount", lcbvDTO.getCanceledCount());
				
				
				
			}
			break;
		}
		
		case "/departmentManage":
			contentPage = "/WEB-INF/views/admin/adminDepartmentManage.jsp";
			request.setAttribute("departmentList", service.getDepartmentList());
			
			
			String status = request.getParameter("status");
			if (status == null || status.isBlank()) status = "ACTIVE";
			
			String lectureStatus = request.getParameter("lectureStatus");
			if (lectureStatus == null || lectureStatus.isBlank()) lectureStatus = "ALL";

			request.setAttribute("currentStatus", status);
			request.setAttribute("lectureStatus", lectureStatus);
			
			String selectedDept = request.getParameter("departmentId");
			if (selectedDept != null && !selectedDept.isBlank()) {
				try {
					long departmentId = Long.parseLong(selectedDept);
					request.setAttribute("selectedDepartment", service.getDepartmentById(departmentId));
					request.setAttribute("instructorList", service.getAllInstructorByDepartment(departmentId, status));
					request.setAttribute("studentList", service.getAllStudentByDepartment(departmentId, status));
					
					List<LectureDTO> lectureList = lectureService.getAllLectureByDepartment(departmentId, lectureStatus);
					Map<Long, Integer> enrollCountMap = lectureService.getEnrollCountByLectureId(lectureList);
					
					request.setAttribute("lectureList", lectureList);
					request.setAttribute("enrollCountMap", enrollCountMap);
					
					
					int capacitySum = 0;
		            int currentSum = 0;
		            for (var l : lectureList) {
		                capacitySum += l.getCapacity();
		                Integer cur = enrollCountMap.get(l.getLectureId());
		                currentSum += (cur == null ? 0 : cur);
		            }
		            request.setAttribute("capacitySum", capacitySum);
		            request.setAttribute("currentSum", currentSum);
					
					
				} catch (NumberFormatException ignore) {
					//TODO 숫자만 있는 셀렉트 문에서 값 받는 거라 int외의 변수가 들어올 일 없어서 이것도 괜찮습니다
				}
			}

			break;
		
		case "/calendarEdit":
			
			List<CodeOptionDTO> codeOptions = Arrays.stream(ScheduleCode.values())
												.map(c -> new CodeOptionDTO(c.name(), c.getLabel()))
												.toList();
			request.setAttribute("codeOptions", codeOptions);
			
			String mode = request.getParameter("action");
			String schId = request.getParameter("id");
			if("EDIT".equalsIgnoreCase(mode)) {
				if (schId == null || schId.isBlank()) {
					System.out.println("일정 id가 없습니다.");
				} else {
					
					try {
						
						long scheduleId = Long.parseLong(schId);
						SchoolScheduleDTO event = SchoolScheduleDAO.getInstance().findById(scheduleId);
						if (event == null) {
							System.out.println("존재하지 않는 일정입니다.");
						} else {
							request.setAttribute("event", event);
							SchoolScheduleDAO.getInstance().scheduleDelete(scheduleId);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("calendarEdit doGet 에러남");
					}
					
					
				}
			}
			
			
			contentPage = "/WEB-INF/views/admin/adminCalendarEdit.jsp";
			break;
			
		case "/campus":
			contentPage = "/WEB-INF/views/admin/adminCampusMap.jsp";
			break;
			
		case "/updateStudent":
			contentPage = "/WEB-INF/views/admin/adminUpdateStudent.jsp";
			request.setAttribute("loginId", request.getAttribute("loginId"));

			String rid = request.getParameter("requestId");
		    if (rid == null) {
		        response.sendRedirect(contextPath + "/admin/studentInfoUpdateRequests");
		        return;
		    }

		    Long requestId;
		    try {
		        requestId = Long.parseLong(rid.trim());
		    } catch (NumberFormatException e) {
		        response.sendRedirect(contextPath + "/admin/studentInfoUpdateRequests");
		        return;
		    }

		    Map<String, Object> detail = service.getStudentInfoUpdateRequestDetail(requestId);

		    request.setAttribute("req", detail.get("req"));
		    request.setAttribute("currentUser", detail.get("currentUser"));
		    request.setAttribute("currentStudent", detail.get("currentStudent"));
		    request.setAttribute("currentDept", detail.get("currentDept"));
		    request.setAttribute("filesByType", detail.get("filesByType"));
		    request.setAttribute("departments", service.getDepartmentList());
			break;
			
		case "/studentInfoUpdateRequests":
			contentPage = "/WEB-INF/views/admin/studentInfoUpdateRequests.jsp";
			
		    List<StudentInfoUpdateRequestDTO> list = service.getStudentInfoUpdateRequests();
		    request.setAttribute("requestList", list);
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
				break;
			}
			
			case "/calendarEdit": {
				if("CREATE".equals(action)||"EDIT".equals(action)) {
				try {
					
	                String title = request.getParameter("title");
	                String startDateParam = request.getParameter("startDate");
	                String endDateParam = request.getParameter("endDate");
	                String memo = request.getParameter("memo");
	                String scheduleCodeParam = request.getParameter("scheduleCode");
	                
	                if (title == null || title.isBlank()
	                        || startDateParam == null || startDateParam.isBlank()
	                        || scheduleCodeParam == null || scheduleCodeParam.isBlank()) {
	                    throw new IllegalArgumentException("필수값이 누락되었습니다.");
	                }
	                
	                LocalDate startDate = LocalDate.parse(startDateParam);
	                LocalDate endDate = (endDateParam == null||endDateParam.isBlank())
	                        ? startDate
	                        : LocalDate.parse(endDateParam);
	                ScheduleCode scheduleCode = ScheduleCode.valueOf(scheduleCodeParam);

	                SchoolScheduleDTO dto = new SchoolScheduleDTO();
	                dto.setTitle(title.trim());
	                dto.setStartDate(startDate);
	                dto.setEndDate(endDate);
	                dto.setMemo(memo);
	                dto.setScheduleCode(scheduleCode);
	                
	                if("CREATE".equalsIgnoreCase(action)) {
	                	SchoolScheduleDAO.getInstance().scheduleAdd(dto);
	                } else if ("EDIT".equalsIgnoreCase(action)) {
	                	String idParam = request.getParameter("id");
	                	if (idParam == null || idParam.isBlank()) {
	                		System.out.println("일정 id가 없습니다.");
	                	}
	                	dto.setId(Long.parseLong(idParam));
	                	SchoolScheduleDAO.getInstance().scheduleUpdate(dto);
	                	
	                }
	                
	                response.sendRedirect(contextPath + "/calendar/view");
	                break;
					
				} catch (Exception e) {
					//TODO 잘못된 값이 들어오면 에러날 수 있긴 한데 셀렉트로 받아오는 거라 그럴 일은 없을 겁니다
					e.printStackTrace();
					System.out.println("calendarEdit doPost에서 에러남");
					break;
				}
				
			}
			
        if("DELETE".equalsIgnoreCase(action)) {
          try {
            long id = Long.parseLong(request.getParameter("id"));
            SchoolScheduleDAO.getInstance().scheduleDelete(id);
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println("calendarEdit doPost delete 에러남");
          }

          response.sendRedirect(contextPath+"/calendar/view");
          return;

        }
				
			}
			
			
			case "/check-email": {
				// 1. 요청 파라미터
		        String email = request.getParameter("email");

		        // 2. 기본 유효성 검사
		        // input타입 지정이라 불필요?
		        // 이메일 형식도 input type=email로 되어있어 통과 불가?
		        if (email == null || email.trim().isEmpty()) {
		            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		            response.getWriter().write("{\"error\":\"email is required\"}");
		            return;
		        }

		        // 3. 중복 여부 확인
		        LoginService ls = LoginService.getInstance();
		        boolean isDuplicate = ls.DuplicateEmail(email);

		        // 4. 응답 타입 설정 (중요)
		        response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");

		        // 5. JSON 응답
		        String json = "{\"duplicate\":" + isDuplicate + "}";
		        response.getWriter().write(json);
				break;
			}
			case "/check-loginId": {
				// 1. 요청 파라미터
		        String loginId = request.getParameter("loginId");

		        // 2. 기본 유효성 검사
		        // input타입 지정이라 불필요?
		        // 이메일 형식도 input type=email로 되어있어 통과 불가?
		        if (loginId == null || loginId.trim().isEmpty()) {
		            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		            response.getWriter().write("{\"error\":\"loginId is required\"}");
		            return;
		        }

		        // 3. 중복 여부 확인
		        LoginService ls = LoginService.getInstance();
		        boolean isDuplicate = ls.DuplicateLoginId(loginId);

		        // 4. 응답 타입 설정 (중요)
		        response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");

		        // 5. JSON 응답
		        String json = "{\"duplicate\":" + isDuplicate + "}";
		        response.getWriter().write(json);
				break;
			}
			
			case "/updateStudentProcess": {
			    Long requestId = Long.parseLong(request.getParameter("requestId"));
			    Long studentId = Long.parseLong(request.getParameter("studentId"));

			    service.applyStudentInfoUpdate(requestId, studentId, request);

			    response.sendRedirect(contextPath + "/admin/studentInfoUpdateRequests");
			    return;
			}
			
			default: break;
		}
		
		
	}

}
