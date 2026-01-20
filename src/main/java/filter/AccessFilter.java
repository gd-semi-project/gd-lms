package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mysql.cj.Session;

// @WebFilter("/AccessFilter")
public class AccessFilter extends HttpFilter {
	private static final List<String> whiteList = Arrays.asList(
		    "/",
		    "/login",
		    "/login/login.do",
		    ""
		);

	private String encoding = "UTF-8"; // 기본 인코딩 설정

	public void destroy() {
	}
	
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 요청과 응답에 인코딩 설정
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        
        // 세션 여부 확인
		HttpSession session = request.getSession(false);
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = uri.substring(contextPath.length()); 
		
		// 세션이 없고, 접근 경로가 화이트리스트라면 모두 통과
		if (session == null) {
			if (whiteList.contains(actionPath)) {
				System.out.println("웹필터) 화이트리스트 통과");
				chain.doFilter(request, response);
				return;
			} else {
				System.out.println("로그인 하지 않고 접근을 시도했습니다. 접근페이지: " + uri);
				// 에러페이지 연결
				return;
			}
		}
		
		chain.doFilter(request, response);
		
		// 세션이 있으면 로그인 성공한 것 이후 role체크 추가
		

		/*
		if (uri.equals(request.getContextPath() + "/login/login.do")) {
			System.out.println("웹필터PAGE) login.do 웹필터 통과");
			chain.doFilter(request, response);
			return;
	    }

		if (uri.equals(request.getContextPath() + "/index.jsp") || uri.equals(request.getContextPath() + "/")) {
			if (session != null) {
				if (session.getAttribute("UserInfo") == null) {
					response.sendRedirect(request.getContextPath() + "/login/login.do");
				} else {
					chain.doFilter(request, response);
				}
			} else {
				chain.doFilter(request, response);
			}			
	    }
		*/
	}
	

	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("웹 필터를 초기화합니다.");
		String encodingParam = fConfig.getInitParameter("encoding");
        if (encodingParam != null) {
            encoding = encodingParam;
        }

	}

}
