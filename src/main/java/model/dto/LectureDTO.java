package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;
import utils.AppDate;
import utils.AppTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LectureDTO {

	private Long lectureId;
	private Long userId;
	private Long departmentId;
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
	private String instructorName;
	private List<LectureScheduleDTO> schedules;
	private String scheduleHtml;
	
}
