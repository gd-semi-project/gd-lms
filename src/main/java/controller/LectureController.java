package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.LectureDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.LectureService;

@WebServlet("/lecture/*")
public class LectureController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LectureService lectureService = LectureService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String action = uri.substring(ctx.length() + "/lecture".length());
        if (action.isEmpty()) action = "/detail";

        /* =======================
         * 🔥 테스트용 세션 주입 (강사)
         * ======================= */
        HttpSession session = request.getSession(true);

        if (session.getAttribute("UserInfo") == null) {
            UserDTO testUser = new UserDTO();
            testUser.setUser_id(1L);            // 김도윤 강사
            testUser.setLogin_id("inst_kim");
            testUser.setName("김도윤");
            testUser.setRole(Role.INSTRUCTOR); // ⭐ 강사 권한

            session.setAttribute("UserInfo", testUser);
            System.out.println("🔥 LectureController 테스트용 강사 세션 주입 완료");
        }

        UserDTO loginUser = (UserDTO) session.getAttribute("UserInfo");
        
        switch (action) {

        //강의 상세
        case "/detail": {
            long lectureId = Long.parseLong(request.getParameter("id"));

            LectureDTO lecture = lectureService.getLectureDetail(lectureId);

            request.setAttribute("lecture", lecture);
            request.setAttribute(
                "schedules",
                lectureService.getLectureSchedules(lectureId)
            );
            request.setAttribute("activeTab", "detail");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/detail.jsp"
            );
            break;
        }

        // 출석
        case "/attendance": {
            long lectureId = Long.parseLong(request.getParameter("id"));

            LectureDTO lecture = lectureService.getLectureDetail(lectureId);

            request.setAttribute("lecture", lecture);
            request.setAttribute("activeTab", "attendance");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/attendance.jsp"
            );
            break;
        }

        // 성적
        case "/grades": {
            request.setAttribute("activeTab", "grades");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/grades.jsp"
            );
            break;
        }

        /* =======================
         * 과제
         * ======================= */
        case "/assignments": {
            request.setAttribute("activeTab", "assignments");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/assignments.jsp"
            );
            break;
        }

        /* =======================
         * QnA
         * ======================= */
        case "/qna": {
            request.setAttribute("activeTab", "qna");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/qna.jsp"
            );
            break;
        }

        // 수강생 정보
        case "/students": {

            if (loginUser.getRole() == Role.STUDENT) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            long lectureId = Long.parseLong(request.getParameter("id"));
            
            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            request.setAttribute("lecture", lecture);

            request.setAttribute(
                "students",
                lectureService.getLectureStudents(lectureId)
            );
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

        /* =======================
         * 공통 레이아웃 forward
         * ======================= */
        request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
               .forward(request, response);
    }
}