package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.AccessDTO;
import model.dto.ScoreDTO;
import model.enumtype.Role;
import service.ScoreService;

@WebServlet("/score/*")
public class ScoreController extends HttpServlet {

	private final ScoreService scoreService = ScoreService.getInstance();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ctx = request.getContextPath();
		String uri = request.getRequestURI();
		String action = uri.substring(ctx.length() + "/score".length());

		if (action == null || action.isBlank()) {
			action = "/grades";
		}

		// 로그인 체크
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect(ctx + "/login");
			return;
		}

		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
		if (access == null) {
			response.sendRedirect(ctx + "/login");
			return;
		}

		Role role = access.getRole();

		switch (action) {

		case "/grades": {

			Long lectureId = parseLong(request.getParameter("lectureId"));
			if (lectureId == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			// 공통: 성적 목록
			List<ScoreDTO> scores = scoreService.getScoreList(lectureId);

			request.setAttribute("scores", scores);
			request.setAttribute("lectureId", lectureId);
			request.setAttribute("role", role);

			if (role == Role.INSTRUCTOR) {
				request.setAttribute("midtermOpen", scoreService.isMidtermOpen());
				request.setAttribute("finalOpen", scoreService.isFinalOpen());
			}

			request.setAttribute("activeTab", "grades");
			request.setAttribute("contentPage", "/WEB-INF/views/lecture/grades.jsp");
			break;
		}

		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ctx = request.getContextPath();
		String uri = request.getRequestURI();

		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect(ctx + "/login");
			return;
		}

		AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
		if (access == null || access.getRole() != Role.INSTRUCTOR) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (uri.endsWith("/grades/save")) {

			Long lectureId = Long.parseLong(request.getParameter("lectureId"));

			boolean midtermDisabled = Boolean.parseBoolean(request.getParameter("midtermDisabled"));
			boolean finalDisabled = Boolean.parseBoolean(request.getParameter("finalDisabled"));

			List<ScoreDTO> scoreList = extractScoreList(request, lectureId);

			scoreService.saveScores(lectureId, scoreList, midtermDisabled, finalDisabled);

			response.sendRedirect(ctx + "/score/grades?lectureId=" + lectureId);
			return;
		}

		if (uri.endsWith("/grades/calculate")) {

			Long lectureId = Long.parseLong(request.getParameter("lectureId"));

			scoreService.calculateGrade(lectureId);

			response.sendRedirect(ctx + "/score/grades?lectureId=" + lectureId);
			return;
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

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

			dto.setScoreId(parseLong(request.getParameter("scoreId_" + sid)));

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