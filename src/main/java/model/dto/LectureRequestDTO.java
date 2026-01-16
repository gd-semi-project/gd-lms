package model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.LectureValidation;

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
	private String createdAt;
	private String section;
}
