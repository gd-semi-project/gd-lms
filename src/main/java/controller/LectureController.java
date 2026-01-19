package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.LectureDTO;
import model.dto.LectureStudentDTO;
import service.LectureService;

@WebServlet("/lecture/*")
public class LectureController extends HttpServlet {

    private final LectureService lectureService = LectureService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();
        String action = uri.substring(ctx.length() + "/lecture".length());

        if (action == null || action.isBlank()) action = "/detail";

        HttpSession session = request.getSession(false);

        // 로그인 체크
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        switch (action) {

        // 강의 상세
        case "/detail": {
            Long lectureId = parseLong(request.getParameter("lectureId"));
            if (lectureId == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("lecture", lecture);
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required");
                return;
            }

            List<LectureStudentDTO> students =
                lectureService.getLectureStudents(lectureId);

            request.setAttribute("students", students);
            request.setAttribute("lectureId", lectureId);
            request.setAttribute("activeTab", "students");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/students.jsp"
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

    private Long parseLong(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}