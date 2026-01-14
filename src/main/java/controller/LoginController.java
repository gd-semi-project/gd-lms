package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.UserDTO;
import service.LoginService;
import utils.HashUtil;

import java.io.IOException;

@WebServlet("/loginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		
		if (command.equals("/login.do")) {
			String user_id = request.getParameter("id");
			String user_passwd = request.getParameter("passwd");
			user_passwd = HashUtil.sha256(user_passwd); // 실무 bcrypt 등 타 암호화 로직 사용 필요
			
			LoginService ls = LoginService.getInstance();
			UserDTO userDTO = ls.DoLogin(user_id, user_passwd);
			
			if (userDTO != null) {
				request.setAttribute("UserInfo", userDTO);
				response.sendRedirect("/WEB-INF/testViewSihyeon/");
			} else {
				// 로그인 실패 로직
				response.sendRedirect("/index_goheekwon.jsp");
			}
		}
	}

}
