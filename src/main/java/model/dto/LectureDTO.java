package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Data
public class LectureDTO {

	private int lecture_id;
	private String lecture_title;
	private int lecture_round;
	private LocalDate start_date;
	private LocalDate end_date;
	private String room;
	private int capacity;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
}
