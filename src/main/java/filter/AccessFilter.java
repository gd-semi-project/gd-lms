package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import java.io.IOException;

@WebFilter("/AccessFilter")
public class AccessFilter extends HttpFilter implements Filter {
	private String session_id;
       
    public AccessFilter() {
        super();
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 1. 인코딩 필터
		// 2. 로그인 확인
		// 3. role 확인
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// 1. 세션에서 로그인정보 불러와서 저장해놓기
	}

}
