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
import utils.PasswordUtil;

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
		} else if (actionPath.equals("/login/passwordReset")) {
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/identityVerification.jsp");
			rd.forward(request, response);
		} else if (actionPath.equals("/login/resetPasswordForm")) {
		    Boolean resetAuth = (session != null)
		            ? (Boolean) session.getAttribute("resetAuth")
		            : null;

		    String resetToken = (session != null)
		            ? (String) session.getAttribute("resetToken")
		            : null;

		    Long resetUserId = (session != null)
		            ? (Long) session.getAttribute("resetUserId")
		            : null;

		    // 🔒 비정상 접근 차단
		    if (session == null || resetAuth == null || !resetAuth
		            || resetToken == null || resetUserId == null) {

		        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access");
		        return;
		    }

		    // 캐시 방지 (선택이지만 강력 추천)
		    response.setHeader("Cache-Control", "no-store");
		    response.setHeader("Pragma", "no-cache");
		    response.setDateHeader("Expires", 0);

		    RequestDispatcher rd =
		        request.getRequestDispatcher("/WEB-INF/views/login/resetPasswordForm.jsp");
		    rd.forward(request, response);
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
		} else if (action.equals("/check-info")) {
			// 1. 요청 파라미터
		    String email = request.getParameter("email");
		    String birthDate = request.getParameter("birthDate"); // "yyyy-MM-dd" 형식

		    // 2. 입력 검증
		    if (email == null || email.trim().isEmpty() || birthDate == null || birthDate.trim().isEmpty()) {
		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        response.getWriter().write("{\"error\":\"email and birthDate are required\"}");
		        return;
		    }

		    // 3. 서비스 호출
		    LoginService loginService = LoginService.getInstance();
		    boolean isMatch = loginService.verifyUserInfo(email, birthDate); 
		    // verifyUserInfo: email + birthDate 일치하면 true, 아니면 false
		    
		    session.setAttribute("tokenType", "PasswordReset");
		    
		    // 4. JSON 응답 설정
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    String json = "{\"match\":" + isMatch + "}";
		    response.getWriter().write(json);
		} else if (action.equals("/get-user-id")) {
			// 1. 요청 파라미터
		    String email = request.getParameter("email");
		    String birthDate = request.getParameter("birthDate"); // "yyyy-MM-dd" 형식

		    // 2. 입력 검증
		    if (email == null || email.trim().isEmpty() || birthDate == null || birthDate.trim().isEmpty()) {
		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        response.getWriter().write("{\"error\":\"email and birthDate are required\"}");
		        return;
		    }

		    // 3. 서비스 호출
		    LoginService loginService = LoginService.getInstance();
		    Long userId = loginService.getUserId(email, birthDate);
		    
		    if (userId == 0) {
		        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		        response.getWriter().write("{\"error\":\"userId not found\"}");
		        return;
		    }
		    
		    // 4. JSON 응답 설정
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    String json = "{\"userId\":" + userId + "}";
		    response.getWriter().write(json);
		} else if (action.equals("/create-token")) {
			LoginService ls = LoginService.getInstance();
		    Long userId = Long.parseLong(request.getParameter("userId"));
		    String token_type = (String) session.getAttribute("tokenType");
		    if (userId == null || userId == 0) {
		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        return;
		    }
		    
		    String token = ls.getPlainToken(userId, token_type, request.getRemoteAddr());
		    session.setAttribute("resetAuth", true);
		    session.setAttribute("resetUserId", userId);
		    session.setAttribute("resetToken", token);
		} else if (action.equals("/resetPassword")) {
			LoginService ls = LoginService.getInstance();
		    Long userId = (Long) session.getAttribute("resetUserId");
		    String token = (String) session.getAttribute("resetToken");

		    if (session == null || token == null) {
		        response.sendError(HttpServletResponse.SC_FORBIDDEN);
		        return;
		    }
		    
		    // DB 검증 후 진행
		    String hashToken = HashUtil.sha256(token);
		    boolean valid = ls.verifyResetToken(userId, hashToken);
		    if (!valid) {
		        response.sendError(HttpServletResponse.SC_FORBIDDEN);
		        return;
		    }
		    
		    // 임시 비밀번호 생성
		    String tempPassword = PasswordUtil.generateTempPassword();

		    // DB 반영 (암호화 필수)
		    ls.issueTempPassword(userId, tempPassword);

		    // 🔥 재사용 방지
		    session.removeAttribute("resetAuth");
		    session.removeAttribute("resetUserId");
		    session.removeAttribute("resetToken");
		    session.removeAttribute("tokenType");

		    // 사용자 전달용
		    request.setAttribute("tempPassword", tempPassword);

		    RequestDispatcher rd =
		        request.getRequestDispatcher("/WEB-INF/views/login/resetResult.jsp");
		    rd.forward(request, response);
		}
		
	}

}
