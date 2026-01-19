package controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 임시용 로그인 가정 컨트롤러
@WebServlet("/dev/login")
public class DevLoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    	
    	
        // 기본 ADMIN (ADMIN/INSTRUCTOR/STUDENT)
        String role = req.getParameter("role");
        if (role == null || role.isBlank()) role = "ADMIN";

        // 기본 userId=1
        Long userId = 1L;
        String userIdParam = req.getParameter("userId");
        try {
            if (userIdParam != null && !userIdParam.isBlank()) {
                userId = Long.parseLong(userIdParam);
            }
        } catch (Exception ignored) {}

        HttpSession session = req.getSession(true);
        session.setAttribute("userId", userId);
        session.setAttribute("role", role);
        session.setAttribute("userName", "DEV-" + role);

        // 공지사항으로 이동
        resp.sendRedirect(req.getContextPath() + "/instructor/profile");
    }
}
