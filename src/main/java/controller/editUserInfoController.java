package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.dto.MypageDTO;
import model.dto.StudentsDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.MyPageService;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/editUserInfoController/*")
public class editUserInfoController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private MyPageService myPageService = new MyPageService();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
        if (access == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // loginId 확보
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) {
            // 세션 꼬임 방어
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String action = request.getPathInfo();
        if ("/edit".equals(action)) {	
			// ROLE이 STUDENT인경우(학생)만 접근가능
			if (access.getRole() != Role.STUDENT) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			MypageDTO mypage = myPageService.getMypageDTO(loginId);
			if (mypage == null) {
				response.sendRedirect(request.getContextPath() + "/login");
				return;
			}

			request.setAttribute("mypage", mypage);

			request.setAttribute("contentPage", "/WEB-INF/views/student/editInfo.jsp");

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

			return;

        }
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
        if (access == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // loginId 확보
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) {
            // 세션 꼬임 방어
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (access.getRole() != Role.STUDENT) {
	        response.sendError(HttpServletResponse.SC_FORBIDDEN);
	        return;
	    }
        
        UserDTO user = new UserDTO();
        // 생년월일값이 null일경우 에러 방지
        String birthDateParam = request.getParameter("birthDate");
        if (birthDateParam != null && !birthDateParam.isBlank()) {
            user.setBirthDate(LocalDate.parse(birthDateParam));
        }
        user.setLoginId(loginId);
        user.setBirthDate(LocalDate.parse(request.getParameter("birthDate")));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        user.setAddress(request.getParameter("address"));

        // StudentsDTO
        StudentsDTO student = new StudentsDTO();
        student.setTuitionAccount(request.getParameter("tuitionAccount"));
        
        myPageService.updateStudentInfo(loginId, user, student);
        
        response.sendRedirect(request.getContextPath() + "/mypage/studentPage");
	}

}
