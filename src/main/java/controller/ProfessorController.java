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
import model.dto.ProfessorDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.ProfessorService;

@WebServlet("/professor/*")
public class ProfessorController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProfessorService professorService = ProfessorService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String command = requestURI.substring(contextPath.length());
        String action = command.substring("/professor".length());

        if (action.isEmpty()) action = "/";

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("UserInfo") == null) {
            response.sendRedirect(contextPath + "/login/login.do");
            return;
        }

        UserDTO loginUser = (UserDTO) session.getAttribute("UserInfo");

        if (loginUser.getRole() != Role.TEACHER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int professorId = loginUser.getUser_id();

        switch (action) {

            // 교수 정보
            case "/profile": {
                ProfessorDTO professor =
                        professorService.getProfessorInfo(professorId);
                request.setAttribute("professor", professor);
                request.getRequestDispatcher("/jiyun/professor/profile.jsp")
                       .forward(request, response);
                return;
            }

         // 교수 담당 강의 목록
            case "/lectures": {

                List<LectureDTO> lectures =
                        professorService.getMyLectures(professorId);

                request.setAttribute("lectures", lectures);
                request.setAttribute("contentPage", "/WEB-INF/jiyun/professor/lectureListTest.jsp");

                request.getRequestDispatcher("/WEB-INF/testViewSihyeon/layoutTest.jsp")
                       .forward(request, response);
                return;
            }

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}