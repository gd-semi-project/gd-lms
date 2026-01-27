package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.MyLectureDTO;
import model.enumtype.Role;
import service.LectureService;

@WebServlet("/student/*")
public class StudentController extends HttpServlet {

    private final LectureService lectureService =
        LectureService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);

        // 로그인 권한 체크
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO access =
            (AccessDTO) session.getAttribute("AccessInfo");

        if (access == null || access.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long userId = access.getUserId();
        if (userId == null) {
            session.invalidate();
            session = request.getSession();
            session.setAttribute("errorMessage", "세션 정보가 유효하지 않습니다.");
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;
        }

        /* ======================
         *  URL 분기
         * ====================== */
        String uri = request.getRequestURI();
        String action =
            uri.substring(ctx.length() + "/student".length());

        // 기본 페이지 → 내 강의 목록
        if (action == null || action.isBlank()) {
            action = "/lectures";
        }

        switch (action) {

        // 학생 내 강의 목록
        case "/lectures": {

            List<LectureDTO> lectures =
                lectureService.getMyLectures(access, null);

            request.setAttribute("lectures", lectures);
            request.setAttribute("status", "ONGOING");
            request.setAttribute("activeMenu", "lectures");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/lectureList.jsp"
            );
            break;
        }
        // 학생 내 종강한 강의 목록 
        case "/lectures/ended" : {
        	List<MyLectureDTO> lectures =
        	        lectureService.getMyEndedLectures(
        	            access.getUserId()
        	        );
        		request.setAttribute("status", "ENDED");
        	    request.setAttribute("lectures", lectures);
        	    request.setAttribute("activeMenu", "lectures");
        	    request.setAttribute(
        	        "contentPage",
        	        "/WEB-INF/views/lecture/lectureList.jsp"
        	    );
        	    break;
        }
        default:
        	 session.setAttribute("errorMessage", "존재하지 않는 요청입니다.");
        	    response.sendRedirect(ctx + "/error?errorCode=404");
        	    return;
        }

        request.getRequestDispatcher(
            "/WEB-INF/views/layout/layout.jsp"
        ).forward(request, response);
    }
}