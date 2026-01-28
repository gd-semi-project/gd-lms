package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import exception.AccessDeniedException;
import exception.BadRequestException;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import exception.UnauthorizedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.ScoreDTO;
import model.enumtype.Role;
import service.LectureAccessService;
import service.LectureService;
import service.ScoreService;

@WebServlet("/score/*")
public class ScoreController extends HttpServlet {

	private final ScoreService scoreService = ScoreService.getInstance();
	private final LectureService lectureService = LectureService.getInstance();
	private final LectureAccessService lectureAccessService = new LectureAccessService();

	// ===================== GET =====================
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ctx = request.getContextPath();
		String uri = request.getRequestURI();
		String action = uri.substring(ctx.length() + "/score".length());

		if (action == null || action.isBlank())
			action = "/grades";

		HttpSession session = request.getSession(false);
		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
		Role role = access.getRole();

		try {

			switch (action) {

			// ================= 성적 조회 =================
			case "/grades": {

				Long lectureId = parseLong(request.getParameter("lectureId"));
				if (lectureId == null) {
					throw new BadRequestException("강의 정보가 올바르지 않습니다.");
				}

				// 🔐 접근 권한 체크
				lectureAccessService.assertCanAccessLecture(access.getUserId(), lectureId, role);

				LectureDTO lecture = lectureService.getLectureDetail(lectureId);
				lectureAccessService.assertLectureIsOpen(lecture);

				request.setAttribute("lecture", lecture);
				request.setAttribute("lectureId", lectureId);
				request.setAttribute("role", role);

				if (role == Role.INSTRUCTOR) {
					List<ScoreDTO> scores = scoreService.getScoreList(lectureId);
					request.setAttribute("scores", scores);

				} else if (role == Role.STUDENT) {
					Long studentId = access.getUserId();
					ScoreDTO myScore = scoreService.getMyScore(lectureId, studentId);
					request.setAttribute("myScore", myScore);
				}

				request.setAttribute("activeTab", "grades");
				request.setAttribute("contentPage", "/WEB-INF/views/lecture/grades.jsp");
				break;
			}

			// ================= 학생 전체 성적 =================
			case "/totscore": {

				if (role != Role.STUDENT) {
					throw new AccessDeniedException("학생만 접근 가능합니다.");
				}

				Long studentId = access.getUserId();
				List<ScoreDTO> myScores = scoreService.getMytotScore(studentId);

				request.setAttribute("myScores", myScores);
				request.setAttribute("activeTab", "myScore");
				request.setAttribute("contentPage", "/WEB-INF/views/student/totScore.jsp");
				break;
			}

			default:
				throw new ResourceNotFoundException("요청하신 페이지를 찾을 수 없습니다.");
			}

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

		} catch (BadRequestException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

		} catch (UnauthorizedException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

		} catch (AccessDeniedException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

		} catch (ResourceNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

		} catch (InternalServerException e) {
			throw e;
		}
	}

	// ===================== POST =====================
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ctx = request.getContextPath();
		String uri = request.getRequestURI();

		HttpSession session = request.getSession(false);
		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
		Role role = access.getRole();

		try {

			// ================= 성적 저장 =================
			if (uri.endsWith("/grades/save")) {

				Long lectureId = parseLong(request.getParameter("lectureId"));
				if (lectureId == null) {
					throw new BadRequestException("강의 정보가 올바르지 않습니다.");
				}

				lectureAccessService.assertCanAccessLecture(access.getUserId(), lectureId, role);

				if (role != Role.INSTRUCTOR) {
					throw new AccessDeniedException("교수만 성적 저장이 가능합니다.");
				}

				LectureDTO lecture = lectureService.getLectureDetail(lectureId);
				lectureAccessService.assertLectureIsOpen(lecture);

				List<ScoreDTO> scoreList = extractScoreList(request, lectureId);
				scoreService.saveScores(lectureId, scoreList);

				response.sendRedirect(ctx + "/score/grades?lectureId=" + lectureId);
				return;
			}

			// ================= 학점 계산 =================
			if (uri.endsWith("/grades/calculate")) {

				Long lectureId = parseLong(request.getParameter("lectureId"));
				if (lectureId == null) {
					throw new BadRequestException("강의 정보가 올바르지 않습니다.");
				}

				lectureAccessService.assertCanAccessLecture(access.getUserId(), lectureId, role);

				if (role != Role.INSTRUCTOR) {
					throw new AccessDeniedException("교수만 학점 계산이 가능합니다.");
				}

				LectureDTO lecture = lectureService.getLectureDetail(lectureId);
				lectureAccessService.assertLectureIsOpen(lecture);

				scoreService.calculateGrade(lectureId);

				response.sendRedirect(ctx + "/score/grades?lectureId=" + lectureId);
				return;
			}

			throw new ResourceNotFoundException("요청하신 작업을 처리할 수 없습니다.");

		} catch (BadRequestException e) {

			String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
			response.sendRedirect(
					ctx + "/score/grades?lectureId=" + request.getParameter("lectureId") + "&warning=" + msg);

		} catch (AccessDeniedException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

		} catch (UnauthorizedException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

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

	private List<ScoreDTO> extractScoreList(HttpServletRequest request, Long lectureId) {

		String[] studentIds = request.getParameterValues("studentId");
		List<ScoreDTO> list = new ArrayList<>();

		if (studentIds == null)
			return list;

		for (String sid : studentIds) {

			ScoreDTO dto = new ScoreDTO();
			dto.setLectureId(lectureId);
			dto.setStudentId(Long.parseLong(sid));

			String scoreIdParam = request.getParameter("scoreId_" + sid);
			if (scoreIdParam != null) {
				dto.setScoreId(Long.parseLong(scoreIdParam));
			}

			dto.setAssignmentScore(parseInteger(request.getParameter("assignmentScore_" + sid)));
			dto.setMidtermScore(parseInteger(request.getParameter("midtermScore_" + sid)));
			dto.setFinalScore(parseInteger(request.getParameter("finalScore_" + sid)));

			list.add(dto);
		}

		return list;
	}

	private Integer parseInteger(String s) {
		try {
			return (s == null || s.isBlank()) ? null : Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}
}