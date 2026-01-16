package keronBall;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/keronBall/*")
public class KeronBallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/keronBall".length());
		
		if (actionPath.isEmpty()) actionPath = "/";
		
		String contentPage = "";
		
		switch(actionPath) {
		
		case "/remote":
			contentPage = "/WEB-INF/keronBall/keronBallModal.jsp";
			break;
		
		case "/time":
			contentPage = "/WEB-INF/keronBall/timeControlPanel.jsp";
			break;
			
		case "/db":
			contentPage = "/WEB-INF/keronBall/dbControlPanel.jsp";
			break;
			
			
			
		default:
			break;
		}
		
		request.setAttribute("contentPage", contentPage);
		
		
		RequestDispatcher rd = request.getRequestDispatcher(contentPage);
		rd.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/keronBall".length());
		String action = request.getParameter("action");
		
		switch(actionPath) {
		
		case "/db": {
			if("CREATEALL".equals(action)) {
				
				KeronBallService.getInstance().createAllDB(request);
				
				response.sendRedirect(contextPath + "/keronBall/db");
				return;
			} if("DELETEALL".equals(action)) {
				
				KeronBallService.getInstance().deleteAllDB();
				
				response.sendRedirect(contextPath + "/keronBall/db");
				return;
			} else {
				response.sendRedirect(contextPath + "/keronBall/db");
				return;
			}
		}
		}
	}

}
