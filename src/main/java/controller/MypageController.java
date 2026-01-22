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
import model.dto.UserDTO;
import model.enumtype.Role;
import service.MyPageService;

import java.io.IOException;
import java.util.List;

@WebServlet("/mypage/*")
public class MypageController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private MyPageService myPageService = new MyPageService();
	private LectureDAO lectureDAO = LectureDAO.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 HttpSession session = request.getSession(false);
	        if (session == null) {
	            response.sendRedirect(request.getContextPath() + "/login");
	            return;
	        }

	        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
	        if (access == null) {
	            response.sendRedirect(request.getContextPath() + "/login");
	            return;
	        }
	        // loginId 확보
	        String loginId = (String) session.getAttribute("loginId");
	        if (loginId == null) {
	            // 세션 꼬임 방어
	            session.invalidate();
	            response.sendRedirect(request.getContextPath() + "/login");
	            return;
	        }
	        
	        String action = request.getPathInfo();
	        if (action == null || action.equals("/")) {
	            action = "/studentPage";
	        }
		
		switch (action) {
		// 학생관련
		case "/studentPage": {	// 학생정보
			// ROLE이 STUDENT인경우(학생)만 접근가능
			if (access.getRole() != Role.STUDENT) {
		        response.sendError(HttpServletResponse.SC_FORBIDDEN);
		        return;
		    }
			
			 MypageDTO mypage = myPageService.getMypageDTO(loginId);
	            if (mypage == null) {
	                response.sendRedirect(request.getContextPath() + "/login");
	                return;
	            }
	            
	            request.setAttribute("mypage", mypage);

			request.setAttribute(
			        "contentPage",
			        "/WEB-INF/views/student/studentPage.jsp"
			    );

			    request.getRequestDispatcher(
			        "/WEB-INF/views/layout/layout.jsp"
			    ).forward(request, response);

			    return;
		}
		case "/score": {	// 내 점수(학생)
			// ROLE이 STUDENT인경우(학생)만 접근가능
			if (access.getRole() != Role.STUDENT) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			 MypageDTO mypage = myPageService.getMypageDTO(loginId);
	            if (mypage == null) {
	                response.sendRedirect(request.getContextPath() + "/login");
	                return;
	            }
	            
	            request.setAttribute("mypage", mypage);

			request.setAttribute(
			        "contentPage",
			        "/WEB-INF/views/student/totScore.jsp"
			    );

			    request.getRequestDispatcher(
			        "/WEB-INF/views/layout/layout.jsp"
			    ).forward(request, response);

			    return;
		}
		case "/mySubjectPage": {	// 내가 수강한과목(학생)
			// ROLE이 STUDENT인경우(학생)만 접근가능
			if (access.getRole() != Role.STUDENT) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			 MypageDTO mypage = myPageService.getMypageDTO(loginId);
	            if (mypage == null) {
	                response.sendRedirect(request.getContextPath() + "/login");
	                return;
	            }
	            request.setAttribute("mypage", mypage);
	            
	         // 내가 수강중인 강의 목록 불러오기
	         long userId = access.getUserId();
	         List<MyLectureDTO> myLecture = lectureDAO.selectMyEnrollmentedLecture(userId);
	         request.setAttribute("myLecture", myLecture);
	         
			request.setAttribute(
			        "contentPage",
			        "/WEB-INF/views/student/mySubjectPage.jsp"
			    );

			    request.getRequestDispatcher(
			        "/WEB-INF/views/layout/layout.jsp"
			    ).forward(request, response);

			    return;
		}
		case "/enrollmentPage": {	// 수강신청(학생)
			// ROLE이 STUDENT인경우(학생)만 접근가능
			if (access.getRole() != Role.STUDENT) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			 MypageDTO mypage = myPageService.getMypageDTO(loginId);
	            if (mypage == null) {
	                response.sendRedirect(request.getContextPath() + "/login");
	                return;
	            }
	            
	            request.setAttribute("mypage", mypage);

			request.setAttribute(
			        "contentPage",
			        "/WEB-INF/views/student/enrollmentPage.jsp"
			    );

			    request.getRequestDispatcher(
			        "/WEB-INF/views/layout/layout.jsp"
			    ).forward(request, response);

			    return;
		}
		case "/mySchedule": {	// 내 스케줄(학생)
			// ROLE이 STUDENT인경우(학생)만 접근가능
			if (access.getRole() != Role.STUDENT) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			 MypageDTO mypage = myPageService.getMypageDTO(loginId);
	            if (mypage == null) {
	                response.sendRedirect(request.getContextPath() + "/login");
	                return;
	            }
	            
	            request.setAttribute("mypage", mypage);

			request.setAttribute(
			        "contentPage",
			        "/WEB-INF/views/student/mySchedule.jsp"
			    );

			    request.getRequestDispatcher(
			        "/WEB-INF/views/layout/layout.jsp"
			    ).forward(request, response);

			    return;
		}
		default:
			 response.sendError(HttpServletResponse.SC_NOT_FOUND);
             break;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}