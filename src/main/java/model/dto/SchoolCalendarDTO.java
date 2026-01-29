package model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchoolCalendarDTO {

	private int selectedYear;
	private int selectedMonth;
	

	private String displayMonth;
	private int prevMonth;
	private int prevYear;
	private int nextMonth;
	private int nextYear;
	
	private List<List<DayDTO>> weeks;
	
	private List<SchoolScheduleDTO> eventList;
	private int monthCount;
	
}
