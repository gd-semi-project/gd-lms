package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder.Default;
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
		
//		if (session == null || session.getAttribute("AccessInfo") == null) {
//		    response.sendRedirect(request.getContextPath() + "/login");
//		    return;
//		}
//		
//		String loginId = (String) session.getAttribute("loginId");
//		
//		String path = request.getPathInfo();
//		if(path == null || path.equals("/")) {
//			path = "/studentPage";
//		}
		
		 String ctx = request.getContextPath();
	        String uri = request.getRequestURI();
	        String action = uri.substring(ctx.length() + "/mypage".length());
	        
	        if (action == null || action.isBlank()) action = "/studentPage";
		
		switch (action) {
		case "/studentPage": {
			MypageDTO mypage = myPageService.getMypageDTO("loginId");
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