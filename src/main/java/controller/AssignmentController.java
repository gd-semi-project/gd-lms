// AssignmentController.java
package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.AssignmentDTO;
import model.dto.AssignmentSubmissionDTO;
import model.dto.FileDTO;
import model.dto.LectureDTO;
import model.enumtype.Role;
import service.AssignmentService;
import service.FileUploadService;
import service.LectureService;
import utils.FileUploadUtil;

@WebServlet("/lecture/assignments")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,  // 메모리에 저장할 임계값
	    maxFileSize = 1024 * 1024 * 50,   // 업로드 파일 최대 크기 (50MB)
	    maxRequestSize = 1024 * 1024 * 100 // 요청 전체 크기 (100MB)
	)
public class AssignmentController extends HttpServlet {

    private final AssignmentService assignmentService = AssignmentService.getInstance();
    private final LectureService lectureService = LectureService.getInstance();

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

        long lectureId = parseLong(request.getParameter("lectureId"));
        if (lectureId <= 0) {
            response.sendRedirect(ctx + "/lecture/list?error=invalidLectureId");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) action = "list";

        try {
            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("lecture", lecture);
            request.setAttribute("lectureId", lectureId);
            request.setAttribute("activeTab", "assignments");
            
            // 목록
            if ("list".equalsIgnoreCase(action)) {
                List<AssignmentDTO> assignments = assignmentService.getAssignmentsByLecture(lectureId, userId, role);
                request.setAttribute("assignments", assignments);
                request.setAttribute("contentPage", "/WEB-INF/views/lecture/assignment/list.jsp");
            }

            // 상세
            else if ("view".equalsIgnoreCase(action)) {
                long assignmentId = parseLong(request.getParameter("assignmentId"));
                AssignmentDTO assignment = assignmentService.getAssignmentDetail(assignmentId, lectureId, userId, role);

                if (assignment == null) {
                    throw new AssignmentService.NotFoundException("과제를 찾을 수 없습니다.");
                }

                request.setAttribute("assignment", assignment);
                
                FileUploadService fus = FileUploadService.getInstance();
                if (role == Role.INSTRUCTOR || role == Role.ADMIN) {
                    // 교수: 모든 제출물 가져오기
                    List<AssignmentSubmissionDTO> submissions = assignmentService.getSubmissions(assignmentId, lectureId, userId, role);
                    request.setAttribute("submissions", submissions);

                    // 과제 자체 파일 조회
                    List<FileDTO> assignmentFiles = fus.getFileList("ASSIGNMENT", assignmentId);
                    request.setAttribute("fileList", assignmentFiles);

                    // 제출물별 파일 조회 (교수 채점용)
                    for (AssignmentSubmissionDTO sub : submissions) {
                        List<FileDTO> submissionFiles = fus.getFileList("ASSIGNMENT_SUBMISSION", sub.getSubmissionId());
                        sub.setFileList(submissionFiles); // DTO 안에 담아서 JSP에서 반복 출력
                    }

                } else if (role == Role.STUDENT) {
                    // 학생: 본인 제출물만
                    AssignmentSubmissionDTO mySubmission = assignmentService.getMySubmission(assignmentId, lectureId, userId, role);
                    request.setAttribute("mySubmission", mySubmission);

                    if (mySubmission != null) {
                        List<FileDTO> myFileList = fus.getFileList("ASSIGNMENT_SUBMISSION", mySubmission.getSubmissionId());
                        mySubmission.setFileList(myFileList);
                    }
                }


                request.setAttribute("contentPage", "/WEB-INF/views/lecture/assignment/view.jsp");
            }

            // 작성 폼 (교수)
            else if ("writeForm".equalsIgnoreCase(action)) {
                if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
                    throw new AssignmentService.AccessDeniedException("과제 생성 권한이 없습니다.");
                }
                request.setAttribute("contentPage", "/WEB-INF/views/lecture/assignment/write.jsp");
            }

            // 수정 폼 (교수)
            else if ("edit".equalsIgnoreCase(action)) {
                if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
                    throw new AssignmentService.AccessDeniedException("과제 수정 권한이 없습니다.");
                }

