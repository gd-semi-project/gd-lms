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
public class LectureForEnrollDTO {

	private Long lectureId;
	private String departmentName;
	private String lectureTitle;
	private String instructorName;
	private String room;
	private int capacity;
	private String schedule;

}
