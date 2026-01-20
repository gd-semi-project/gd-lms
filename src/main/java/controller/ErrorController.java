package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.LoginService;
import utils.HashUtil;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(
		urlPatterns = {"/error"}
		)

public class ErrorController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = requestURI.substring(contextPath.length());
		
		HttpSession session = request.getSession(false);
		
		String errorCode = (String) request.getAttribute("errorCode");
		String errorMessage = (String) request.getAttribute("errorMessage");
		// 에러코드와 에러메시지는 각 서비스에서 redirect 발생
		if (errorCode == null && errorMessage == null) {
			// 에러코드나 에러메시지 없이 페이지 직접 접근시 별도 에러내용 발생
		} else if (errorCode.equals("404")) {
			
		} else if (errorCode.equals("403")) {
			
		} else if (errorCode.equals("500")) {
			
		} else if (errorCode.equals("404")) {
			
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
