package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreDTO {
	private Long gradeId;
	
	private int enrollmentId; // enroll FK
	private int lectureId;	// lecture FK
	private int usersId;	// user FK
	
	// 점수 입력전에 값은 NULL이여야 하기 때문 int대신 Integer사용(int는 NULL불가)
	private Integer attendanceScore;	// 출석 점수
	private Integer middleScore;	// 중간고사 점수
	private Integer finalScore;	// 기말고사 점수
	private Integer totalScore;	// 종합 점수
	private Integer gradeScore;	// 과제 점수
	private String score;		// 등급 A+, A, B+ 등등등
		
	
}
