package model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyscheduleDTO {

	private String lectureTitle;
    private String weekDay;    
    private int startHour;     
    private int endHour;
	
}
