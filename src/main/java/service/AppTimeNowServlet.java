package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.AppDateTime;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/appTime.now")
public class AppTimeNowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		
		LocalDateTime now = AppDateTime.now();
		String nowStr = now.format(FMT);
		
        Object flag = getServletContext().getAttribute("isKeronTime");
        boolean isKeronTime = (flag instanceof Boolean) ? (Boolean) flag : false;
		
        response.getWriter().write(
                "{"
              + "\"now\":\"" + nowStr + "\","
              + "\"isKeronTime\":" + isKeronTime
              + "}"
            );
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
