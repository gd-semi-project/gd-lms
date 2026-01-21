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
import model.enumtype.Role;
import service.InstructorService;
import service.LectureRequestService;
import service.LectureService;

@WebServlet("/instructor/*")
public class InstructorController extends HttpServlet {

    private InstructorService instructorService = InstructorService.getInstance();
    private LectureService lectureService = LectureService.getInstance();
    private LectureRequestService lectureRequestService = LectureRequestService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();

        // 로그인 체크
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO accessInfo = (AccessDTO) session.getAttribute("AccessInfo");
        if (accessInfo == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        // 권한 체크
        if (accessInfo.getRole() != Role.INSTRUCTOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        long instructorId = accessInfo.getUserId();

        String uri = request.getRequestURI();
        String action = uri.substring(ctx.length() + "/instructor".length());

        if (action.isEmpty()) action = "/lectures";

        switch (action) {

        // 강사 프로필
        case "/profile": {
            Map<String, Object> profile = instructorService.getInstructorProfile(instructorId);

            request.setAttribute("instructor", profile.get("instructor"));
            request.setAttribute("user", profile.get("user"));
            request.setAttribute("contentPage", "/WEB-INF/views/instructor/profile.jsp");
            break;
        }

        // 내 강의 목록
        case "/lectures": {
            List<LectureDTO> lectures = lectureService.getLecturesByInstructor(instructorId);

            request.setAttribute("lectures", lectures);
            request.setAttribute("contentPage", "/WEB-INF/views/lecture/lectureList.jsp");
            break;
        }

        case "/lecture/request": {
            List<LectureRequestDTO> requests = lectureRequestService.getMyLectureRequests(instructorId);

            request.setAttribute("requests", requests);
            request.setAttribute("contentPage", "/WEB-INF/views/lecture/requestList.jsp");
            break;
        }

        case "/lecture/request/new": {
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/requestForm.jsp"
            );
            break;
        }

        case "/lecture/request/edit": {
            Long lectureId = Long.parseLong(request.getParameter("lectureId"));

            LectureRequestDTO dto =
                lectureRequestService.getLectureRequestDetail(lectureId);

            request.setAttribute("request", dto);
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

        request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        // 로그인 체크
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO accessInfo = (AccessDTO) session.getAttribute("AccessInfo");
        if (accessInfo == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        long instructorId = accessInfo.getUserId();

        // 신규 신청
        if (uri.endsWith("/lecture/request")) {
            lectureRequestService.createLectureRequest(instructorId, request);
            response.sendRedirect(ctx + "/instructor/lecture/request");
            return;
        }

        // 수정
        if (uri.endsWith("/lecture/request/edit")) {
            Long lectureId = Long.parseLong(request.getParameter("lectureId"));
            lectureRequestService.updateLectureRequest(lectureId, request);
            response.sendRedirect(ctx + "/instructor/lecture/request");
            return;
        }

        // 삭제
        if (uri.endsWith("/lecture/request/delete")) {
            Long lectureId = Long.parseLong(request.getParameter("lectureId"));
            lectureRequestService.deleteLectureRequest(lectureId);
            response.sendRedirect(ctx + "/instructor/lecture/request");
        }
    }
}