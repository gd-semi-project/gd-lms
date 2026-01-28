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

                // 🔐 본인 강의만 접근 가능(= lecture 테이블 기준 instructor 체크)
                lectureAccessService.assertCanAccessLecture(
                    instructorId, lectureId, Role.INSTRUCTOR
                );

                // ✅ 1) detail.jsp는 LectureDTO 기반이 더 안전함 (status/validation 포함)
                LectureDTO lecture = lectureService.getLectureDetail(lectureId);
                if (lecture == null) {
                    throw new ResourceNotFoundException("존재하지 않는 강의입니다.");
                }

                // ✅ 2) 신청 상세 화면임을 표시 (탭/버튼 분기용)
                request.setAttribute("lecture", lecture);
                request.setAttribute("isRequest", true);
                request.setAttribute("activeTab", "detail");

                // ✅ 3) detail.jsp에서 쓰는 데이터들 세팅
                request.setAttribute("schedules",
                    lectureRequestService.getLectureSchedules(lectureId));

                request.setAttribute("scorePolicy",
                    lectureRequestService.getScorePolicy(lectureId));

                // ✅ 4) 강사 프로필은 LectureDTO.getUserId()로 조회 (여긴 있음)
                Map<String, Object> profile =
                    instructorService.getInstructorProfile(lecture.getUserId());

                request.setAttribute("instructor", profile.get("instructor"));
                request.setAttribute("user", profile.get("user"));

                // ✅ 5) 기존 lecture/detail.jsp 재사용
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

                // 🔐 권한 체크 (본인 강의만 수정 가능)
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

            if (uri.endsWith("/lecture/request")) {

                lectureRequestService.createLectureRequest(instructorId, request);
                response.sendRedirect(ctx + "/instructor/lecture/request?success=created");
                return;
            }

            if (uri.endsWith("/lecture/request/edit")) {

                Long lectureId = Long.parseLong(request.getParameter("lectureId"));

                // 🔐 권한 체크
                lectureAccessService.assertCanAccessLecture(
                        instructorId, lectureId, Role.INSTRUCTOR
                );

                lectureRequestService.updateLectureRequest(lectureId, request);
                response.sendRedirect(ctx + "/instructor/lecture/request?success=updated");
                return;
            }

            if (uri.endsWith("/lecture/request/delete")) {

                Long lectureId = Long.parseLong(request.getParameter("lectureId"));

                // 🔐 권한 체크
                lectureAccessService.assertCanAccessLecture(
                        instructorId, lectureId, Role.INSTRUCTOR
                );

                lectureRequestService.deleteLectureRequest(lectureId);
                response.sendRedirect(ctx + "/instructor/lecture/request?success=deleted");
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
}