package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.LectureDTO;
import model.dto.QnaPostDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.LectureService;
import service.QnaService;

@WebServlet("/lecture/qna")
public class QnaController extends HttpServlet {

    private final QnaService qnaService = new QnaService();
    private final LectureService lectureService = LectureService.getInstance();

    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();

//        // 1) 로그인 체크
//        HttpSession session = request.getSession(false);
//        UserDTO user = (session == null) ? null : (UserDTO) session.getAttribute("UserInfo");
//        if (user == null) {
//            response.sendRedirect(ctx + "/login");
//            return;
//        }
     // 1) 로그인 체크 임시용
        HttpSession session = request.getSession(true); // false -> true 로
        UserDTO user = (UserDTO) session.getAttribute("UserInfo");

        if (user == null) {
            // ===== 개발/테스트용 임시 로그인 주입 =====
            user = new UserDTO();
            user.setUserId(1L);              // DB에 존재하는 user_id로
            user.setRole(Role.ADMIN);        // ADMIN / INSTRUCTOR / STUDENT 중 선택
            session.setAttribute("UserInfo", user);
            // ======================================
        }

        // 2) 파라미터
        long lectureId = parseLong(request.getParameter("lectureId"));
        if (lectureId <= 0) {
            response.sendRedirect(ctx + "/lecture/list?error=invalidLectureId");
            return;
        }

        int page = parseInt(request.getParameter("page"), 1);
        int size = parseInt(request.getParameter("size"), DEFAULT_SIZE);
        if (size > MAX_SIZE) size = MAX_SIZE;
        if (size <= 0) size = DEFAULT_SIZE;
        if (page <= 0) page = 1;

        int offset = (page - 1) * size;

        long userId = user.getUserId();
        Role role = user.getRole();

        try {
            // 3) lecture 객체 세팅 (탭에서 lecture.lectureId 사용하므로 필수)
            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 4) 목록 조회(서비스 내부 권한 체크)
            int totalCount = qnaService.countByLecture(lectureId, userId, role);
            List<QnaPostDTO> list = qnaService.listByLecture(lectureId, size, offset, userId, role);
            int totalPages = (int) Math.ceil(totalCount / (double) size);

            // 5) request 바인딩
            request.setAttribute("lecture", lecture);     // ★ 탭용
            request.setAttribute("lectureId", lectureId); // ★ 화면용
            request.setAttribute("activeTab", "qna");

            request.setAttribute("qnaList", list);
            request.setAttribute("page", page);
            request.setAttribute("size", size);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("totalPages", totalPages);

            // 6) layout 패턴으로 이동
            request.setAttribute("contentPage", "/WEB-INF/views/lecture/qna/list.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (QnaService.AccessDeniedException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/accessDenied.jsp")
                   .forward(request, response);

        } catch (QnaService.NotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/notFound.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Q&A 목록 조회 중 오류가 발생했습니다.");
            request.setAttribute("exception", e);
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp")
                   .forward(request, response);
        }
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
