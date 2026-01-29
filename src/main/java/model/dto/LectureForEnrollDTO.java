package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
	private int currentCount;    // 현재 수강 인원
	private String schedule;

}
