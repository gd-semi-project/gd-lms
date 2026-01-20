package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.UserDTO;
import model.enumtype.Role;
import service.LectureAccessService;
import service.QnaService;
import service.exception.AccessDeniedException;

@WebServlet("/lecture/qna")
public class QnaController extends HttpServlet {

    private final LectureAccessService accessService = new LectureAccessService();
    private final QnaService qnaService = new QnaService(); // 추후 구현

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 로그인 체크 (세션 키는 팀 표준으로 통일 권장: "UserInfo")
        HttpSession session = request.getSession(false);
        UserDTO user = (session == null) ? null : (UserDTO) session.getAttribute("UserInfo");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2) lectureId 파라미터 검증 (DB: BIGINT -> long)
        long lectureId = parseLong(request.getParameter("lectureId"));
        if (lectureId <= 0) {
            response.sendRedirect(request.getContextPath() + "/lecture/list?error=invalidLectureId");
            return;
        }

        try {
            // 3) 권한 체크 (boolean 분기 제거 → 예외 가드)
            long userId = user.getUserId();
            Role role = user.getRole(); // enum

            accessService.assertCanAccessLecture(userId, lectureId, role);

            // 4) (추후) Q&A 목록 조회
            // List<QnaDTO> qnaList = qnaService.getQnaListByLecture(lectureId, userId, role, ...);
            // request.setAttribute("qnaList", qnaList);

            request.setAttribute("lectureId", lectureId);
            request.setAttribute("activeTab", "qna");

            // 5) 뷰 이동
            request.getRequestDispatcher("/WEB-INF/views/lecture/qna.jsp")
                   .forward(request, response);

        } catch (AccessDeniedException e) {
            // 권한 없음
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/accessDenied.jsp")
                   .forward(request, response);
        }
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); }
        catch (Exception e) { return 0L; }
    }
}
