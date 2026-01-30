package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(
		urlPatterns = {"/error"}
		)

public class ErrorController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contextPath = request.getContextPath();
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect(contextPath + "/login");
		} else {
			String errorMessage = (String) session.getAttribute("errorMessage");
			if (errorMessage == null) {
				session.setAttribute("errorMessage", "비인가 접근입니다.");
				response.sendRedirect(contextPath + "/error?errorCode=403");
				return;
			}
		}
		
		String errorCode = (String) request.getParameter("errorCode");
		String errorMessage = (String) session.getAttribute("errorMessage");
		request.setAttribute("errorMessage", errorMessage);
		session.removeAttribute("errorMessage");
		
		// 에러코드와 에러메시지는 각 서비스에서 redirect 발생
		if (errorCode.equals("400")) {
			response.sendError(400);
		} else if (errorCode.equals("401")) {
			response.sendError(401);
		} else if (errorCode.equals("403")) {
			response.sendError(403);
		} else if (errorCode.equals("404")) {
			response.sendError(404);
		} else if (errorCode.equals("500")) {
			response.sendError(500);
			
		}
	}
}
