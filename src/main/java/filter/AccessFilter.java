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
		    "login",
		    "resources"
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
			System.out.println(middlePath);
			if (whiteList.contains(middlePath)) {
				System.out.println("웹필터) 화이트리스트 통과");
				chain.doFilter(request, response);
				return;
			} else {
				// 세션이 없는 상태에서 화이트리스트 외 페이지 접근시
				response.sendRedirect(contextPath + "/");
				return;
			}
		}
					
		// 세션이 있으면 로그인 성공한 것 이후 role체크 추가
		chain.doFilter(request, response);
		AccessDTO accessDTO =(AccessDTO) session.getAttribute("AccessInfo");
		
		if (accessDTO.getRole() == Role.ADMIN) {
			
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
