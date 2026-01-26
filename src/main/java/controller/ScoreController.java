package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.ScoreDTO;
import model.enumtype.Role;
import service.LectureService;
import service.ScoreService;

@WebServlet("/score/*")
public class ScoreController extends HttpServlet {

    private final ScoreService scoreService =
            ScoreService.getInstance();
    private final LectureService lectureService =
            LectureService.getInstance();

    /* ==================================================
     * GET - 성적 페이지
     * ================================================== */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();
        String action =
                uri.substring(ctx.length() + "/score".length());

        if (action == null || action.isBlank()) {
            action = "/grades";
        }

        /* 로그인 체크 */
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO access =
                (AccessDTO) session.getAttribute("AccessInfo");
        if (access == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        Role role = access.getRole();

        switch (action) {

        /* =========================
         * 성적 조회
         * ========================= */
        case "/grades": {

            Long lectureId =
                    parseLong(request.getParameter("lectureId"));
            if (lectureId == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            LectureDTO lecture =
                    lectureService.getLectureDetail(lectureId);

            request.setAttribute("lecture", lecture);
            request.setAttribute("lectureId", lectureId);
            request.setAttribute("role", role);

            if (role == Role.INSTRUCTOR) {
                // 교수: 전체 학생
                List<ScoreDTO> scores =
                        scoreService.getScoreList(lectureId);
                request.setAttribute("scores", scores);

            } else if (role == Role.STUDENT) {
                // 🔥 학생: 본인만
                Long studentId = access.getUserId(); // or studentId 매핑

                ScoreDTO myScore =
                        scoreService.getMyScore(
                                lectureId,
                                studentId
                        );

                request.setAttribute("myScore", myScore);
            }

            request.setAttribute("activeTab", "grades");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/grades.jsp"
            );
            break;
        }

        default:
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/layout/layout.jsp"
        ).forward(request, response);
    }

    /* ==================================================
     * POST
     * ================================================== */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO access =
                (AccessDTO) session.getAttribute("AccessInfo");
        if (access == null
            || access.getRole() != Role.INSTRUCTOR) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        /* =========================
         * 성적 저장 (부분 저장 허용)
         * ========================= */
        if (uri.endsWith("/grades/save")) {

            Long lectureId =
                    Long.parseLong(
                        request.getParameter("lectureId")
                    );

            List<ScoreDTO> scoreList =
                    extractScoreList(request, lectureId);

            try {
                scoreService.saveScores(
                        lectureId,
                        scoreList
                );

            } catch (IllegalStateException e) {
                // 🔥 저장 실패 사유 사용자에게 전달
                String msg =
                        URLEncoder.encode(
                                e.getMessage(),
                                StandardCharsets.UTF_8
                        );

                response.sendRedirect(
                    ctx + "/score/grades?lectureId="
                    + lectureId
                    + "&warning="
                    + msg
                );
                return;
            }

            response.sendRedirect(
                    ctx + "/score/grades?lectureId=" + lectureId
            );
            return;
        }

        /* =========================
         * 학점 계산
         * ========================= */
        if (uri.endsWith("/grades/calculate")) {

            Long lectureId =
                    Long.parseLong(
                        request.getParameter("lectureId")
                    );

            try {
                scoreService.calculateGrade(lectureId);

            } catch (IllegalStateException e) {
                // 🔥 500 대신 경고 메시지 전달
                String msg =
                        URLEncoder.encode(
                                e.getMessage(),
                                StandardCharsets.UTF_8
                        );

                response.sendRedirect(
                    ctx + "/score/grades?lectureId="
                    + lectureId
                    + "&warning="
                    + msg
                );
                return;
            }

            response.sendRedirect(
                    ctx + "/score/grades?lectureId=" + lectureId
            );
            return;
        }

        response.sendError(
                HttpServletResponse.SC_NOT_FOUND);
    }

    /* ==================================================
     * 내부 유틸
     * ================================================== */

    private Long parseLong(String s) {
        try {
            return (s == null || s.isBlank())
                    ? null
                    : Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSP → ScoreDTO 리스트 변환
     */
    private List<ScoreDTO> extractScoreList(
            HttpServletRequest request,
            Long lectureId
    ) {

        String[] studentIds =
                request.getParameterValues("studentId");

        List<ScoreDTO> list = new ArrayList<>();
        if (studentIds == null) return list;

        for (String sid : studentIds) {

            ScoreDTO dto = new ScoreDTO();
            dto.setLectureId(lectureId);
            dto.setStudentId(Long.parseLong(sid));

            String scoreIdParam =
                    request.getParameter("scoreId_" + sid);
            if (scoreIdParam != null) {
                dto.setScoreId(
                        Long.parseLong(scoreIdParam)
                );
            }

            dto.setAssignmentScore(
                parseInteger(
                    request.getParameter("assignmentScore_" + sid)
                )
            );
            dto.setMidtermScore(
                parseInteger(
                    request.getParameter("midtermScore_" + sid)
                )
            );
            dto.setFinalScore(
                parseInteger(
                    request.getParameter("finalScore_" + sid)
                )
            );

            list.add(dto);
        }

        return list;
    }

    private Integer parseInteger(String s) {
        try {
            return (s == null || s.isBlank())
                    ? null
                    : Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}