package controller;

import java.io.IOException;
import java.util.List;

import exception.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.Role;
import service.*;

@WebServlet("/lecture/*")
public class LectureController extends HttpServlet {

    private final LectureService lectureService = LectureService.getInstance();
    private final LectureRequestService lectureRequestService = LectureRequestService.getInstance();
    private final ScorePolicyService scorePolicyService = ScorePolicyService.getInstance();
    private final InstructorService instructorService = InstructorService.getInstance();
    private final LectureAccessService lectureAccessService = new LectureAccessService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();
        String action = uri.substring(ctx.length() + "/lecture".length());

        if (action == null || action.isBlank()) action = "/detail";

        HttpSession session = request.getSession(false);
        AccessDTO accessInfo = (AccessDTO) session.getAttribute("AccessInfo");

        if (accessInfo == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

        Long userId = accessInfo.getUserId();
        Role role = accessInfo.getRole();

        try {

            Long lectureId = parseLong(request.getParameter("lectureId"));
            if (lectureId == null) {
                throw new BadRequestException("강의 정보가 올바르지 않습니다.");
            }

            LectureDTO lecture = lectureService.getLectureDetail(lectureId);
            if (lecture == null) {
                throw new ResourceNotFoundException("존재하지 않는 강의입니다.");
            }

            // 🔐 공통 권한 체크 (수강 여부 / 담당 강의 여부)
            lectureAccessService.assertCanAccessLecture(userId, lectureId, role);

            // 🔐 detail 제외 + 학생일 때만 승인/진행중 체크
            if (!action.equals("/detail") && role == Role.STUDENT) {
                lectureAccessService.assertLectureIsOpen(lecture);
            }

            switch (action) {

            // ================= 강의 상세 =================
            case "/detail": {

                request.setAttribute("lecture", lecture);
                request.setAttribute("schedules",
                        lectureRequestService.getLectureSchedules(lectureId));
                request.setAttribute("scorePolicy",
                        scorePolicyService.getPolicy(lectureId));

                var profile =
                        instructorService.getInstructorProfile(lecture.getUserId());
                request.setAttribute("instructor", profile.get("instructor"));
                request.setAttribute("user", profile.get("user"));

                request.setAttribute("activeTab", "detail");
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/detail.jsp");
                break;
            }

            // ================= 수강생 목록 =================
            case "/students": {

                if (role == Role.STUDENT) {
                    throw new AccessDeniedException("수강생 목록은 교수만 조회할 수 있습니다.");
                }

                List<LectureStudentDTO> students =
                        lectureService.getLectureStudents(lectureId);

                request.setAttribute("lecture", lecture);
                request.setAttribute("students", students);
                request.setAttribute("activeTab", "students");
                request.setAttribute("contentPage",
                        "/WEB-INF/views/lecture/students.jsp");
                break;
            }

            default:
                throw new ResourceNotFoundException("요청하신 페이지를 찾을 수 없습니다.");
            }

            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (BadRequestException e) {

            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("contentPage", "/WEB-INF/views/error/400.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp")
                   .forward(request, response);

        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (ResourceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (InternalServerException e) {
            throw e;
        }
    }

    // ================= 유틸 =================
    private Long parseLong(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}