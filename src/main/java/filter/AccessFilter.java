package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.UserDTO;
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
		System.out.println("웹필터 실행");
		String uri = req.getRequestURI();
		if (uri.endsWith("index.jsp") || uri.endsWith("login.do") || uri.equals(req.getContextPath() + "/")) {
			System.out.println("웹필터 통과");
			chain.doFilter(request, response);
			return;
	    }
		
		UserDTO session_id;
		System.out.println("어디서 걸리냐2");
		if (session != null) {
			if (session.getId() != null) {
				session_id = (UserDTO) session.getAttribute("UserInfo");
				// 2. 로그인 확인
				if (session_id.getLogin_id() == null) {
					res.sendRedirect(req.getContextPath() + "/index.jsp");
				} else {
					System.out.println("어디서 걸리냐3");
					chain.doFilter(request, response);
				}
			}
		}

//			// 3. role 별도 필터에서 확인? 아니면 필터 1개에서 uri값 확인해서 처리?
//			if (session_id.getRole() != null) {
//				Role role = session_id.getRole(); 
//				
//				if (uri.contains("/admin/")) {
//		            if (!Role.ADMIN.equals(role)) {
//		                res.sendRedirect(req.getContextPath() + "/accessDenied.jsp");
//		                return;
//		            }
//		        }
//
//		        if (uri.contains("/instructor/")) {
//		            if (!Role.INSTRUCTOR.equals(role) && !Role.ADMIN.equals(role)) {
//		                res.sendRedirect(req.getContextPath() + "/accessDenied.jsp");
//		                return;
//		            }
//		        }
//			}
		}

	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("어디서 걸리냐1");
		String encodingParam = fConfig.getInitParameter("encoding");
        if (encodingParam != null) {
            encoding = encodingParam;
        }

	}

}