                long assignmentId = parseLong(request.getParameter("assignmentId"));
                AssignmentDTO assignment = assignmentService.getAssignmentDetail(assignmentId, lectureId, userId, role);
                request.setAttribute("assignment", assignment);
                request.setAttribute("contentPage", "/WEB-INF/views/lecture/assignment/edit.jsp");
            }

            // 제출 폼 (학생)
            else if ("submitForm".equalsIgnoreCase(action)) {
                if (role != Role.STUDENT) {
                    throw new AssignmentService.AccessDeniedException("학생만 제출 가능합니다.");
                }

                long assignmentId = parseLong(request.getParameter("assignmentId"));
                AssignmentDTO assignment = assignmentService.getAssignmentDetail(assignmentId, lectureId, userId, role);
                AssignmentSubmissionDTO mySubmission = assignmentService.getMySubmission(assignmentId, lectureId, userId, role);

                request.setAttribute("assignment", assignment);
                request.setAttribute("mySubmission", mySubmission);
                request.setAttribute("contentPage", "/WEB-INF/views/lecture/assignment/submit.jsp");
            }

            // 채점 폼 (교수)
            else if ("gradeForm".equalsIgnoreCase(action)) {
                if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
                    throw new AssignmentService.AccessDeniedException("채점 권한이 없습니다.");
                }

                long assignmentId = parseLong(request.getParameter("assignmentId"));
                long submissionId = parseLong(request.getParameter("submissionId"));

                AssignmentDTO assignment = assignmentService.getAssignmentDetail(assignmentId, lectureId, userId, role);
                List<AssignmentSubmissionDTO> submissions = assignmentService.getSubmissions(assignmentId, lectureId, userId, role);

                AssignmentSubmissionDTO submission = submissions.stream()
                        .filter(s -> s.getSubmissionId() == submissionId)
                        .findFirst()
                        .orElse(null);

                request.setAttribute("assignment", assignment);
                request.setAttribute("submission", submission);
                request.setAttribute("contentPage", "/WEB-INF/views/lecture/assignment/grade.jsp");
            }

            else {
                response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId);
                return;
            }

            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

        } catch (AssignmentService.AccessDeniedException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/accessDenied.jsp").forward(request, response);

        } catch (AssignmentService.NotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/notFound.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "과제 처리 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
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

        Collection<Part> partList = request.getParts();

        String action = request.getParameter("action");
        long lectureId = parseLong(request.getParameter("lectureId"));
        long assignmentId = parseLong(request.getParameter("assignmentId"));

        if (lectureId <= 0) {
            response.sendRedirect(ctx + "/lecture/list?error=invalidLectureId");
            return;
        }

        try {
            // 과제 생성
            if ("create".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                String dueDateStr = request.getParameter("dueDate");
                int maxScore = parseInt(request.getParameter("maxScore"), 100);

                AssignmentDTO dto = new AssignmentDTO();
                dto.setLectureId(lectureId);
                dto.setTitle(title.trim());
                dto.setContent(content.trim());
                dto.setDueDate(LocalDateTime.parse(dueDateStr));
                dto.setMaxScore(maxScore);

                long newId = assignmentService.createAssignment(dto, userId, role);

                response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId
                        + "&action=view&assignmentId=" + newId + "&success=created");
                return;
            }

            // 과제 수정
            if ("update".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                String dueDateStr = request.getParameter("dueDate");
                int maxScore = parseInt(request.getParameter("maxScore"), 100);

                AssignmentDTO dto = new AssignmentDTO();
                dto.setAssignmentId(assignmentId);
                dto.setLectureId(lectureId);
                dto.setTitle(title.trim());
                dto.setContent(content.trim());
                dto.setDueDate(LocalDateTime.parse(dueDateStr));
                dto.setMaxScore(maxScore);

                assignmentService.updateAssignment(dto, userId, role);

                response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId
                        + "&action=view&assignmentId=" + assignmentId + "&success=updated");
                return;
            }

            // 과제 삭제
            if ("delete".equalsIgnoreCase(action)) {
                assignmentService.deleteAssignment(assignmentId, lectureId, userId, role);

                response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId + "&success=deleted");
                return;
            }

            // 과제 제출
            if ("submit".equalsIgnoreCase(action)) {
                String content = request.getParameter("content");

                AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
                dto.setAssignmentId(assignmentId);
                dto.setContent(content != null ? content.trim() : "");
                
                assignmentService.submitAssignment(dto, lectureId, userId, role, request.getParts());

                response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId
                        + "&action=view&assignmentId=" + assignmentId + "&success=submitted");
                return;
            }

            // 채점
            if ("grade".equalsIgnoreCase(action)) {
                long submissionId = parseLong(request.getParameter("submissionId"));
                int score = parseInt(request.getParameter("score"), 0);
                String feedback = request.getParameter("feedback");

                assignmentService.gradeSubmission(submissionId, lectureId, score, feedback, userId, role);

                response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId
                        + "&action=view&assignmentId=" + assignmentId + "&success=graded");
                return;
            }

            response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId);

        } catch (AssignmentService.AccessDeniedException e) {
            response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId + "&error=accessDenied");

        } catch (AssignmentService.NotFoundException e) {
            response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId + "&error=notFound");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(ctx + "/lecture/assignments?lectureId=" + lectureId + "&error=serverError");
        }
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
