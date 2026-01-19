package model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchoolScheduleDTO {
	private long id;
	private String title;
	private LocalDate startDate;
	private LocalDate endDate;
	private String memo;
}
