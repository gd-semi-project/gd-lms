package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.UserDTO;
import model.enumtype.Role;

import java.io.IOException;

// @WebFilter("/AccessFilter")
public class AccessFilter extends HttpFilter implements Filter {
	private String encoding = "UTF-8"; // 기본 인코딩 설정

    public AccessFilter() {
        super();
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 요청과 응답에 인코딩 설정
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		
		String uri = req.getRequestURI();
		
		if (uri.endsWith("index_goheekwon.jsp") || uri.endsWith("login.do")) {
			chain.doFilter(request, response); // forward 대신 필터 체인 통과
		    return;
	    }
		
		UserDTO session_id;
		if (session.getId() != null) {
			session_id = (UserDTO) session.getAttribute("UserInfo");
			// 2. 로그인 확인
			if (session_id.getLogin_id() == null) {
				res.sendRedirect(req.getContextPath() + "/index_goheekwon.jsp");
			}

			// 3. role 별도 필터에서 확인? 아니면 필터1개에서 uri값 확인해서 처리?
			if (session_id.getRole() != null) {
				Role role = session_id.getRole(); 
				
				if (uri.contains("/admin/")) {
		            if (!Role.ADMIN.equals(role)) {
		                res.sendRedirect(req.getContextPath() + "/accessDenied.jsp");
		                return;
		            }
		        }

		        if (uri.contains("/instructor/")) {
		            if (!Role.INSTRUCTOR.equals(role) && !Role.ADMIN.equals(role)) {
		                res.sendRedirect(req.getContextPath() + "/accessDenied.jsp");
		                return;
		            }
		        }
			}
		}
		chain.doFilter(request, response); // forward 대신 필터 체인 통과
	    return;
	}

	public void init(FilterConfig fConfig) throws ServletException {
		String encodingParam = fConfig.getInitParameter("encoding");
        if (encodingParam != null) {
            encoding = encodingParam;
        }

	}

}
