package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.QnaAnswerDTO;
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
    
    // SC_FORBIDDEN = 403
    // SC_BAD_REQUEST = 400
    // SC_NOT_FOUND = 404
    // SC_BAD_REQUEST =400

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO accessInfo = (AccessDTO) session.getAttribute("AccessInfo");
        if (accessInfo == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        long userId = accessInfo.getUserId();
        Role role = accessInfo.getRole();

        // 2) 공통 파라미터: lectureId는 목록/상세 모두 필요
        long lectureId = parseLong(request.getParameter("lectureId"));
        if (lectureId <= 0) {
            response.sendRedirect(ctx + "/lecture/list?error=invalidLectureId");
            return;
        }

        // 3) action 분기
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) action = "list";

        try {
            // 4) lecture 객체 세팅 (탭/화면 공통)
            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                session.setAttribute("errorMessage", "존재하지 않는 강의입니다.");
                response.sendRedirect(ctx + "/error?errorCode=404");
                return;
            }

            request.setAttribute("lecture", lecture);
            request.setAttribute("lectureId", lectureId);
            request.setAttribute("activeTab", "qna");

            // =========================
            // 상세
            // =========================
            if ("view".equalsIgnoreCase(action)) {

                long qnaId = parseLong(request.getParameter("qnaId"));
                if (qnaId <= 0) {
                    response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId + "&error=invalidQnaId");
                    return;
                }
                // 에러 메세지
                String err = request.getParameter("error");
                if ("badRequest".equals(err)) request.setAttribute("errorMessage", "입력값을 다시 확인해주세요.");
                else if ("emptyContent".equals(err)) request.setAttribute("errorMessage", "내용을 입력해주세요.");
                else if ("invalidInput".equals(err)) request.setAttribute("errorMessage", "요청값이 올바르지 않습니다.");

                QnaPostDTO post = qnaService.getPostDetail(qnaId, lectureId, userId, role);
                if (post == null) {
                    throw new QnaService.NotFoundException("해당 Q&A 글을 찾을 수 없습니다.");
                }

                List<QnaAnswerDTO> answers = qnaService.getAnswers(qnaId, lectureId, userId, role);

                request.setAttribute("post", post);
                request.setAttribute("answers", answers);

                request.setAttribute("contentPage", "/WEB-INF/views/lecture/qna/view.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                       .forward(request, response);
                return;
            }

            // =========================
            // 수정 폼
            // =========================
            if ("edit".equalsIgnoreCase(action)) {

                long qnaId = parseLong(request.getParameter("qnaId"));
                if (qnaId <= 0) {
                    response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId + "&error=invalidQnaId");
                    return;
                }
                
                // 에러 메세지
                String err = request.getParameter("error");
                if ("badRequest".equals(err)) request.setAttribute("errorMessage", "입력값을 다시 확인해주세요.");
                
                QnaPostDTO post = qnaService.getPostDetail(qnaId, lectureId, userId, role);
                if (post == null) {
                    throw new QnaService.NotFoundException("해당 Q&A 글을 찾을 수 없습니다.");
                }

                // 권한 체크: 학생은 본인 글만 수정 가능
                if (role == Role.STUDENT && post.getAuthorId() != userId) {
                    throw new QnaService.AccessDeniedException("수정 권한이 없습니다.");
                }

                request.setAttribute("post", post);

                request.setAttribute("contentPage", "/WEB-INF/views/lecture/qna/edit.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                       .forward(request, response);
                return;
            }

            // =========================
            // 작성 폼
            // =========================
            if ("writeForm".equalsIgnoreCase(action)) {

                // 학생만 질문 작성 가능
                if (role != Role.STUDENT) {
                    throw new QnaService.AccessDeniedException("질문 작성은 학생만 가능합니다.");
                }
                // 작성중 입력 오류값 에러 메세지
                String err = request.getParameter("error");
                if ("badRequest".equals(err)) {
                    request.setAttribute("errorMessage", "입력값을 다시 확인해주세요.");
                } else if ("emptyField".equals(err)) {
                    request.setAttribute("errorMessage", "제목/내용은 필수입니다.");
                }

                
                request.setAttribute("contentPage", "/WEB-INF/views/lecture/qna/write.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                       .forward(request, response);
                return;
            }

            // =========================
            // 목록 (기본)
            // =========================
            
            // 에러 메세지
            String err = request.getParameter("error");
            if ("badRequest".equals(err)) {
                request.setAttribute("errorMessage", "잘못된 요청입니다.");
            }

            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), DEFAULT_SIZE);
            if (size > MAX_SIZE) size = MAX_SIZE;
            if (size <= 0) size = DEFAULT_SIZE;
            if (page <= 0) page = 1;

            int offset = (page - 1) * size;

            int totalCount = qnaService.countByLecture(lectureId, userId, role);
            List<QnaPostDTO> list = qnaService.listByLecture(lectureId, size, offset, userId, role);
            int totalPages = (int) Math.ceil(totalCount / (double) size);

            request.setAttribute("qnaList", list);
            request.setAttribute("page", page);
            request.setAttribute("size", size);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("totalPages", totalPages);

            request.setAttribute("contentPage", "/WEB-INF/views/lecture/qna/list.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (QnaService.UnauthorizedException e) {
        	// 401 로그인 오류
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;

        } catch (QnaService.AccessDeniedException e) {
        	// 403 권한오류
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(ctx + "/error?errorCode=403");
            return;

        } catch (QnaService.NotFoundException e) {
        	// 404 경로,리소스 오류
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(ctx + "/error?errorCode=404");
            return;

        } catch (Exception e) {
        	// 500 서버오류
            log("QnaController doGet error", e);
            session.setAttribute("errorMessage", "서버 오류가 발생했습니다.");
            response.sendRedirect(ctx + "/error?errorCode=500");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String ctx = request.getContextPath();

     // doGet, doPost 공통 로그인 체크 로직
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO accessInfo = (AccessDTO) session.getAttribute("AccessInfo");
        if (accessInfo == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        long userId = accessInfo.getUserId();
        Role role = accessInfo.getRole();
        
        // 2) 공통 파라미터
        String action = request.getParameter("action");
        long lectureId = parseLong(request.getParameter("lectureId"));
        long qnaId = parseLong(request.getParameter("qnaId"));

        if (lectureId <= 0) {
            response.sendRedirect(ctx + "/lecture/list?error=invalidLectureId");
            return;
        }
     

        try {
        	// 입력오류 null 체크 
        	if (action == null || action.isBlank()) { throw new IllegalArgumentException("action is required."); }
        	   // action별 파라미터 검증
            if ("answer".equalsIgnoreCase(action)
                    || "update".equalsIgnoreCase(action)
                    || "delete".equalsIgnoreCase(action)) {
                if (qnaId <= 0) throw new IllegalArgumentException("qnaId is required.");
            }
            
            // =========================
            // 답변 등록 (INSTRUCTOR, ADMIN)
            // =========================
            if ("answer".equalsIgnoreCase(action)) {
                String content = request.getParameter("content");

                if (content == null || content.trim().isEmpty()) {
                    response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId 
                            + "&action=view&qnaId=" + qnaId + "&error=emptyContent");
                    return;
                }

                QnaAnswerDTO dto = new QnaAnswerDTO();
                dto.setQnaId(qnaId);
                dto.setContent(content.trim());

                qnaService.addAnswer(dto, lectureId, userId, role);

                response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId 
                        + "&action=view&qnaId=" + qnaId + "&success=answerCreated");
                return;
            }

            // =========================
            // 질문 등록 (STUDENT)
            // =========================
            if ("create".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                String isPrivate = request.getParameter("isPrivate"); // "Y" or "N"

                if (title == null || title.trim().isEmpty() 
                        || content == null || content.trim().isEmpty()) {
                    response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId 
                            + "&action=writeForm&error=emptyField");
                    return;
                }

                QnaPostDTO dto = new QnaPostDTO();
                dto.setLectureId(lectureId);
                dto.setTitle(title.trim());
                dto.setContent(content.trim());
                dto.setIsPrivate("Y".equals(isPrivate) 
                        ? model.enumtype.IsPrivate.Y : model.enumtype.IsPrivate.N);

                long newQnaId = qnaService.createPost(dto, userId, role);

                response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId 
                        + "&action=view&qnaId=" + newQnaId + "&success=created");
                return;
            }

            // =========================
            // 질문 수정 (작성자 본인, ADMIN, INSTRUCTOR)
            // =========================
            if ("update".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                String isPrivate = request.getParameter("isPrivate");

                if (title == null || title.trim().isEmpty() 
                        || content == null || content.trim().isEmpty()) {
                    response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId 
                            + "&action=view&qnaId=" + qnaId + "&error=invalidInput");
                    return;
                }

                QnaPostDTO dto = new QnaPostDTO();
                dto.setQnaId(qnaId);
                dto.setLectureId(lectureId);
                dto.setTitle(title.trim());
                dto.setContent(content.trim());
                dto.setIsPrivate("Y".equals(isPrivate) 
                        ? model.enumtype.IsPrivate.Y : model.enumtype.IsPrivate.N);

                qnaService.updatePost(dto, userId, role);

                response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId 
                        + "&action=view&qnaId=" + qnaId + "&success=updated");
                return;
            }

            // =========================
            // 질문 삭제 (작성자 본인, ADMIN, INSTRUCTOR)
            // =========================
            if ("delete".equalsIgnoreCase(action)) {
                qnaService.deletePost(qnaId, lectureId, userId, role);

                response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId + "&success=deleted");
                return;
            }

           
            response.sendRedirect(ctx + "/lecture/qna?lectureId=" + lectureId);

        } catch (IllegalArgumentException e) {

            String targetAction;

            if ("create".equalsIgnoreCase(action)) {
                // 작성 저장 실패
                targetAction = "writeForm";

            } else if ("update".equalsIgnoreCase(action)) {
                // 수정 저장 실패 
                targetAction = "edit";

            } else if ("answer".equalsIgnoreCase(action)) {
                // 답변 등록 실패 
                targetAction = "view";

            } else if ("delete".equalsIgnoreCase(action)) {
                // 삭제 실패 
                targetAction = "list";

            } else {
                // 나머지
                targetAction = "list";
            }

            String url = ctx + "/lecture/qna?lectureId=" + lectureId
                    + "&action=" + targetAction
                    + (qnaId > 0 ? "&qnaId=" + qnaId : "")
                    + "&error=badRequest";

            response.sendRedirect(url);
            return;
        } catch (QnaService.UnauthorizedException e) {
        	// 401 로그인 오류
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;

        } catch (QnaService.AccessDeniedException e) {
        	// 403 권한 오류
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(ctx + "/error?errorCode=403");
            return;

        } catch (QnaService.NotFoundException e) {
        	// 404 경로,리소스 오류
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(ctx + "/error?errorCode=404");
            return;

        } catch (Exception e) {
        	// 서버 오류
            log("QnaController doPost error", e);
            session.setAttribute("errorMessage", "서버 오류가 발생했습니다.");
            response.sendRedirect(ctx + "/error?errorCode=500");
            return;
        }
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}