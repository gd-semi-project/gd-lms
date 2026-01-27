package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import utils.EnrollmentPeriod;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mysql.cj.Session;

// @WebFilter("/AccessFilter")
public class AccessFilter extends HttpFilter {

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

		if (session == null) {
			if (whiteList.contains(actionPath)) {
		        chain.doFilter(request, response);
		    } else {
		    	// TODO: 로그인 후 접근해주세요 에러 페이지 연결
		        response.sendRedirect(contextPath + "/login");
		    }
		}else if (session != null) {
			if (session.getAttribute("AccessInfo") != null) {
				// role 권한 체크
				AccessDTO accessDTO = (AccessDTO) session.getAttribute("AccessInfo");
				if (middlePath.equals("admin")) {
					if (accessDTO.getRole() == Role.ADMIN) {
						chain.doFilter(request, response);
					} else {
						// TODO: 권한이 부족합니다. 메인 페이지 연결
						response.sendRedirect(contextPath + "/main");
					}
				} else if (middlePath.equals("instructor")) {
					if (accessDTO.getRole() == Role.INSTRUCTOR || accessDTO.getRole() == Role.ADMIN) {
						chain.doFilter(request, response);
					} else {
						// TODO: 권한이 부족합니다. 메인 페이지 연결
						response.sendRedirect(contextPath + "/main");
					}
				} else {
					// 그외 페이지는 허용
					chain.doFilter(request, response);
				}
			} else if (session.getAttribute("AccessInfo") == null) {
				// 비로그인시 화이트리스트 페이지 접근 가능
				if (whiteList.contains(middlePath) || whiteList.contains(actionPath)) {
					chain.doFilter(request, response);
				} else {
					// TODO: 로그인 후 접근해주세요 에러 페이지 연결
					response.sendRedirect(contextPath + "/login");
				}
			}
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