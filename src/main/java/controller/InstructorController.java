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
import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.SchoolScheduleDTO;
import model.enumtype.Role;
import service.InstructorService;
import service.LectureRequestService;
import service.LectureService;

@WebServlet("/instructor/*")
public class InstructorController extends HttpServlet {

    private InstructorService instructorService =
        InstructorService.getInstance();
    private LectureService lectureService =
        LectureService.getInstance();
    private LectureRequestService lectureRequestService =
        LectureRequestService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String ctx = request.getContextPath();

        // 로그인 체크
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO access =
            (AccessDTO) session.getAttribute("AccessInfo");

        if (access == null || access.getRole() == Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long instructorId = access.getUserId();

        String uri = request.getRequestURI();
        String action =
            uri.substring(ctx.length() + "/instructor".length());

        if (action.isEmpty())
            action = "/lectures";

        switch (action) {

        // 강사 프로필
        case "/profile": {
        	
        	String userId = request.getParameter("userId");
        	if (userId != null) {
        		Long userID = Long.parseLong(userId);
                Map<String, Object> profile =
                        instructorService.getInstructorProfile(userID);
                
                request.setAttribute("instructor", profile.get("instructor"));
                request.setAttribute("user", profile.get("user"));
                request.setAttribute(
                		"contentPage",
                		"/WEB-INF/views/instructor/profile.jsp"
                		);
                userId = null;
                break;
        	};
        	
        	
        	
            Map<String, Object> profile =
                instructorService.getInstructorProfile(access.getUserId());

            request.setAttribute("instructor", profile.get("instructor"));
            request.setAttribute("user", profile.get("user"));
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/instructor/profile.jsp"
            );
            break;
        }

        // 내 강의 목록
        case "/lectures": {
          String status = request.getParameter("status");
          if (status == null || status.isBlank()) status = "ONGOING";


          List<LectureDTO> lectures = lectureService.getMyLectures(access, status);

          request.setAttribute("lectures", lectures);
          request.setAttribute("activeMenu", "lectures");
          request.setAttribute("contentPage", "/WEB-INF/views/lecture/lectureList.jsp");
          break;
        }

        // 강의 개설 신청 목록
        case "/lecture/request": {

            boolean isOpen =
                lectureRequestService.isLectureRequestPeriod();

            request.setAttribute(
                "requests",
                lectureRequestService.getMyLectureRequests(instructorId)
            );
            request.setAttribute("isLectureRequestOpen", isOpen);

            if (!isOpen) {
                SchoolScheduleDTO period =
                    lectureRequestService.getNearestLectureRequestPeriod();

                if (period != null) {
                    request.setAttribute(
                        "errorMessage",
                        "현재는 강의 개설 신청 기간이 아닙니다."
                    );
                    request.setAttribute(
                        "requestStartDate",
                        period.getStartDate()
                    );
                    request.setAttribute(
                        "requestEndDate",
                        period.getEndDate()
                    );
                }
            }

            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/requestList.jsp"
            );
            break;
        }
        

     // 신규 신청 폼
        case "/lecture/request/new": {

            boolean isOpen =
                lectureRequestService.isLectureRequestPeriod();

            if (!isOpen) {
                // 직접 접근 차단
                response.sendRedirect(
                    ctx + "/instructor/lecture/request"
                );
                return;
            }

            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/requestForm.jsp"
            );
            break;
        }

        // 수정 폼
        case "/lecture/request/edit": {

            boolean isOpen =
                lectureRequestService.isLectureRequestPeriod();

            if (!isOpen) {
                response.sendRedirect(ctx + "/instructor/lecture/request");
                return;
            }

            Long lectureId =
                Long.parseLong(request.getParameter("lectureId"));

            request.setAttribute(
                "lecture",
                lectureRequestService.getLectureRequestDetail(lectureId)
            );
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/requestEditForm.jsp"
            );
            break;
        }

        default:
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.getRequestDispatcher(
            "/WEB-INF/views/layout/layout.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);
        AccessDTO access =
            (AccessDTO) session.getAttribute("AccessInfo");

        if (access == null || access.getRole() != Role.INSTRUCTOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long instructorId = access.getUserId();

        try {
            // 신규 신청
            if (uri.endsWith("/lecture/request")) {
                lectureRequestService.createLectureRequest(
                    instructorId, request
                );
                response.sendRedirect(
                    ctx + "/instructor/lecture/request?success=created"
                );
                return;
            }

            // 수정
            if (uri.endsWith("/lecture/request/edit")) {
                Long lectureId =
                    Long.parseLong(request.getParameter("lectureId"));

                lectureRequestService.updateLectureRequest(
                    lectureId, request
                );
                response.sendRedirect(
                    ctx + "/instructor/lecture/request"
                );
                return;
            }

            // 삭제
            if (uri.endsWith("/lecture/request/delete")) {
                Long lectureId =
                    Long.parseLong(request.getParameter("lectureId"));

                lectureRequestService.deleteLectureRequest(lectureId);

                response.sendRedirect(
                    ctx + "/instructor/lecture/request?success=deleted"
                );
                return;
            }

        } catch (IllegalArgumentException e) {

            request.setAttribute("errorMessage", e.getMessage());

            if (uri.endsWith("/lecture/request/edit")) {
                request.setAttribute(
                    "contentPage",
                    "/WEB-INF/views/lecture/requestEditForm.jsp"
                );
            } else {
                request.setAttribute(
                    "contentPage",
                    "/WEB-INF/views/lecture/requestForm.jsp"
                );
            }

            request.getRequestDispatcher(
                "/WEB-INF/views/layout/layout.jsp"
            ).forward(request, response);
        }
    }
    
}