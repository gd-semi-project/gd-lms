package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.dto.UserDTO;
import model.enumtype.Role;

public class AccessFilter extends HttpFilter implements Filter {
   
    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        String encodingParam = fConfig.getInitParameter("encoding");
        if (encodingParam != null) encoding = encodingParam;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	 chain.doFilter(request, response);
    	 return;

    	 /*
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();

        // 1. 정적 리소스 및 로그인 페이지는 통과
        if (uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/")
                || uri.endsWith("index_goheekwon.jsp") || uri.endsWith("login.do")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 세션 없으면 로그인 페이지로 이동
        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/index_goheekwon.jsp");
            return;
        }

        // 3. 사용자 정보 확인
        UserDTO user = (UserDTO) session.getAttribute("UserInfo");
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/index_goheekwon.jsp");
            return;
        }

        // 4. 권한별 접근 제어
        Role role = user.getRole();
        if (uri.contains("/admin/") && !Role.ADMIN.equals(role)) {
            res.sendRedirect(req.getContextPath() + "/accessDenied.jsp");
            return;
        }
        if (uri.contains("/instructor/")
                && !(Role.INSTRUCTOR.equals(role) || Role.ADMIN.equals(role))) {
            res.sendRedirect(req.getContextPath() + "/accessDenied.jsp");
            return;
        }

        // 5. 나머지는 정상 통과
        chain.doFilter(request, response);
        */
    }

    @Override
    public void destroy() {}
}
