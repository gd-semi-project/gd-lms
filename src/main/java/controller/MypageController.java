package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.MypageDTO;
import service.MyPageService;

import java.io.IOException;


@WebServlet("/mypage/*")
public class MypageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private MyPageService myPageService = new MyPageService();
	
@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		// 로그인 안한경우
		// 세션이 없거나 로그인 정보가 없으면 로그인 페이지로 이동함
		if(session == null || session.getAttribute("loginId") == null) {
			response.sendRedirect("/login");
			return;
		}
		
		// 로그인한 사용자의 정보를 가져오기
		
		// 로그인시 세션에 저장해두는 객체
		String loginId = (String) session.getAttribute("loginId");
		
		
			try {
				// service에서 유저관련 정보 조립
				MypageDTO mypage = myPageService.getMypageDTO(loginId);
				// 조회한 마이페이지 DTO를 request 영역에 저장
				request.setAttribute("mypage", mypage);
				
				// 마이페이지 화면 이동
				request.getRequestDispatcher("/webapp/test.jsp").forward(request, response);
			} catch (Exception e) {
				request.setAttribute("errorMessage", e.getMessage());
				request.getRequestDispatcher("에러메세지 화면").forward(request, response);
			}
		
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
