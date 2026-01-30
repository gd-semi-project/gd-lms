package keronBall;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.AppTime;

import java.io.IOException;
import java.time.LocalDateTime;

import automation.AppScheduleListener;

@WebServlet("/keronBall/*")
public class KeronBallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		KeronBallService keronBallService = KeronBallService.getInstance();
		
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/keronBall".length());
		
		if (actionPath.isEmpty()) actionPath = "/";
		
		String contentPage = "";
		
		switch(actionPath) {
		
		case "/remote":
			contentPage = "/WEB-INF/keronBall/keronBallModal.jsp";
			break;
		
		case "/time":
			contentPage = "/WEB-INF/keronBall/timeControlPanel.jsp";
			break;
			
		case "/db":
			contentPage = "/WEB-INF/keronBall/dbControlPanel.jsp";
			break;
			
		case "/updateDB":
			contentPage = "/WEB-INF/keronBall/dbUpdatePanel.jsp";
			request.setAttribute("tableNames", keronBallService.getAllTables());
			break;
			
		default:
			break;
		}
		
		request.setAttribute("contentPage", contentPage);
		
		
		RequestDispatcher rd = request.getRequestDispatcher(contentPage);
		rd.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/keronBall".length());
		String action = request.getParameter("action");
		
		switch(actionPath) {
		
		case "/timeApply": {
			String keronYear = request.getParameter("year");
			String keronMonth = request.getParameter("month");
			String keronDay = request.getParameter("day");
			String keronHour = request.getParameter("hour");
			String keronMinute = request.getParameter("minute");
			
			if (
					keronYear == null ||
					keronMonth == null ||
					keronDay == null ||
					keronHour == null ||
					keronMinute == null ||
					keronYear.isBlank() ||
					keronMonth.isBlank() ||
					keronDay.isBlank() ||
					keronHour.isBlank() ||
					keronMinute.isBlank()
					) {
				System.out.println("케론볼 error: 시간을 모두 선택해주세요.");
				break;
			}
			
			int year, month, day, hour, minute;
			
			try {
		       year   = Integer.parseInt(keronYear);
		       month  = Integer.parseInt(keronMonth);
		       day    = Integer.parseInt(keronDay);
		       hour   = Integer.parseInt(keronHour);
		       minute = Integer.parseInt(keronMinute);
			
			} catch (NumberFormatException e) {
				System.out.println("케론볼 error: 시간 값 형식이 올바르지 않습니다. 근데 이 에러는 진짜 생길 일 없음");
				break;
			}
			
			LocalDateTime target;
			try {
				target = LocalDateTime.of(year, month, day, hour, minute, 0);
			} catch (Exception e) {
				System.out.println("케론볼 error: 존재하지 않는 날짜/시간입니다.");
				break;
			}
			
			LocalDateTime real = LocalDateTime.now();
			
			long offsetSeconds = java.time.Duration.between(real, target).getSeconds();
			AppTime.setKeronBallSeconds(offsetSeconds);
			
			getServletContext().setAttribute("isKeronTime", Boolean.TRUE);
			
			response.sendRedirect(contextPath + "/keronBall/time");
			break;
			
		}
		
		case "/restoreTime":{
			AppTime.setReal();
			
			getServletContext().setAttribute("isKeronTime", Boolean.FALSE);

			response.sendRedirect(contextPath + "/keronBall/time");
			break;
		}
		
		
		case "/forceTick": {
			AppScheduleListener listner = AppScheduleListener.getInstance();
			
			if(listner == null) {
				System.out.println("Scheduler not initialized");
				return;
			}
			
			
			listner.forceTick();
			response.sendRedirect(contextPath + "/keronBall/time");
			return;
		}
		
		
		
		case "/db": {
			if("CREATEALL".equals(action)) {
				
				KeronBallService.getInstance().createAllDB(request);
				
				response.sendRedirect(contextPath + "/keronBall/db");
				break;
			} if("DELETEALL".equals(action)) {
				
				KeronBallService.getInstance().deleteAllDB();
				
				response.sendRedirect(contextPath + "/keronBall/db");
				break;
			} else {
				response.sendRedirect(contextPath + "/keronBall/db");
				break;
			}
		}
		default: break;
		}
	}

}
