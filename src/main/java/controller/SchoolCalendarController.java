package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.SchoolCalendarDTO;
import service.SchoolCalendarService;

import java.io.IOException;

@WebServlet("/calendar/*")
public class SchoolCalendarController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());
		String actionPath = command.substring("/calendar".length());
		
		if (actionPath.isEmpty()) actionPath = "/";
		
		String contentPage = "";
		
		switch(actionPath) {
		
		case "/view":
			contentPage = "/WEB-INF/views/calendar/calendar.jsp";
			SchoolCalendarService calendarService = SchoolCalendarService.getInstance();
			
			SchoolCalendarDTO calendar = calendarService.getCalendar(request.getParameter("year"),request.getParameter("month"));
			
			request.setAttribute("calendar", calendar);
			
			request.setAttribute("selectedYear", calendar.getSelectedYear());
			request.setAttribute("selectedMonth", calendar.getSelectedMonth());
			request.setAttribute("displayMonth", calendar.getDisplayMonth());
			request.setAttribute("prevYear", calendar.getPrevYear());
			request.setAttribute("prevMonth", calendar.getPrevMonth());
			request.setAttribute("nextYear", calendar.getNextYear());
			request.setAttribute("nextMonth", calendar.getNextMonth());
			request.setAttribute("weeks", calendar.getWeeks());
			request.setAttribute("eventList", calendar.getEventList());
			request.setAttribute("monthCount", calendar.getMonthCount());
			break;
			
		default:
			break;
			
		}
		
		request.setAttribute("contentPage", contentPage);
		
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/layout/layout.jsp");
		rd.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
