package model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleUiPolicyDTO {

	private boolean available;
	private String message;
	private LocalDate startDate;
	private LocalDate endDate;
	
}
