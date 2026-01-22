package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.EnrollmentService;

import java.io.IOException;

// 학생 수강신청 관련 컨트롤러
@WebServlet("/enroll/*")
public class EnrollmentController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private EnrollmentService enrollmentService = new EnrollmentService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
