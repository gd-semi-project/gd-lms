package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.UserDAO;
import model.dto.AccessDTO;
import model.dto.LoginResultDTO;
import model.enumtype.LoginStatus;
import model.enumtype.UserStatus;
import service.LoginService;
import utils.HashUtil;
import utils.MailSender;
import utils.PasswordUtil;
import java.io.IOException;

@WebServlet(
		urlPatterns = {"/main", "/login/*", "/about"}
		)

public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = requestURI.substring(contextPath.length());
		
		HttpSession session = request.getSession(false);
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
		} else if (actionPath.equals("/main")) {
			request.setAttribute("contentPage", "/WEB-INF/views/about/about.jsp");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
			rd.forward(request, response);
		} else if (actionPath.equals("/login/logout")) {
			if (session != null) {
				session.invalidate();
			}
			response.sendRedirect(contextPath + "/login");
			return;
		} else if (actionPath.equals("/login/passwordReset")) {
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/identityVerification.jsp");
			rd.forward(request, response);
		} else if (actionPath.equals("/login/resetPassword")) {
			try {
				LoginService ls = LoginService.getInstance();
			    String token = (String) request.getParameter("token");

			    if (token == null || token.length() != 32) {
			    	session.setAttribute("errorMessage", "올바른 방식으로 접근해주세요.");
					response.sendRedirect(contextPath + "/error?errorCode=400");
			    }

			    // DB 검증 후 진행
			    String hashToken = HashUtil.sha256(token);
			    Long userId = ls.getUserIdByToken(hashToken);
			    
			    // 임시 비밀번호 생성
			    String tempPassword = PasswordUtil.generateTempPassword();

			    // 임시 비밀번호 DB 반영 및 계정 ACTIVE로 변경
			    ls.issueTempPassword(userId, HashUtil.sha256(tempPassword));
			    ls.setLoginStatusACTIVE(userId);
			    
			    // 토큰 사용만료 처리
			    ls.markTokenAsUsed(hashToken);
			    
			    // 사용자 전달용
			    request.setAttribute("tempPassword", tempPassword);
			    
			    RequestDispatcher rd =
			        request.getRequestDispatcher("/WEB-INF/views/login/resetResultPassword.jsp");
			    rd.forward(request, response);
			} catch (Exception e) {
				session.setAttribute("errorMessage", e.getMessage());
				response.sendRedirect(contextPath + "/error?errorCode=400");
			}
		} else if (actionPath.equals("/about")) {
			request.setAttribute("contentPage", "/WEB-INF/views/about/about.jsp");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
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
			
			// 실무 bCrypt 등 타 암호화 로직 사용 필요
			user_passwd = HashUtil.sha256(user_passwd);
			
			LoginService ls = LoginService.getInstance();
			
			LoginResultDTO loginResultDTO = ls.DoLogin(user_id, user_passwd); 
			AccessDTO accessDTO = loginResultDTO.getAccessDTO();
			
			if (loginResultDTO.getLoginStatus() == LoginStatus.SUCCESS ||
				loginResultDTO.getLoginStatus() == LoginStatus.SUCCESS_MPWC) {
				session = request.getSession();
				session.setAttribute("AccessInfo", accessDTO);
				// 조회 기준 MyPageService용(로그인동안 loginId값 기억 myPage관련 로직을 사용하기위해서 필요)
				session.setAttribute("loginId", user_id);
				// 로그인 동안 user_id(PK)가져옴
				session.setAttribute("userId", accessDTO.getUserId());
				if (loginResultDTO.getLoginStatus() == LoginStatus.SUCCESS_MPWC) {
					session.setAttribute("MPWC", true);
				}
				response.sendRedirect(contextPath + "/main");
			} else if (loginResultDTO.getLoginStatus() == LoginStatus.FAIL) {
				request.setAttribute("LoginErrorMsg", "로그인 정보가 맞지 않습니다.");
				request.setAttribute("contentPage", "/WEB-INF/views/login/login.jsp");
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/index.jsp");
				rd.forward(request, response);
			} else if (loginResultDTO.getLoginStatus() == LoginStatus.INACTIVE) {
				request.setAttribute("LoginErrorMsg", "비활성화 계정입니다. 관리자에게 문의해주세요.");
				request.setAttribute("contentPage", "/WEB-INF/views/login/login.jsp");
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/index.jsp");
				rd.forward(request, response);
			} else if (loginResultDTO.getLoginStatus() == LoginStatus.LOCKED) {
				request.setAttribute("LoginErrorMsg", "잠금 처리된 계정입니다. 비밀번호 초기화 해주세요.");
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
		    LoginService ls = LoginService.getInstance();
		    boolean isMatch = ls.verifyUserInfo(email, birthDate);
		    
		    // 세션 저장(최종적으로 세션에서 제거해야함)
		    session.setAttribute("tokenType", "PasswordReset");
		    
		    // 4. JSON 응답 설정
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    String json = "{\"match\":" + isMatch + "}";
		    response.getWriter().write(json);
		} else if (action.equals("/create-token")) {
			LoginService ls = LoginService.getInstance();
			String email = request.getParameter("email");
			String birthDate = request.getParameter("birthDate");
		    Long userId = ls.getUserId(email, birthDate);
		    String token_type = (String) session.getAttribute("tokenType");
		    String message = "";
		    if (userId == null || userId == 0) {
		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        return;
		    }
		    String token;
		    UserStatus userStatus = ls.getloginStatus(userId);
		    
		    // 계정이 비활성화 상태라면 토큰 미발급
		    if (userStatus == UserStatus.INACTIVE) {
		    	message = "계정 비활성화";
		    } else {
		    	token = ls.getPlainToken(userId, token_type, request.getRemoteAddr());
		    	// 토큰 생성하고 메일전송
			    String resetLink =
		                request.getScheme() + "://" +
		                request.getServerName() + ":" +
		                request.getServerPort() +
		                request.getContextPath() +
		                "/login/resetPassword?token=" + token;

		        // 5️. HTML 메일 내용
		        String subject = "[가산구디대학교] 비밀번호 재설정 안내";
		        String content =
		        		"<html><body>" +
	    			    "<h2>비밀번호 재설정</h2>" +
	    			    "<p>" + ls.getLoginIdByUserId(userId) + "님 안녕하세요.</p>" +
	    			    "<p>아래 버튼을 클릭하여 비밀번호를 재설정하세요.</p>" +
	    			    "<p style='margin-top:20px;'>" +
	    			    "<a href='" + resetLink + "' " +
	    			    "style='display:inline-block;padding:10px 20px;background:#0d6efd;color:white;" +
	    			    "text-decoration:none;border-radius:5px;'>비밀번호 재설정</a>" +
	    			    "</p>" +
	    			    "<p style='margin-top:20px;'>이 링크는 10분 동안만 유효합니다.</p>" +
	    			    "</body></html>";
		        
		        MailSender.sendMail(email, subject, content);
		        
		        // 세션 속성 제거
		        session.removeAttribute("tokenType");
		        
		        // 6. JSON 응답 설정
		        boolean generateTokenCheck;
		        if (token == null) {
		        	generateTokenCheck = false;
		        } else {
		        	generateTokenCheck = true;
		        }
		        
			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    String json = "{\"status\":" + generateTokenCheck + ",\"message\":\"" + message + "\"}";
			    response.getWriter().write(json);
		    }
		} 
	}

}
