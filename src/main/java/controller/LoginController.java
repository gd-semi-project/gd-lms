package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import model.dto.UserDTO;
import service.LoginService;
import utils.HashUtil;

import java.io.IOException;

@WebServlet("/login/*")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String action = command.substring("/login".length());

		if (action.equals("/login.do")) {
			response.sendRedirect("/gd-lms/index_goheekwon.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String action = command.substring("/login".length());
		
		if (action.equals("/login.do")) {
			String user_id = request.getParameter("id");
			String user_passwd = request.getParameter("passwd");
			user_passwd = HashUtil.sha256(user_passwd); // 실무 bcrypt 등 타 암호화 로직 사용 필요
			
			LoginService ls = LoginService.getInstance();
			UserDTO userDTO = ls.DoLogin(user_id, user_passwd);
			HttpSession session = request.getSession();
			if (userDTO != null) {
				session.setAttribute("UserInfo", userDTO);
				response.sendRedirect("/test.jsp");
			} else {
				session.setAttribute("LoginErrorMsg", "로그인 정보가 맞지 않습니다.");
				response.sendRedirect("/gd-lms/index_goheekwon.jsp");
			}
		}
	}

}
