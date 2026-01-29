package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import exception.AccessDeniedException;
import exception.BadRequestException;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.SchoolScheduleDTO;
import model.enumtype.Role;
import service.InstructorService;
import service.LectureAccessService;
import service.LectureRequestService;
import service.LectureService;

@WebServlet("/instructor/*")
public class InstructorController extends HttpServlet {

    private final InstructorService instructorService = InstructorService.getInstance();
    private final LectureService lectureService = LectureService.getInstance();
    private final LectureRequestService lectureRequestService = LectureRequestService.getInstance();
    private final LectureAccessService lectureAccessService = new LectureAccessService();
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String ctx = request.getContextPath();
        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

        Long instructorId = access.getUserId();

        String uri = request.getRequestURI();
        String action = uri.substring(ctx.length() + "/instructor".length());

        if (action.isEmpty()) action = "/lectures";

        try {

            switch (action) {

            case "/profile": {
                String userIdParam = request.getParameter("userId");
                Long targetUserId = (userIdParam != null)
                        ? Long.parseLong(userIdParam)
                        : access.getUserId();

                Map<String, Object> profile =
                        instructorService.getInstructorProfile(targetUserId);

                request.setAttribute("instructor", profile.get("instructor"));
                request.setAttribute("user", profile.get("user"));
                request.setAttribute("contentPage",
                        "/WEB-INF/views/instructor/profile.jsp");
                break;
            }
            
            case "/profile/edit": {

                Map<String, Object> profile =
                    instructorService.getInstructorProfile(access.getUserId());

                request.setAttribute("instructor", profile.get("instructor"));
                request.setAttribute("user", profile.get("user"));
                request.setAttribute("contentPage",
                    "/WEB-INF/views/instructor/profileEdit.jsp");

                break;
            }

            case "/lectures": {
                String status = request.getParameter("status");
                if (status == null || status.isBlank()) status = "ONGOING";

                List<LectureDTO> lectures =
                        lectureService.getMyLectures(access, status);

                request.setAttribute("lectures", lectures);
                request.setAttribute("activeMenu", "lectures");
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/lectureList.jsp");
                break;
            }

            case "/lecture/request": {

                boolean isOpen =
                        lectureRequestService.isLectureRequestPeriod();

                request.setAttribute("requests",
                        lectureRequestService.getMyLectureRequests(instructorId));
                request.setAttribute("isLectureRequestOpen", isOpen);

                if (!isOpen) {
                	request.setAttribute("errorMessage",
                            "현재는 강의 개설 신청 기간이 아닙니다.");
                	
                        SchoolScheduleDTO period =
                            lectureRequestService.getNearestLectureRequestPeriod();

                        if (period != null) {
                            request.setAttribute("requestStartDate", period.getStartDate());
                            request.setAttribute("requestEndDate", period.getEndDate());
                        }
                }

                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/requestList.jsp");
                break;
            }
            
            case "/lecture/request/detail": {

                String lectureIdParam = request.getParameter("lectureId");
                if (lectureIdParam == null || lectureIdParam.isBlank()) {
                    throw new BadRequestException("lectureId가 필요합니다.");
                }

                Long lectureId;
                try {
                    lectureId = Long.parseLong(lectureIdParam);
                } catch (NumberFormatException e) {
                    throw new BadRequestException("lectureId 형식이 올바르지 않습니다.");
                }

                lectureAccessService.assertCanAccessLecture(
                    instructorId, lectureId, Role.INSTRUCTOR
                );

                LectureRequestDTO lecture = lectureRequestService.getLectureRequestDetail(lectureId);
                if (lecture == null) {
                	throw new ResourceNotFoundException("존재하지 않는 강의 신청입니다.");
                }

                request.setAttribute("lecture", lecture);
                request.setAttribute("isRequest", true);
                request.setAttribute("activeTab", "detail");

                request.setAttribute("schedules",
                    lectureRequestService.getLectureSchedules(lectureId));

                request.setAttribute("scorePolicy",
                    lectureRequestService.getScorePolicy(lectureId));

                Map<String, Object> profile =
                	    instructorService.getInstructorProfile(instructorId);

                request.setAttribute("instructor", profile.get("instructor"));
                request.setAttribute("user", profile.get("user"));

                request.setAttribute("contentPage",
                    "/WEB-INF/views/lecture/detail.jsp");
                break;
            }

            case "/lecture/request/new": {

                if (!lectureRequestService.isLectureRequestPeriod()) {

                    SchoolScheduleDTO period =
                            lectureRequestService.getNearestLectureRequestPeriod();

                    request.setAttribute("errorMessage",
                            "현재는 강의 개설 신청 기간이 아닙니다.");

                    if (period != null) {
                        request.setAttribute("requestStartDate", period.getStartDate());
                        request.setAttribute("requestEndDate", period.getEndDate());
                    }

                    request.setAttribute("contentPage",
                            "/WEB-INF/views/lecture/requestList.jsp");

                    request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                           .forward(request, response);
                    return;
                }

                request.setAttribute("rooms",
                        lectureRequestService.getAllRooms());
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/requestForm.jsp");
                break;
            }

            case "/lecture/request/edit": {

                String lectureIdParam = request.getParameter("lectureId");
                if (lectureIdParam == null) {
                    throw new BadRequestException("lectureId가 필요합니다.");
                }

                Long lectureId;
                try {
                    lectureId = Long.parseLong(lectureIdParam);
                } catch (NumberFormatException e) {
                    throw new BadRequestException("lectureId 형식이 올바르지 않습니다.");
                }

                lectureAccessService.assertCanAccessLecture(
                        instructorId, lectureId, Role.INSTRUCTOR
                );

                LectureRequestDTO lecture =
                        lectureRequestService.getLectureRequestDetail(lectureId);

                request.setAttribute("rooms",
                        lectureRequestService.getAllRooms());
                request.setAttribute("lecture", lecture);
                request.setAttribute("scorePolicy",
                        lectureRequestService.getScorePolicy(lectureId));
                request.setAttribute("schedules",
                        lectureRequestService.getLectureSchedules(lectureId));
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/requestEditForm.jsp");
                break;
            }

            default:
                throw new ResourceNotFoundException("요청하신 페이지를 찾을 수 없습니다.");
            }

            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (BadRequestException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("contentPage",
                    "/WEB-INF/views/error/400.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (ResourceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (InternalServerException e) {
            throw e;
        }
    }

    // ================= POST =================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);
        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");

        Long instructorId = access.getUserId();

        try {
        	
        	if (uri.endsWith("/profile/edit")) {

        	    Long userId = Long.parseLong(request.getParameter("userId"));

        	    if (!userId.equals(access.getUserId())) {
        	        throw new AccessDeniedException("본인 정보만 수정할 수 있습니다.");
        	    }

        	    String name = request.getParameter("name");
        	    String email = request.getParameter("email");
        	    String phone = request.getParameter("phone");
        	    String officeRoom = request.getParameter("officeRoom");
        	    String officePhone = request.getParameter("officePhone");

        	    instructorService.updateInstructorProfile(
        	        userId,
        	        name,
        	        email,
        	        phone,
        	        officeRoom,
        	        officePhone
        	    );

        	    response.sendRedirect(ctx + "/instructor/profile?success=updated");
        	    return;
        	}

        	if (uri.endsWith("/lecture/request")) {
        		
        		String[] weekDays = request.getParameterValues("weekDay");
        	    if (weekDays == null || weekDays.length == 0) {
        	        throw new BadRequestException("요일을 최소 1개 이상 선택해야 합니다.");
        	    }
        		
        		validateScoreWeight(request);
        	    lectureRequestService.createLectureRequest(instructorId, request);

        	    request.getSession().setAttribute("flashMessage", "created");
        	    response.sendRedirect(ctx + "/instructor/lecture/request");
        	    return;
        	}

        	if (uri.endsWith("/lecture/request/edit")) {

        	    Long lectureId = Long.parseLong(request.getParameter("lectureId"));

        	    lectureAccessService.assertCanAccessLecture(
        	            instructorId, lectureId, Role.INSTRUCTOR
        	    );
        	    
        	    validateScoreWeight(request);

        	    try {
        	        lectureRequestService.updateLectureRequest(lectureId, request);

        	        request.getSession().setAttribute("flashMessage", "updated");
        	        response.sendRedirect(ctx + "/instructor/lecture/request");
        	        return;

        	    } catch (Exception e) {

        	        request.getSession().setAttribute("flashMessage", "failed");
        	        response.sendRedirect(ctx + "/instructor/lecture/request");
        	        return;
        	    }
        	}

        	if (uri.endsWith("/lecture/request/delete")) {

        	    Long lectureId = Long.parseLong(request.getParameter("lectureId"));

        	    lectureAccessService.assertCanAccessLecture(
        	            instructorId, lectureId, Role.INSTRUCTOR
        	    );

        	    lectureRequestService.deleteLectureRequest(lectureId);

        	    request.getSession().setAttribute("flashMessage", "deleted");
        	    response.sendRedirect(ctx + "/instructor/lecture/request");
        	    return;
        	}

            throw new ResourceNotFoundException("요청하신 작업을 처리할 수 없습니다.");

        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (BadRequestException e) {

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("rooms", lectureRequestService.getAllRooms());

            if (uri.endsWith("/lecture/request/edit")) {

                Long lectureId = Long.parseLong(request.getParameter("lectureId"));
                request.setAttribute("lecture",
                        lectureRequestService.getLectureRequestDetail(lectureId));
                request.setAttribute("scorePolicy",
                        lectureRequestService.getScorePolicy(lectureId));
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/requestEditForm.jsp");

            } else {
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/requestForm.jsp");
            }

            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (InternalServerException e) {
            throw e;
        }
    }
    
    
    private void validateScoreWeight(HttpServletRequest request) {

        int attendance = Integer.parseInt(request.getParameter("attendanceWeight"));
        int assignment = Integer.parseInt(request.getParameter("assignmentWeight"));
        int midterm = Integer.parseInt(request.getParameter("midtermWeight"));
        int fin = Integer.parseInt(request.getParameter("finalWeight"));

        int total = attendance + assignment + midterm + fin;

        if (total != 100) {
            throw new BadRequestException("성적 배점의 합은 반드시 100%여야 합니다. (현재: " + total + "%)");
        }
    }
}