package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import model.dao.UserDAO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.LoginService;
import utils.HashUtil;

import java.io.IOException;
import java.time.LocalDate;

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
		} else if (action.equals("/registUser.do")) {
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
			String user_passwd = request.getParameter("pw");
			user_passwd = HashUtil.sha256(user_passwd); // 실무 bcrypt 등 타 암호화 로직 사용 필요
			
			LoginService ls = LoginService.getInstance();
			UserDTO userDTO = ls.DoLogin(user_id, user_passwd);
			HttpSession session = request.getSession();
			if (userDTO != null) {
				session.setAttribute("UserInfo", userDTO);
				response.sendRedirect("/gd-lms/test.jsp");
			} else {
				session.setAttribute("LoginErrorMsg", "로그인 정보가 맞지 않습니다.");
				response.sendRedirect("/gd-lms/index_goheekwon.jsp");
			}
		} else if (action.equals("/registUser.do")) {
			UserDTO userDTO = new UserDTO();
			userDTO.setLoginId(request.getParameter("loginId"));
			userDTO.setPassword(request.getParameter("password"));
			userDTO.setName(request.getParameter("name"));
			userDTO.setEmail(request.getParameter("enail"));
			userDTO.setBirthDate(LocalDate.parse(request.getParameter("birthDate")));
			
			Role role = Role.fromLabel(request.getParameter("role"));
			userDTO.setRole(role);
			
			LoginService ls = LoginService.getInstance();
			ls.RegistUser(userDTO);
			
			response.sendRedirect("/gd-lms/index_goheekwon.jsp");
		}
	}

}
