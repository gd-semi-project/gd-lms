package model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DayDTO {
	private LocalDate date;
	private int dayNumber;
	private boolean inCurrentMonth;
	private boolean isToday;
	
	private List<EventSummaryDTO> events;
}
