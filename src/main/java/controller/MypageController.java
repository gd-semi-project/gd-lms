package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder.Default;
import model.dto.AccessDTO;
import model.dto.MypageDTO;
import model.dto.UserDTO;
import service.MyPageService;

import java.io.IOException;

@WebServlet("/mypage/*")
public class MypageController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private MyPageService myPageService = new MyPageService();

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
		case "/studentPage": {
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