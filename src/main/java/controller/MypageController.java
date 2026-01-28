package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder.Default;
import model.dao.LectureDAO;
import model.dto.AccessDTO;
import model.dto.MyLectureDTO;
import model.dto.MypageDTO;
import model.dto.MyscheduleDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.EnrollmentService;
import service.MyPageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/mypage/*")
public class MypageController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private MyPageService myPageService = new MyPageService();
	private LectureDAO lectureDAO = LectureDAO.getInstance();
	private EnrollmentService enrollmentService = new EnrollmentService();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 HttpSession session = request.getSession(false);
		 String ctx = request.getContextPath();
		 // 로그인 세션 체크
	        if (session == null) {
	        	session = request.getSession();
	        	session.setAttribute("errorMessage", "로그인이 필요합니다.");
	            response.sendRedirect(ctx + "/error?errorCode=401");
	            return;
	        }

	        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
	        if (access == null) {
	        	session.setAttribute("errorMessage", "로그인이 필요합니다.");
	            response.sendRedirect(ctx + "/error?errorCode=401");
	            return;
	        }
	        // loginId 확보
	        
	        String loginId = request.getParameter("a_loginId");
	        if (loginId == null) {
	        	loginId = (String) session.getAttribute("loginId");
	        }
	        
	        if (loginId == null) {
	            // 세션 꼬임 방어
	            session.invalidate();
	            session = request.getSession();
	            session.setAttribute("errorMessage", "세션정보가 유효하지 않습니다.");
	            response.sendRedirect(ctx + "/error?errorCode=401");
	            return;
	        }
	        
	        String action = request.getPathInfo();
	        if (action == null || action.equals("/")) {
	            action = "/studentPage";
	        }
		
			try {
				switch (action) {
				// 학생관련
				case "/studentPage": { // 학생정보
					// ROLE이 STUDENT인경우(학생)만 접근가능
					if (access.getRole() != Role.STUDENT && access.getRole() != Role.ADMIN) {
						session.setAttribute("errorMessage", "접근 권한이 없습니다.");
			            response.sendRedirect(ctx + "/error?errorCode=403");
						return;
					}

					MypageDTO mypage = myPageService.getMypageDTO(loginId);

					if (mypage == null) {
						session.setAttribute("errorMessage", "로그인정보를 찾을 수 없습니다.");
			            response.sendRedirect(ctx + "/error?errorCode=404");
						return;
					}

					request.setAttribute("mypage", mypage);

					request.setAttribute("contentPage", "/WEB-INF/views/student/studentPage.jsp");

					request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

					return;
				}
				case "/score": { // 내 점수(학생)
					// ROLE이 STUDENT인경우(학생)만 접근가능
					if (access.getRole() != Role.STUDENT) {
						session.setAttribute("errorMessage", "접근 권한이 없습니다(학생만 접근가능).");
			            response.sendRedirect(ctx + "/error?errorCode=403");
						return;
					}

					MypageDTO mypage = myPageService.getMypageDTO(loginId);
					if (mypage == null) {
						session.setAttribute("errorMessage", "로그인정보를 찾을 수 없습니다.");
			            response.sendRedirect(ctx + "/error?errorCode=404");
						return;
					}

					request.setAttribute("mypage", mypage);

					request.setAttribute("contentPage", "/WEB-INF/views/student/totScoreNotice.jsp");

					request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

					return;
				}

				case "/enrollmentPage": { // 수강신청(학생)
					// ROLE이 STUDENT인경우(학생)만 접근가능
					if (access.getRole() != Role.STUDENT) {
						session.setAttribute("errorMessage", "접근 권한이 없습니다(학생만 접근가능).");
			            response.sendRedirect(ctx + "/error?errorCode=403");
						return;
					}
					// 수강신청기능일경우 접근가능
					if (!enrollmentService.isEnrollmentPeriod()) {

						request.setAttribute("contentPage", "/WEB-INF/views/student/enrollmentClose.jsp");

						request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
						return;
					}

					MypageDTO mypage = myPageService.getMypageDTO(loginId);
					if (mypage == null) {
						session.setAttribute("errorMessage", "로그인정보를 찾을 수 없습니다.");
			            response.sendRedirect(ctx + "/error?errorCode=404");
						return;
					}

					request.setAttribute("mypage", mypage);

					request.setAttribute("contentPage", "/WEB-INF/views/student/enrollNotice.jsp");

					request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);
					return;
				}
				case "/mySchedule": { // 내 스케줄(학생)
					// ROLE이 STUDENT인경우(학생)만 접근가능
					if (access.getRole() != Role.STUDENT) {
						session.setAttribute("errorMessage", "접근 권한이 없습니다(학생만 접근가능).");
			            response.sendRedirect(ctx + "/error?errorCode=403");
						return;
					}

					MypageDTO mypage = myPageService.getMypageDTO(loginId);
					if (mypage == null) {
						session.setAttribute("errorMessage", "로그인정보를 찾을 수 없습니다.");
			            response.sendRedirect(ctx + "/error?errorCode=404");
						return;
					}

					request.setAttribute("mypage", mypage);

					// 내가 스케줄 불러오기
					Long userId = access.getUserId();
					if (userId == null) {
					    session.invalidate();
					    session = request.getSession();
					    session.setAttribute("errorMessage", "세션 정보가 유효하지 않습니다.");
					    response.sendRedirect(ctx + "/error?errorCode=401");
					    return;
					}
					List<MyLectureDTO> myLecture = lectureDAO.selectMyEnrollmentedLecture(userId);
					List<MyscheduleDTO> mySchedule = lectureDAO.selectMySchedule(userId);

					Map<String, Map<Integer, String>> scheduleMap = new HashMap<>();

					for (MyscheduleDTO s : mySchedule) {
						scheduleMap.putIfAbsent(s.getWeekDay(), new HashMap<>());

						for (int h = s.getStartHour(); h < s.getEndHour(); h++) {
							scheduleMap.get(s.getWeekDay()).put(h, s.getLectureTitle());
						}
					}
					request.setAttribute("myLecture", myLecture);
					request.setAttribute("mySchedule", mySchedule);
					request.setAttribute("scheduleMap", scheduleMap);

					request.setAttribute("contentPage", "/WEB-INF/views/student/mySchedule.jsp");

					request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

					return;
				}
				default:
					session.setAttribute("errorMessage", "존재하지 않는 요청입니다.");
					response.sendRedirect(ctx + "/error?errorCode=404");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("errorMessage", "페이지 처리 중 서버 오류가 발생했습니다.");
				response.sendRedirect(ctx + "/error?errorCode=500");
				return;
			}
			
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}