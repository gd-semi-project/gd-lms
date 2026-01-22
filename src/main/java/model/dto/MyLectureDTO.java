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
public class MyLectureDTO {

	private Long lectureId;
	private Long userId;
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
	// 과목 담당교수
	private String InstructorName;

}
