package model.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import model.enumtype.Week;

@NoArgsConstructor
@ToString
@AllArgsConstructor
@Data
public class LectureScheduleDTO {
	private int scheduleId;
	private int lectureId;
	private Week weekDay;
	private LocalTime startTime;
	private LocalTime endTime;
	private LocalDateTime created_at;
}
