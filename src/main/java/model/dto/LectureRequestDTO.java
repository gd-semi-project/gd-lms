package model.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.LectureValidation;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LectureRequestDTO {
	private Long lectureId;
	private String instructorName;
	private String lectureTitle;
	private String schedule;
	private int capacity;
	private LectureValidation validation;
	private LocalDateTime createdAt;
	private String section;
	
	// 추가 : 지윤
	private int lectureRound;
    private LocalDate startDate;
    private LocalDate endDate;
    private String room; 
}
