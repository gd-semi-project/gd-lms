package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
		String contentPage = "";
		
		if (action.equals("/login.do")) {
			contentPage = "/index.jsp";
		} else if (action.equals("/logout.do")) {
			HttpSession session = request.getSession(false);
	        if (session != null) {
	            session.invalidate(); // 세션 무효화
	        }
	        contentPage = "/index.jsp";
		}
		// TODO : adminController 로 이전 필요
		else if (action.equals("/registUser.do")) {
			// response.sendRedirect("/gd-lms/index_goheekwon.jsp");
			contentPage = "/index.jsp";
		}
		request.setAttribute("contentPage", contentPage);
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/loginLayout.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String action = command.substring("/login".length());
		String contentPage = "";
		
		
		if (action.equals("/login.do")) {
			String user_id = request.getParameter("id");
			String user_passwd = request.getParameter("pw");
			user_passwd = HashUtil.sha256(user_passwd); // 실무 bcrypt 등 타 암호화 로직 사용 필요
			
			LoginService ls = LoginService.getInstance();
			UserDTO userDTO = ls.DoLogin(user_id, user_passwd);
			HttpSession session = request.getSession();
			if (userDTO != null) {
				session.setAttribute("UserInfo", userDTO);
				response.sendRedirect("/adminDashboard.jsp");
			} else {
				session.setAttribute("LoginErrorMsg", "로그인 정보가 맞지 않습니다.");
				response.sendRedirect("/gd-lms");
				return;
			}
		} 
		// DOTO : AdminController로 이전 필요
		else if (action.equals("/registUser.do")) {
			UserDTO userDTO = new UserDTO();
			userDTO.setLogin_id(request.getParameter("loginId"));
			userDTO.setPassword(request.getParameter("password"));
			userDTO.setName(request.getParameter("name"));
			userDTO.setEmail(request.getParameter("email"));
			userDTO.setBirth_date(LocalDate.parse(request.getParameter("birthDate")));
			
			Role role = Role.fromLabel(request.getParameter("role"));
			userDTO.setRole(role);
			
			LoginService ls = LoginService.getInstance();
			ls.RegistUser(userDTO);
			
			// response.sendRedirect("/gd-lms/login.jsp");
			contentPage = "/index.jsp";
		}
		


		request.setAttribute("contentPage", contentPage);
		
		if (contentPage.equals("/index.jsp")) {
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/loginLayout.jsp");
			rd.forward(request, response);
		}
	}

}
