package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.QnaAnswerDTO;
import model.dto.QnaPostDTO;
import model.enumtype.isPrivate;
import service.QnaService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/qna/*")
public class QnaController extends HttpServlet {

    private final QnaService qnaService = new QnaService();

    private static final String LAYOUT_PAGE = "/WEB-INF/views/layout/layout.jsp";
    private static final String QNA_LIST = "/WEB-INF/views/qna/list.jsp";
    private static final String QNA_VIEW = "/WEB-INF/views/qna/view.jsp";
    private static final String QNA_NEW  = "/WEB-INF/views/qna/new.jsp";
    private static final String QNA_EDIT = "/WEB-INF/views/qna/edit.jsp";
    private static final String ERROR_403 = "/WEB-INF/views/error/403.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String action = resolveAction(req);

        try {
            switch (action) {
                case "/list": handleList(req, resp); break;
                case "/view": handleView(req, resp); break;
                case "/new":  showNew(req, resp); break;
                case "/edit": showEdit(req, resp); break;
                default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (QnaService.AccessDeniedException e) {
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String action = resolveAction(req);

        try {
            switch (action) {
                case "/create": handleCreate(req, resp); break;
                case "/update": handleUpdate(req, resp); break;
                case "/delete": handleDelete(req, resp); break;
                case "/answer/create": handleCreateAnswer(req, resp); break;
                default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (QnaService.AccessDeniedException e) {
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    // ====== Handlers ======
    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        if (lectureId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required.");
            return;
        }

        int page = parseInt(req.getParameter("page"), 1, 1, 1_000_000);
        int size = parseInt(req.getParameter("size"), 10, 1, 100);
        int limit = size;
        int offset = (page - 1) * size;

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        int totalCount = qnaService.countByLecture(lectureId, userId, role);
        List<QnaPostDTO> list = qnaService.listByLecture(lectureId, limit, offset, userId, role);
        int totalPages = (int)Math.ceil(totalCount / (double)size);

        req.setAttribute("lectureId", lectureId);
        req.setAttribute("qnaList", list);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("role", role);
        req.setAttribute("userId", userId);

        forwardLayout(req, resp, QNA_LIST);
    }

    private void handleView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        Long qnaId = parseLongNullable(req.getParameter("qnaId"));
        if (lectureId == null || qnaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId and qnaId are required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        QnaPostDTO post = qnaService.getPostDetail(qnaId, lectureId, userId, role);
        if (post == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<QnaAnswerDTO> answers = qnaService.getAnswers(qnaId, userId, role, lectureId);

        req.setAttribute("lectureId", lectureId);
        req.setAttribute("post", post);
        req.setAttribute("answers", answers);
        req.setAttribute("role", role);
        req.setAttribute("userId", userId);

        forwardLayout(req, resp, QNA_VIEW);
    }

    private void showNew(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        if (lectureId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required.");
            return;
        }
        req.setAttribute("lectureId", lectureId);
        forwardLayout(req, resp, QNA_NEW);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        Long qnaId = parseLongNullable(req.getParameter("qnaId"));
        if (lectureId == null || qnaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId and qnaId are required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        QnaPostDTO post = qnaService.getPostDetail(qnaId, lectureId, userId, role);
        req.setAttribute("lectureId", lectureId);
        req.setAttribute("post", post);

        forwardLayout(req, resp, QNA_EDIT);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        if (lectureId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        QnaPostDTO dto = new QnaPostDTO();
        dto.setLectureId(lectureId);
        dto.setTitle(trimToNull(req.getParameter("title")));
        dto.setContent(trimToNull(req.getParameter("content")));

        // ★ String -> enum
        dto.setIsPrivate(parseIsPrivate(req.getParameter("isPrivate")));

        long newId = qnaService.createPost(dto, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/qna/view",
                "lectureId", String.valueOf(lectureId),
                "qnaId", String.valueOf(newId)));
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        Long qnaId = parseLongNullable(req.getParameter("qnaId"));
        if (lectureId == null || qnaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId and qnaId are required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        QnaPostDTO dto = new QnaPostDTO();
        dto.setQnaId(qnaId);
        dto.setLectureId(lectureId);
        dto.setTitle(trimToNull(req.getParameter("title")));
        dto.setContent(trimToNull(req.getParameter("content")));

        // ★ String -> enum
        dto.setIsPrivate(parseIsPrivate(req.getParameter("isPrivate")));

        qnaService.updatePost(dto, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/qna/view",
                "lectureId", String.valueOf(lectureId),
                "qnaId", String.valueOf(qnaId)));
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        Long qnaId = parseLongNullable(req.getParameter("qnaId"));
        if (lectureId == null || qnaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId and qnaId are required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        qnaService.deletePost(qnaId, lectureId, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/qna/list",
                "lectureId", String.valueOf(lectureId)));
    }

    private void handleCreateAnswer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        Long qnaId = parseLongNullable(req.getParameter("qnaId"));
        if (lectureId == null || qnaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId and qnaId are required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        QnaAnswerDTO dto = new QnaAnswerDTO();
        dto.setQnaId(qnaId);
        dto.setContent(trimToNull(req.getParameter("content")));

        qnaService.addAnswer(dto, lectureId, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/qna/view",
                "lectureId", String.valueOf(lectureId),
                "qnaId", String.valueOf(qnaId)));
    }

    // ====== helpers ======
    private void forwardLayout(HttpServletRequest req, HttpServletResponse resp, String contentPage)
            throws ServletException, IOException {
        req.setAttribute("contentPage", contentPage);
        req.getRequestDispatcher(LAYOUT_PAGE).forward(req, resp);
    }

    private String resolveAction(HttpServletRequest req) {
        String path = req.getPathInfo();
        return (path == null || path.isBlank()) ? "/list" : path;
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private int parseInt(String s, int defaultVal, int min, int max) {
        try {
            int v = Integer.parseInt(s);
            if (v < min) return min;
            if (v > max) return max;
            return v;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private Long parseLongNullable(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Long getLoginUserId(HttpSession session) {
        if (session == null) return null;
        Object v = session.getAttribute("userId");
        if (v instanceof Long) return (Long) v;
        if (v instanceof String) {
            try { return Long.parseLong((String)v); } catch (Exception ignored) {}
        }
        return null;
    }

    private String getLoginRole(HttpSession session) {
        if (session == null) return null;
        Object v = session.getAttribute("role");
        return (v instanceof String) ? (String)v : null;
    }

    private String buildRedirectUrl(HttpServletRequest req, String path, String... kv) {
        StringBuilder sb = new StringBuilder();
        sb.append(req.getContextPath()).append(path);

        boolean first = true;
        for (int i = 0; i + 1 < kv.length; i += 2) {
            String key = kv[i];
            String val = kv[i + 1];
            if (key == null || val == null) continue;

            sb.append(first ? "?" : "&");
            first = false;

            sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
            sb.append("=");
            sb.append(URLEncoder.encode(val, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    // ★ 컨트롤러 입력값 -> enum 변환
    // 체크박스/라디오/셀렉트 구현에 따라 "Y"/"on"/"true"/"1" 등으로 들어올 수 있어 방어적으로 처리
    private isPrivate parseIsPrivate(String param) {
        if (param == null) return isPrivate.N;

        String v = param.trim();
        if (v.isEmpty()) return isPrivate.N;

        // 대표 케이스들: "Y", "on", "true", "1"
        if ("Y".equalsIgnoreCase(v) || "ON".equalsIgnoreCase(v) || "TRUE".equalsIgnoreCase(v) || "1".equals(v)) {
            return isPrivate.Y;
        }
        return isPrivate.N;
    }
}
