package model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GradeDTO {
	private int grade_id;
	
	private int enrollment_id; // enroll FK
	private int lecture_id;	// lecture FK
	private int users_id;	// user FK
	
	// 점수 입력전에 값은 NULL이여야 하기 때문 int대신 Integer사용(int는 NULL불가)
	private Integer attendance_score;	// 출석 점수
	private Integer middle_score;	// 중간고사 점수
	private Integer final_score;	// 기말고사 점수
	private Integer total_score;	// 종합 점수
	private Integer grade_score;	// 과제 점수
	private String grade;		// 등급 A+, A, B+ 등등등
		
	
}
