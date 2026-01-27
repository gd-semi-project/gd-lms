package controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.dao.StudentDAO;
import model.dto.AccessDTO;
import model.dto.LectureDTO;
import model.dto.MypageDTO;
import model.dto.MyLectureDTO;
import model.enumtype.Role;
import service.AdminService;
import service.LectureService;
import service.MyPageService;

@WebServlet("/student/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,       // 1MB (메모리에 잠깐 쌓이다가)
        maxFileSize = 10 * 1024 * 1024,        // 10MB per file
        maxRequestSize = 50 * 1024 * 1024      // 50MB total
		)
public class StudentController extends HttpServlet {

    private final LectureService lectureService =
        LectureService.getInstance();
    private AdminService adminService = AdminService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        HttpSession session = request.getSession(false);

        // 로그인 권한 체크
        if (session == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        AccessDTO access =
            (AccessDTO) session.getAttribute("AccessInfo");

        if (access == null || access.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long userId = access.getUserId();
        if (userId == null) {
            session.invalidate();
            session = request.getSession();
            session.setAttribute("errorMessage", "세션 정보가 유효하지 않습니다.");
            response.sendRedirect(ctx + "/error?errorCode=401");
            return;
        }

        /* ======================
         *  URL 분기
         * ====================== */
        String uri = request.getRequestURI();
        String action =
            uri.substring(ctx.length() + "/student".length());

        // 기본 페이지 → 내 강의 목록
        if (action == null || action.isBlank()) {
            action = "/lectures";
        }

        switch (action) {

        // 학생 내 강의 목록
        case "/lectures": {

            List<LectureDTO> lectures =
                lectureService.getMyLectures(access, null);

            request.setAttribute("lectures", lectures);
            request.setAttribute("status", "ONGOING");
            request.setAttribute("activeMenu", "lectures");
            request.setAttribute(
                "contentPage",
                "/WEB-INF/views/lecture/lectureList.jsp"
            );
            break;
        }
        // 학생 내 종강한 강의 목록 
        case "/lectures/ended" : {
        	List<MyLectureDTO> lectures =
        	        lectureService.getMyEndedLectures(
        	            access.getUserId()
        	        );
        		request.setAttribute("status", "ENDED");
        	    request.setAttribute("lectures", lectures);
        	    request.setAttribute("activeMenu", "lectures");
        	    request.setAttribute(
        	        "contentPage",
        	        "/WEB-INF/views/lecture/lectureList.jsp"
        	    );
        	    break;
        }
            
                //학생민감정보수정
        case "/updateInfo": {
        	MyPageService myPageService = new MyPageService();
        	String loginId = (String) session.getAttribute("loginId");
        	MypageDTO mypage = myPageService.getMypageDTO(loginId);
          if (mypage == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
          }
          request.setAttribute("mypage", mypage);
          request.setAttribute("departments", adminService.getDepartmentList());
          request.setAttribute("contentPage", "/WEB-INF/views/student/requestInfoUpdate.jsp");
          break;
        }
    
            
            
        default:
        	 session.setAttribute("errorMessage", "존재하지 않는 요청입니다.");
        	    response.sendRedirect(ctx + "/error?errorCode=404");
        	    return;
        }

        request.getRequestDispatcher(
            "/WEB-INF/views/layout/layout.jsp"
        ).forward(request, response);
    }
    
    
    
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/student".length());
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		switch(actionPath) {
		
			case "/requestInfoUpdate": {
				String loginId = (String) session.getAttribute("loginId");
				System.out.println(session.getAttribute("loginId"));
				if (loginId == null) {
					response.sendRedirect(request.getContextPath()+"/main");
					return;
				}
				
				Map<String, Part> parts = new LinkedHashMap<String, Part>();
		        parts.put("docName", getPartSafe(request, "docName"));
		        parts.put("docGender", getPartSafe(request, "docGender"));
		        parts.put("docAccountNo", getPartSafe(request, "docAccountNo"));
		        parts.put("docDepartment", getPartSafe(request, "docDepartment"));
		        parts.put("docAcademicStatus", getPartSafe(request, "docAcademicStatus"));
				
		        try {
		        	
		        	Long studentId = StudentDAO.getInstance().studentIdFromLoginId(loginId);
		        	
					Long requestId = adminService.studentInfoUpdateRequest(studentId, request, parts);
		        	
					response.sendRedirect(request.getContextPath()+"/mypage/studentPage");
				} catch (Exception e) {
					e.printStackTrace();
					session.setAttribute("errorMessage", "중요 정보 수정 오류 발생");
					response.sendRedirect(request.getContextPath() + "/error?errorCode=500");
				}
		        
		        
		        
		        
			}
		}
	}
	private static Part getPartSafe(HttpServletRequest request, String name) {
	    try {
	        return request.getPart(name);
	    } catch (Exception e) {
	        return null; // 널값 들어올 경우가 많은지라
	    }
	}
}