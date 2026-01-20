package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder.Default;
import model.dto.MypageDTO;
import model.dto.UserDTO;
import service.MyPageService;

import java.io.IOException;


@WebServlet("/mypage/*")
public class MypageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private MyPageService myPageService = new MyPageService();
	
@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String view = request.getParameter("view");
		
		if(view == null) {
			view = "basicInfo";
		}
		String contentPage = "";
		
		switch (view) {
		case "studentPage": {
		    contentPage = "../student/studentPage.jsp";

		    String loginId =
		        (String) request.getSession().getAttribute("loginId");

		    MyPageService myPageService = new MyPageService();
		    MypageDTO mypage = myPageService.getMypageDTO(loginId);

		    request.setAttribute("mypage", mypage);
		    break;
		}
			
			default:
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
		
		}
		
		request.setAttribute("contentPage", contentPage);
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
		rd.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}