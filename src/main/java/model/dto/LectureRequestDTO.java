package model.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.LectureValidation;
import model.enumtype.Week;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LectureRequestDTO {
	private long lectureId;
	private String instructorName;
	private String lectureTitle;
	private String schedule;
	private int capacity;
	private LectureValidation validation;
	private LocalDateTime createdAt;
	private String section;
}
