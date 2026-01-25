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

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.NoticeDTO;
import model.enumtype.NoticeType;
import model.enumtype.Role;
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

        String action = resolveAction(req);

        try {
            switch (action) {
                case "/create":
                    handleCreate(req, resp);
                    return;
                case "/update":
                    handleUpdate(req, resp);
                    return;
                case "/delete":
                    handleDelete(req, resp);
                    return;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND); return;
            }
        } catch (NoticeService.AccessDeniedException e) {
        	if (resp.isCommitted()) return; // 응답 있다면 포워드 금지 
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    // ========== GET Handlers ==========

    private void handleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String items = trimToNull(req.getParameter("items"));
        String text  = trimToNull(req.getParameter("text"));

        String tabType = trimToNull(req.getParameter("tabType"));
        if (tabType == null) tabType = "all";

        if (items != null && !items.equals("title") &&
            !items.equals("content") && !items.equals("all")) {
            items = null;
        }

        int page = parseInt(req.getParameter("page"), 1, 1, 1_000_000);
        int size = parseInt(req.getParameter("size"), 10, 1, 100);

        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        int limit = size;
        int offset = (page - 1) * size;

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);

        requireLogin(userId, role);

        int totalCount;
        List<NoticeDTO> list;

        if ("all".equals(tabType)) {
            totalCount = noticeService.countAllNotices(items, text, userId, role);
            list = noticeService.findPageAllNotices(limit, offset, items, text, userId, role);

        } else if ("lecture".equals(tabType) && lectureId == null) {
            totalCount = noticeService.countAllLectureNotices(items, text, userId, role);
            list = noticeService.findPageAllLectureNotices(limit, offset, items, text, userId, role);

        } else {
            if (lectureId == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required.");
                return;
            }
            totalCount = noticeService.countByLecture(lectureId.longValue(), items, text, userId, role);
            list = noticeService.findPageByLecture(lectureId.longValue(), limit, offset, items, text, userId, role);
        }

        int totalPages = (int) Math.ceil(totalCount / (double) size);
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
        req.setAttribute("userLectures", userLectures);
        req.setAttribute("hasUserLectures", userLectures != null && !userLectures.isEmpty());
        req.setAttribute("role", role.name());

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

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);
        requireLogin(userId, role);

        NoticeDTO notice = noticeService.getNoticeDetail(noticeId, lectureId, userId, role);
        if (notice == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("notice", notice);
        req.setAttribute("role", role.name());
        forwardLayout(req, resp, NOTICE_VIEW);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);
        requireLogin(userId, role);

        if (role == Role.STUDENT) {
            throw new NoticeService.AccessDeniedException("학생은 공지사항을 작성할 수 없습니다.");
        }

        List<LectureDTO> lectureList = noticeService.getAvailableLectures(userId, role);

        req.setAttribute("lectureList", lectureList);
        req.setAttribute("role", role.name());

        forwardLayout(req, resp, NOTICE_NEW);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);
        requireLogin(userId, role);

        if (role == Role.STUDENT) {
            throw new NoticeService.AccessDeniedException("학생은 공지사항을 수정할 수 없습니다.");
        }

        Long noticeId = parseLongNullable(req.getParameter("noticeId"));
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        if (noticeId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeId is required.");
            return;
        }

        NoticeDTO notice = noticeService.getNoticeForEdit(noticeId, lectureId, userId, role);
        if (notice == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("notice", notice);
        req.setAttribute("role", role.name());
        forwardLayout(req, resp, NOTICE_EDIT);
    }

    // ========== POST Handlers ==========

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);
        requireLogin(userId, role);

        if (role == Role.STUDENT) {
            throw new NoticeService.AccessDeniedException("학생은 공지사항을 작성할 수 없습니다.");
        }

        NoticeType noticeType = parseNoticeType(req, resp);
        if (noticeType == null) return; // 400 already sent

        Long lectureId = null;
        if (noticeType == NoticeType.LECTURE) {
            lectureId = parseLongNullable(req.getParameter("lectureId"));
            if (lectureId == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required for LECTURE notice.");
                return;
            }
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

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);
        requireLogin(userId, role);

        if (role == Role.STUDENT) {
            throw new NoticeService.AccessDeniedException("학생은 공지사항을 수정할 수 없습니다.");
        }

        Long noticeId = parseLongNullable(req.getParameter("noticeId"));
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        if (noticeId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeId is required.");
            return;
        }

        NoticeType noticeType = parseNoticeType(req, resp);
        if (noticeType == null) return; // 400 already sent

        // 업데이트 시에도 정합성 유지: LECTURE면 lectureId 필수, ANNOUNCEMENT면 lectureId는 null이 자연스럽다.
        if (noticeType == NoticeType.LECTURE && lectureId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "lectureId is required for LECTURE notice.");
            return;
        }
        if (noticeType == NoticeType.ANNOUNCEMENT) {
            // 전체공지는 lectureId가 들어오더라도 혼선을 줄이기 위해 null로 정규화(선택)
            lectureId = null;
        }

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

        HttpSession session = req.getSession(false);
        Long userId = getLoginUserId(session);
        Role role = getLoginRole(session);
        requireLogin(userId, role);

        if (role == Role.STUDENT) {
            throw new NoticeService.AccessDeniedException("학생은 공지사항을 삭제할 수 없습니다.");
        }

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

    private NoticeType parseNoticeType(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String noticeTypeStr = trimToNull(req.getParameter("noticeType"));
        if (noticeTypeStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "noticeType is required.");
            return null;
        }
        try {
            return NoticeType.valueOf(noticeTypeStr);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid noticeType: " + noticeTypeStr);
            return null;
        }
    }

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
        Object v = session.getAttribute("AccessInfo");
        if (!(v instanceof AccessDTO)) return null;
        return ((AccessDTO) v).getUserId();
    }

    private Role getLoginRole(HttpSession session) {
        if (session == null) return null;
        Object v = session.getAttribute("AccessInfo");
        if (!(v instanceof AccessDTO)) return null;
        return ((AccessDTO) v).getRole();
    }

    private void requireLogin(Long userId, Role role) {
        if (userId == null || role == null) {
            throw new NoticeService.AccessDeniedException("로그인이 필요합니다.");
        }
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
