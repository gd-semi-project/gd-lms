package model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssignmentsDTO {
	private int assignment_id;	// PK
	private int lecture_id;	// lecture_id FK
	private int creator_id;	// user_id FK
	
	private Integer weekNo;	// 주차(1주차, 2주차 ~~)
	private String title;
	private String content;
	
	private LocalDateTime dueAt;	// 마감일
	private LocalDateTime created_at;	// 게시일
	private LocalDateTime updated_at;	// 게시판 수정일
	
}
