package filter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class InputBlockFilter implements Filter {

	// jsp,EL 방어
    private static final List<Pattern> DANGEROUS = List.of(
        Pattern.compile("(?i)<\\s*script\\b"),          
        Pattern.compile("(?i)javascript\\s*:"),         
        Pattern.compile("(?i)on\\w+\\s*="),             
        Pattern.compile("\\$\\{"),                      
        Pattern.compile("#\\{"),                       
        Pattern.compile("<%"),                         
        Pattern.compile("%>")                          
    );

    // 스킵할 영역들
    private static final Set<String> CHECK_PARAMS = Set.of(
        "title", "content", "text", "keyword", "q", "search", "items"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

    	req.setCharacterEncoding("UTF-8");
    	res.setCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // /error  , / resources 스킵
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.startsWith("/error") || path.startsWith("/resources")) {
            chain.doFilter(req, res);
            return;
        }
                
        
        // 파일업로드 스킵 혹시몰라서 
        String ct = request.getContentType();
        if (ct != null && ct.toLowerCase().startsWith("multipart/")) {
            chain.doFilter(req, res);
            return;
        }

        Map<String, String[]> params = request.getParameterMap();

        for (Map.Entry<String, String[]> e : params.entrySet()) {
            String name = e.getKey();
            if (!shouldCheck(name)) continue;

            for (String v : e.getValue()) {
                if (v == null) continue;
                if (isDangerous(v)) {
                	HttpSession session = request.getSession(true);
                	session.setAttribute("errorMessage", "허용되지 않는 입력입니다.");
                	response.sendRedirect(request.getContextPath() + "/error?errorCode=403");
                	return;
                }
            }
        }

        chain.doFilter(req, res);
    }

    private boolean shouldCheck(String name) {
        if (name == null) return false;
        String n = name.toLowerCase();
        if (CHECK_PARAMS.contains(n)) return true;

        // 문자열 끝 영역 전부 포함
        return n.endsWith("title") || n.endsWith("content") || n.contains("search") || n.contains("keyword");
    }

    private boolean isDangerous(String s) {
        for (Pattern p : DANGEROUS) {
            if (p.matcher(s).find()) return true;
        }
        return false;
    }
    @Override public void init(FilterConfig filterConfig) throws ServletException {}
    @Override public void destroy() {}
}
