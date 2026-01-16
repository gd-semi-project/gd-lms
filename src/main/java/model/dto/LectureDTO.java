package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LectureDTO {

	private long lectureId;
	private long userId;
	private String lectureTitle;
	private int lectureRound;
	private LocalDate startDate;
	private LocalDate endDate;
	private LectureStatus status;
	private String room;
	private int capacity;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LectureValidation validation;
	private String section;
}
