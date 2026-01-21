package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.LoginService;
import utils.HashUtil;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(
		urlPatterns = {"/main", "/login/*", "/index.jsp"}
		)

public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = requestURI.substring(contextPath.length());
		
		HttpSession session = request.getSession(false);
		System.out.println("actionPath: " + actionPath);
		if (actionPath.equals("/")) {
			if (session == null) {
				response.sendRedirect(contextPath + "/login");
			} else {
				response.sendRedirect(contextPath + "/main");
			}
		} else if (actionPath.equals("/login") || actionPath.equals("/login/login.do")) {
			request.setAttribute("contentPage", "/WEB-INF/views/login/login.jsp");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/index.jsp");
			rd.forward(request, response);
		}  else if (actionPath.equals("/main")) {
			request.setAttribute("contentPage", "/WEB-INF/main.jsp");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
			rd.forward(request, response);
		} else if (actionPath.equals("/login/logout")) {
			if (session != null) {
				session.invalidate();
			}
			response.sendRedirect(contextPath + "/");
			return;
		} else {
			// 비정상적인 접근 페이지 연결
			// response.sendRedirect(contextPath + "/login");
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String action = command.substring("/login".length());
		HttpSession session = request.getSession(false);
		
		if (action.equals("/login.do")) {
			String user_id = request.getParameter("id");
			String user_passwd = request.getParameter("pw");
			// user_passwd = HashUtil.sha256(user_passwd); // 실무 bcrypt 등 타 암호화 로직 사용 필요
			
			LoginService ls = LoginService.getInstance();
			AccessDTO accessDTO = ls.DoLogin(user_id, user_passwd);
			if (accessDTO != null) {
				session = request.getSession();
				session.setAttribute("AccessInfo", accessDTO);
				// 조회 기준 MyPageService용(로그인동안 loginId값 기억 mypage관련 로직을 사용하기위해서 필요)
				session.setAttribute("loginId", user_id);
				response.sendRedirect(contextPath + "/main");
			} else {
				request.setAttribute("LoginErrorMsg", "로그인 정보가 맞지 않습니다.");
				request.setAttribute("contentPage", "/WEB-INF/views/login/login.jsp");
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/index.jsp");
				rd.forward(request, response);
			}
		} 
		// DOTO : AdminController로 이전 필요
		else if (action.equals("/registUser.do")) {
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
			
			response.sendRedirect("/WEB-INF/views/user/index.jsp");
		}
		
	}

}
