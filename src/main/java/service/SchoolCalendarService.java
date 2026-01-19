package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import model.dao.SchoolCalendarDAO;
import model.dto.DayDTO;
import model.dto.EventSummaryDTO;
import model.dto.SchoolCalendarDTO;
import model.dto.SchoolScheduleDTO;
import utils.AppDate;

public class SchoolCalendarService {
    private static final SchoolCalendarService instance = new SchoolCalendarService();
    private final SchoolCalendarDAO dao = SchoolCalendarDAO.getInstance();

    private SchoolCalendarService() {}

    public static SchoolCalendarService getInstance() {
        return instance;
    }
    
    public SchoolCalendarDTO getCalendar(String year, String month) {
    	YearMonth ym  = resolveYearMonth(year, month);
    	
    	LocalDate monthStart = ym.atDay(1);
    	LocalDate monthEnd = ym.atEndOfMonth();
    	
    	
    	List<SchoolScheduleDTO> monthEvents = dao.selectByRange(monthStart, monthEnd);
    	
    	SchoolCalendarDTO calendar = new SchoolCalendarDTO();
    	calendar.setSelectedYear(ym.getYear());
    	calendar.setSelectedMonth(ym.getMonthValue());
    	calendar.setDisplayMonth(ym.getYear() + "년 " + ym.getMonthValue() + "월");
    	
    	YearMonth prev = ym.minusMonths(1);
    	YearMonth next = ym.plusMonths(1);
    	
    	calendar.setPrevYear(prev.getYear());
    	calendar.setPrevMonth(prev.getMonthValue());
    	calendar.setNextYear(next.getYear());
    	calendar.setNextMonth(next.getMonthValue());
    	
    	calendar.setEventList(monthEvents);
    	calendar.setWeeks(buildWeeks(ym, monthEvents));
    	
    	return calendar;
    	
    	
    	
    }

	private YearMonth resolveYearMonth(String year, String month) {
		
		int y;
		int m;
		
		if (year == null || year.isBlank()) {
			y = LocalDate.now().getYear();
		} else {
			y = Integer.parseInt(year);
		}
		if (month == null || month.isBlank()) {
			m = LocalDate.now().getYear();
		} else {
			m = Integer.parseInt(month);
		}
		
		if (m < 1) {
			y--;
			m = 12;
		} else if (m > 12) {
			y++;
			m = 1;
		}
		return YearMonth.of(y,m);
	}
    
    
    private List<List<DayDTO>> buildWeeks(YearMonth ym, List<SchoolScheduleDTO> events){
    	LocalDate firstDay = ym.atDay(1);
    	LocalDate lastDay = ym.atEndOfMonth();
    	
    	LocalDate start = firstDay;
    	while (start.getDayOfWeek() != DayOfWeek.SUNDAY) {
    		start = start.minusDays(1);
    	}
    	
    	LocalDate end = lastDay;
    	while (end.getDayOfWeek() != DayOfWeek.SATURDAY) {
    		end = end.plusDays(1);
    	}
    	
    	List<List<DayDTO>> weeks = new ArrayList<List<DayDTO>>();
    	LocalDate today = AppDate.now();
    	LocalDate cursor = start;
    	
    	while (!cursor.isAfter(end)) {
    		List<DayDTO> week = new ArrayList<DayDTO>();
    		
    		for (int i = 0; i < 7; i ++) {
    			
    			DayDTO day = new DayDTO();
    			day.setDate(cursor);
    			day.setDayNumber(cursor.getDayOfMonth());
    			day.setInCurrentMonth(cursor.getMonthValue() == ym.getMonthValue());
    			day.setToday(cursor.equals(today));
    			
    			List<EventSummaryDTO> summaries = new ArrayList<EventSummaryDTO>();
    			for (SchoolScheduleDTO e : events) {
    				if(!cursor.isBefore(e.getStartDate()) && !cursor.isAfter(e.getEndDate())){
    					summaries.add(new EventSummaryDTO(e.getId(), e.getTitle()));
    				}
    			}
    			
    			day.setEvents(summaries);
    			week.add(day);
    			
    			cursor = cursor.plusDays(1);
    			
    		}
    		
    		weeks.add(week);
    		
    	}
    	
    	return weeks;
    	
    	
    	
    }
    
    
    
    
}
