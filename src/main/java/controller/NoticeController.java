package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.LectureDTO;
import model.dto.NoticeDTO;
import service.NoticeService;

@WebServlet("/notice/*")
public class NoticeController extends HttpServlet {

    private final NoticeService noticeService = new NoticeService();

    private static final String LAYOUT_PAGE = "/WEB-INF/views/layout/layout.jsp";
    private static final String NOTICE_LIST = "/WEB-INF/views/notice/list.jsp";
    private static final String NOTICE_VIEW = "/WEB-INF/views/notice/view.jsp";
    private static final String NOTICE_NEW  = "/WEB-INF/views/notice/new.jsp";
    private static final String NOTICE_EDIT = "/WEB-INF/views/notice/edit.jsp";
    private static final String ERROR_403   = "/WEB-INF/views/error/403.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        injectDevLogin(req);

        String action = resolveAction(req);
        
        try {
            switch (action) {
                case "/list":
                    handleList(req, resp);
                    break;
                case "/view":
                    handleView(req, resp);
                    break;
                case "/new":
                    showCreateForm(req, resp);
                    break;
                case "/edit":
                    showEditForm(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NoticeService.AccessDeniedException e) {
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        injectDevLogin(req);

        String action = resolveAction(req);
        
        try {
            switch (action) {
                case "/create":
                    handleCreate(req, resp);
                    break;
                case "/update":
                    handleUpdate(req, resp);
                    break;
                case "/delete":
                    handleDelete(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NoticeService.AccessDeniedException e) {
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    // ========== GET Handlers ==========

    private void handleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String items = trimToNull(req.getParameter("items"));
        String text  = trimToNull(req.getParameter("text"));
        
        // 탭 구분: "all" (전체 공지만), "lecture" (강의 공지만)
        String tabType = trimToNull(req.getParameter("tabType"));
        if (tabType == null) tabType = "all"; // 기본값: 전체 공지 탭

        if (items != null && !items.equals("title") && 
            !items.equals("content") && !items.equals("all")) {
            items = null;
        }

        int page = parseInt(req.getParameter("page"), 1, 1, 1_000_000);
        int size = parseInt(req.getParameter("size"), 10, 1, 100);

        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        int limit = size;
        int offset = (page - 1) * size;

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        int totalCount;
        List<NoticeDTO> list;

        if ("all".equals(tabType)) {
            // 1️⃣ 전체 공지 (lecture_id IS NULL)
            totalCount = noticeService.countAllNotices(items, text, userId, role);
            list = noticeService.findPageAllNotices(limit, offset, items, text, userId, role);

        } else if ("lecture".equals(tabType) && lectureId == null) {
            // 2️⃣ 모든 강의 공지 (lecture_id IS NOT NULL)
            totalCount = noticeService.countAllLectureNotices(items, text, userId, role);
            list = noticeService.findPageAllLectureNotices(limit, offset, items, text, userId, role);

        } else {
            // 3️⃣ 특정 강의 공지
            totalCount = noticeService.countByLecture(lectureId, items, text, userId, role);
            list = noticeService.findPageByLecture(lectureId, limit, offset, items, text, userId, role);
        }

        int totalPages = (int) Math.ceil(totalCount / (double) size);

        // 사용자의 강의 목록 조회 (탭 표시용)
        List<LectureDTO> userLectures = noticeService.getUserLectures(userId, role);

        req.setAttribute("noticeList", list);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("lectureId", lectureId);
        req.setAttribute("items", items);
        req.setAttribute("text", text);
        req.setAttribute("tabType", tabType);
        req.setAttribute("userLectures", userLectures); // 강의 목록

        forwardLayout(req, resp, NOTICE_LIST);
    }

    private void handleView(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long noticeId = parseLongNullable(req.getParameter("noticeId"));
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        if (noticeId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeId is required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        NoticeDTO notice = noticeService.getNoticeDetail(noticeId, lectureId, userId, role);
        if (notice == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("notice", notice);
        forwardLayout(req, resp, NOTICE_VIEW);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        // 작성 가능한 강의 목록 조회
        List<LectureDTO> lectureList = noticeService.getAvailableLectures(userId, role);
        
        req.setAttribute("lectureList", lectureList);
        req.setAttribute("role", role);

        forwardLayout(req, resp, NOTICE_NEW);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long noticeId = parseLongNullable(req.getParameter("noticeId"));
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        if (noticeId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeId is required.");
            return;
        }

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        NoticeDTO notice = noticeService.getNoticeForEdit(noticeId, lectureId, userId, role);
        if (notice == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("notice", notice);
        forwardLayout(req, resp, NOTICE_EDIT);
    }

    // ========== POST Handlers ==========

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        String noticeType = trimToNull(req.getParameter("noticeType"));
        Long lectureId = null;

        if ("LECTURE".equals(noticeType)) {
            lectureId = parseLongNullable(req.getParameter("lectureId"));
        }

        String title = trimToNull(req.getParameter("title"));
        String content = trimToNull(req.getParameter("content"));

        NoticeDTO dto = new NoticeDTO();
        dto.setLectureId(lectureId);
        dto.setAuthorId(userId);
        dto.setNoticeType(noticeType);
        dto.setTitle(title);
        dto.setContent(content);

        long newId = noticeService.createNotice(dto, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/notice/view",
                "noticeId", String.valueOf(newId),
                "lectureId", lectureId == null ? null : String.valueOf(lectureId)));
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        Long noticeId = parseLongNullable(req.getParameter("noticeId"));
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        if (noticeId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeId is required.");
            return;
        }

        String noticeType = trimToNull(req.getParameter("noticeType"));
        String title = trimToNull(req.getParameter("title"));
        String content = trimToNull(req.getParameter("content"));

        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeId(noticeId);
        dto.setLectureId(lectureId);
        dto.setAuthorId(userId);
        dto.setNoticeType(noticeType);
        dto.setTitle(title);
        dto.setContent(content);

        noticeService.updateNotice(dto, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/notice/view",
                "noticeId", String.valueOf(noticeId),
                "lectureId", lectureId == null ? null : String.valueOf(lectureId)));
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        Long noticeId = parseLongNullable(req.getParameter("noticeId"));
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        if (noticeId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeId is required.");
            return;
        }

        noticeService.deleteNotice(noticeId, lectureId, userId, role);

        resp.sendRedirect(buildRedirectUrl(req, "/notice/list",
                "lectureId", lectureId == null ? null : String.valueOf(lectureId)));
    }

    // ========== Helpers ==========

    private void forwardLayout(HttpServletRequest req, HttpServletResponse resp, String contentPage)
            throws ServletException, IOException {
        req.setAttribute("contentPage", contentPage);
        req.getRequestDispatcher(LAYOUT_PAGE).forward(req, resp);
    }

    private void injectDevLogin(HttpServletRequest req) {
        HttpSession s = req.getSession(true);
        if (s.getAttribute("userId") == null) {
            s.setAttribute("userId", 1L);
            s.setAttribute("role", "ADMIN");
            s.setAttribute("userName", "DEV-ADMIN");
        }
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
            try { return Long.parseLong((String) v); } catch (Exception ignored) {}
        }
        return null;
    }

    private String getLoginRole(HttpSession session) {
        if (session == null) return null;
        Object v = session.getAttribute("role");
        return (v instanceof String) ? (String) v : null;
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
}