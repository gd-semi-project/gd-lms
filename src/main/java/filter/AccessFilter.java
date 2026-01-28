package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.enumtype.Role;
import service.AdminService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// @WebFilter("/AccessFilter")
public class AccessFilter extends HttpFilter {
	private static final long serialVersionUID = 1L;
	
	// 세션이 없더라도 접근 가능한 페이지
	private static final List<String> whiteList = Arrays.asList(
		    "/login",
		    "/login/login.do",
		    "/login/passwordReset",
		    "/login/check-info",
		    "/login/get-user-id",
		    "/login/create-token",
		    "/login/resetPassword",
		    "/login/passwordReset",
		    "/login/resetPasswordForm",
		    "resources",
		    "error",
		    "appTime.now",
		    "keronBall"
		);

	private String encoding = "UTF-8"; // 기본 인코딩 설정

	public void destroy() {
	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		// Context Path 값
		request.setAttribute("ctx", request.getContextPath());
		
		// 요청과 응답에 인코딩 설정
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);

		// 세션 여부 확인
		HttpSession session = request.getSession(false);
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = uri.substring(contextPath.length());
		String middlePath = "";
		if (actionPath.split("/").length >= 2) {
			middlePath = actionPath.split("/")[1];
		}
		
		// 중복 슬래시 제거로직
		String path = uri.substring(contextPath.length());
		path = path.replaceAll("/{2,}", "/");
		if (path.length() > 1) {
		    path = path.replaceAll("/+$", "");
		}
		String cleanedUri = contextPath + path;

		if (!uri.equals(cleanedUri)) {
		    response.sendRedirect(cleanedUri);
		    return;
		}
		
		// 화이트리스트 페이지는 항상 접근 가능(세션유무상관없이)
		if (whiteList.contains(middlePath) || whiteList.contains(actionPath)) {
			chain.doFilter(request, response);
			return;
		}
		
		// 세션 없으면 login 창으로 이동
		if (session == null) {
	        response.sendRedirect(contextPath + "/login");
	        return;
		}
		
		// 루트디렉터리 접근시 login 페이지 이동
		if (uri.equals(contextPath) || uri.equals(contextPath+"/")) {
			response.sendRedirect(contextPath + "/login");
			return;
		}
		
		if (session.getAttribute("AccessInfo") == null) {
			if (uri.equals(contextPath + "/main")) {
				response.sendRedirect(contextPath + "/login");
				return;
			} else {
				session.setAttribute("errorMessage", "로그인 후 이용가능합니다.");
				response.sendRedirect(contextPath + "/error?errorCode=401");
				return;
			}
		}
		
		// 비밀번호 변경 대상 확인 후 페이지 강제이동
		if (session.getAttribute("MPWC") != null) {
//			boolean MPWC = (boolean)session.getAttribute("MPWC");
//			if (MPWC == true &&
//					!actionPath.equals("/changeUserPw/change") &&
//					!actionPath.equals("/login/logout")) {
//				response.sendRedirect(contextPath + "/changeUserPw/change");
//				return;
//			}
		}
		
		// role 권한 체크
		AccessDTO accessDTO = (AccessDTO) session.getAttribute("AccessInfo");
		
		if (accessDTO != null && accessDTO.getRole() == Role.ADMIN) { // 백시현 추가 관리자 알림용
			try {
				request.setAttribute("pendingInfoUpdateCount", AdminService.getInstance().getPendingStudentInfoUpdateCount());
			} catch (Exception ignore) {}
		}
		
		if (middlePath.equals("admin")) {
			if (accessDTO.getRole() == Role.ADMIN) {
				chain.doFilter(request, response);
			} else {
				session.setAttribute("errorMessage", "관리자만 접근 가능한 페이지입니다.");
				response.sendRedirect(contextPath + "/error?errorCode=403");
			}
		} else if (middlePath.equals("instructor")) {
			if (accessDTO.getRole() == Role.INSTRUCTOR || accessDTO.getRole() == Role.ADMIN) {
				chain.doFilter(request, response);
			} else {
				session.setAttribute("errorMessage", "교수만 접근 가능한 페이지입니다.");
				response.sendRedirect(contextPath + "/error?errorCode=403");
			}
		} else {
			// 그외 페이지는 허용
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("웹 필터를 초기화합니다.");
		String encodingParam = fConfig.getInitParameter("encoding");
		if (encodingParam != null) {
			encoding = encodingParam;
		}
	}

}