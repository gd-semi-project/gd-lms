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

import model.dto.NoticeDTO;
import service.NoticeService;

@WebServlet("/notice/*")
public class NoticeController extends HttpServlet {

    private final NoticeService noticeService = new NoticeService();

    // Layout & Content 경로 상수 (사용자 입력 금지)
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

        // ✅ DEV ONLY: 로그인 구현 전까지 임시 세션 주입
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
            // 권한 없음 에러 처리
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // ✅ DEV ONLY: 로그인 구현 전까지 임시 세션 주입
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
            // 권한 없음 에러 처리
            req.setAttribute("errorMessage", e.getMessage());
            forwardLayout(req, resp, ERROR_403);
        }
    }

    // ========== GET Handlers ==========

    /**
     * 공지사항 목록 조회
     * - 검색 파라미터: items (title/content/all), text (검색어)
     * - 페이징: page, size
     * - 필터: lectureId (null이면 전체 공지, 있으면 특정 강의 공지)
     */
    private void handleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 검색 조건 파라미터
        String items = trimToNull(req.getParameter("items"));
        String text  = trimToNull(req.getParameter("text"));

        // items 값 검증 (허용된 값만)
        if (items != null && !items.equals("title") && 
            !items.equals("content") && !items.equals("all")) {
            items = null;
        }

        // 페이징 파라미터
        int page = parseInt(req.getParameter("page"), 1, 1, 1_000_000);
        int size = parseInt(req.getParameter("size"), 10, 1, 100);

        // 강의 필터
        Long lectureId = parseLongNullable(req.getParameter("lectureId"));

        int limit = size;
        int offset = (page - 1) * size;

        // 세션에서 사용자 정보 가져오기
        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        int totalCount;
        List<NoticeDTO> list;

        // lectureId가 null이면 전체 공지, 있으면 특정 강의 공지
        if (lectureId == null) {
            totalCount = noticeService.countAll(items, text, userId, role);
            list = noticeService.findPageAll(limit, offset, items, text, userId, role);
        } else {
            totalCount = noticeService.countByLecture(lectureId, items, text, userId, role);
            list = noticeService.findPageByLecture(lectureId, limit, offset, items, text, userId, role);
        }

        int totalPages = (int) Math.ceil(totalCount / (double) size);

        // JSP로 전달할 속성 설정
        req.setAttribute("noticeList", list);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("lectureId", lectureId);
        req.setAttribute("items", items);
        req.setAttribute("text", text);

        forwardLayout(req, resp, NOTICE_LIST);
    }

    /**
     * 공지사항 상세 조회
     * - 조회수 증가
     * - 학생은 수강 중인 강의의 공지만 접근 가능
     */
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

    /**
     * 공지사항 작성 폼
     * - ADMIN: 전체 공지 + 강의 공지 모두 가능
     * - INSTRUCTOR: 강의 공지만 가능
     * - STUDENT: 접근 불가
     */
    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        req.setAttribute("lectureId", lectureId);

        forwardLayout(req, resp, NOTICE_NEW);
    }

    /**
     * 공지사항 수정 폼
     * - ADMIN: 모든 공지 수정 가능
     * - INSTRUCTOR: 본인이 작성한 강의 공지만
     * - STUDENT: 접근 불가
     */
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

    // ========== POST Handlers (PRG Pattern) ==========

    /**
     * 공지사항 생성
     */
    private void handleCreate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long userId = getLoginUserId(req.getSession(false));
        String role = getLoginRole(req.getSession(false));

        Long lectureId = parseLongNullable(req.getParameter("lectureId"));
        String noticeType = trimToNull(req.getParameter("noticeType"));
        String title = trimToNull(req.getParameter("title"));
        String content = trimToNull(req.getParameter("content"));

        NoticeDTO dto = new NoticeDTO();
        dto.setLectureId(lectureId);
        dto.setAuthorId(userId);
        dto.setNoticeType(noticeType);
        dto.setTitle(title);
        dto.setContent(content);

        long newId = noticeService.createNotice(dto, userId, role);

        // PRG 패턴: 생성 후 상세 페이지로 리다이렉트
        resp.sendRedirect(buildRedirectUrl(req, "/notice/view",
                "noticeId", String.valueOf(newId),
                "lectureId", lectureId == null ? null : String.valueOf(lectureId)));
    }

    /**
     * 공지사항 수정
     */
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

        // PRG 패턴: 수정 후 상세 페이지로 리다이렉트
        resp.sendRedirect(buildRedirectUrl(req, "/notice/view",
                "noticeId", String.valueOf(noticeId),
                "lectureId", lectureId == null ? null : String.valueOf(lectureId)));
    }

    /**
     * 공지사항 삭제
     */
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

        // PRG 패턴: 삭제 후 목록 페이지로 리다이렉트
        resp.sendRedirect(buildRedirectUrl(req, "/notice/list",
                "lectureId", lectureId == null ? null : String.valueOf(lectureId)));
    }

    // ========== Layout Helper ==========

    private void forwardLayout(HttpServletRequest req, HttpServletResponse resp, String contentPage)
            throws ServletException, IOException {
        req.setAttribute("contentPage", contentPage);
        req.getRequestDispatcher(LAYOUT_PAGE).forward(req, resp);
    }

    // ========== DEV Login Helper ==========

    /**
     * 개발용 임시 로그인 세션 주입
     * 실제 프로덕션에서는 제거 필요!
     */
    private void injectDevLogin(HttpServletRequest req) {
        HttpSession s = req.getSession(true);
        if (s.getAttribute("userId") == null) {
            // 테스트용 계정 설정 (필요에 따라 변경)
            s.setAttribute("userId", 1L);      // 관리자
            s.setAttribute("role", "ADMIN");
            s.setAttribute("userName", "DEV-ADMIN");
            
            // 교수 테스트: userId=2, role=INSTRUCTOR
            // 학생 테스트: userId=4, role=STUDENT
        }
    }

    // ========== Helper Methods ==========

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