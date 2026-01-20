package automation.schedule;

import java.time.LocalDate;

import model.dto.SchoolScheduleDTO;

public interface SchoolScheduleDAO {
	
	LocalDate findEndDateByCode(String scheduleCode);
	
	default boolean isTriggerDayAfterEnd(String scheduleCode, LocalDate today) {
		LocalDate end = findEndDateByCode(scheduleCode);
		return end != null && today.equals(end.plusDays(1));
	}
	
	
	SchoolScheduleDTO findByCode(String scheduleCode);
	SchoolScheduleDTO findByCodeAndEndDate(String scheduleCode, LocalDate endDate);
	
	
	
	
	
}
