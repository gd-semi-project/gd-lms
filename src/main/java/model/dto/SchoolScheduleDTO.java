package model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.ScheduleCode;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchoolScheduleDTO {
	private Long id;
	private String title;
	private LocalDate startDate;
	private LocalDate endDate;
	private String memo;
	private ScheduleCode scheduleCode;
}
