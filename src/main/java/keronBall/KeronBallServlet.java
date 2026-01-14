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
		String action = command.substring("/keronBall".length());
		
		if (action.isEmpty()) action = "/";
		
		String contentPage = "";
		
		switch(action) {
		
		case "/remote":
			contentPage = "/WEB-INF/keronBall/keronBallModal.jsp";
			break;
		
		case "/time":
			contentPage = "/WEB-INF/keronBall/timeControl.jsp";
			break;
			
		default:
			break;
		}
		
		request.setAttribute("contentPage", contentPage);
		
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/keronBall/keronBallModal.jsp");
		rd.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
