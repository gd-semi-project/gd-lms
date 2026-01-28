package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.AccessDTO;
import model.dto.MypageDTO;
import model.dto.StudentDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import service.MyPageService;
import utils.HashUtil;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/changeUserPw/*")
public class changeUserPwController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private MyPageService myPageService = new MyPageService();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String ctx = request.getContextPath();
		HttpSession session = request.getSession(false);
        if (session == null) {
        	session = request.getSession();
        	session.setAttribute("errorMessage", "로그인이 필요합니다.");
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;
        }

        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
        if (access == null) {
        	session.setAttribute("errorMessage", "로그인이 필요합니다.");
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;
        }
        // loginId 확보
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) {
            // 세션 꼬임 방어
        	session.invalidate();
            session = request.getSession();
            session.setAttribute("errorMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;
        }
		// ROLE이 STUDENT인경우(학생)만 접근가능
//		if (access.getRole() != Role.STUDENT && access.getRole() != Role.ADMIN) {
//			session.setAttribute("errorMessage", "접근 권한이 없습니다.");
//            response.sendRedirect(ctx + "/error?errorCode=403");
//			return;
//		}
		
        String action = request.getPathInfo();
		if ("/change".equals(action)) {
			
			MypageDTO mypage = myPageService.getMypageDTO(loginId);
			if (mypage == null) {
				response.sendRedirect(request.getContextPath() + "/login");
				return;
			}

			request.setAttribute("mypage", mypage);

			request.setAttribute("contentPage", "/WEB-INF/views/student/changeAccount.jsp");

			request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp").forward(request, response);

			return;

        }else {
            session.setAttribute("errorMessage", "잘못된 접근입니다.");
            response.sendRedirect(ctx + "/error?errorCode=404");
            return;
        }
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String ctx = request.getContextPath();
		
        if (session == null) {
        	response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO access = (AccessDTO) session.getAttribute("AccessInfo");
        if (access == null) {
        	response.sendRedirect(ctx + "/login");
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
//        if (access.getRole() != Role.STUDENT) {
//        	session.setAttribute("errorMessage", "접근 권한이 없습니다.");
//            response.sendRedirect(ctx + "/error?errorCode=403");
//	        return;
//	    }
        
        String sessionLoginId = (String) session.getAttribute("loginId");
        String inputLoginId = request.getParameter("inputLoginId");
        String currentPw = HashUtil.sha256(request.getParameter("Pw"));
        String newPw = HashUtil.sha256(request.getParameter("newPw"));
        String confirmPw = HashUtil.sha256(request.getParameter("confirmPw"));
        
        // 아무것도 입력안했을때
        if (inputLoginId == null || currentPw == null ||
                newPw == null || confirmPw == null ||
                		inputLoginId.isBlank() || currentPw.isBlank() ||
                newPw.isBlank() || confirmPw.isBlank()) {

                request.setAttribute("error", "모든 항목을 입력하시오");
                doGet(request, response);
                return;
            }
        
		// 비밀번호가 일치하지 않을때
        if (!newPw.equals(confirmPw)) {
            request.setAttribute("error", "비밀번호가 일치하지 않습니다");
            doGet(request, response);
            return;
        }
		
        // 아이디 일치 여부 검증
        if (!sessionLoginId.equals(inputLoginId)) {
            request.setAttribute("error", "현재 로그인한 아이디와 일치하지 않습니다.");
            doGet(request, response);
            return;
        }
        
		
		// 비밀번호 검증
		boolean validPw = myPageService.checkCurrentPassword(loginId, currentPw);
		if (!validPw) {
		    request.setAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
		    doGet(request, response);
		    return;
		}
		try {
			// 비밀번호 변경
			myPageService.changePassword(loginId, confirmPw);
			
			// 성공시 메시지
			session.setAttribute("alertMsg", "비밀번호 변경 성공! 다시 로그인해주세요.");
			
			doGet(request, response);
			return;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// 실패 메시지
			session.setAttribute("error", "비밀번호 변경에 실패하였습니다.");
			
			doGet(request, response);
			return;
		}
	}

}
