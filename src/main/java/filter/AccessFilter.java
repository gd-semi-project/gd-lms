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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mysql.cj.Session;


// @WebFilter("/AccessFilter")
public class AccessFilter extends HttpFilter {
	private static final List<String> whiteList = Arrays.asList(
			"",
		    "/login",
		    "/login/login.do",
		    "resources",
		    "error",
		    "appTime.now"
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
		String middlePath = "";
		if (actionPath.split("/").length >= 2) {
			middlePath = actionPath.split("/")[1];
		}
		
		
		// 세션이 없고, 접근 경로가 화이트리스트라면 모두 통과
		if (session == null) {
			if(!middlePath.contains("appTime") && !actionPath.contains("appTime")) {
				System.out.println("웹필터: " + actionPath);
				System.out.println("웹필터: " + middlePath);
			}
			if (whiteList.contains(middlePath) || whiteList.contains(actionPath)) {
				if(!middlePath.contains("appTime") && !actionPath.contains("appTime")) {
					System.out.println("웹필터) 화이트리스트 통과");
				}
				chain.doFilter(request, response);
				return;
			} else {
				// 세션이 없는 상태에서 화이트리스트 외 페이지 접근시
				System.out.println("웹필터) 로그인 후 접속해주세요.");
				response.sendRedirect(contextPath + "/");
				return;
			}
		}else if (session != null) {
			AccessDTO accessDTO = (AccessDTO) session.getAttribute("AccessInfo");
			if (middlePath.equals("admin")) {
				if (accessDTO.getRole() == Role.ADMIN) {
					chain.doFilter(request, response);
					System.out.println("1");
				} else {
					System.out.println("웹필터) 비인가 접근입니다.");
					response.sendRedirect(contextPath + "/main");
					return;
				}
			}else if (middlePath.equals("instructor")) {
				if (accessDTO.getRole() == Role.INSTRUCTOR) {
					chain.doFilter(request, response);
				} else {
					System.out.println("웹필터) 비인가 접근입니다.");
					response.sendRedirect(contextPath + "/main");
					return;
				}
			} else {
				chain.doFilter(request, response);
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
