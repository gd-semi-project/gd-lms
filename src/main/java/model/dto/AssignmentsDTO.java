package model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssignmentsDTO {
	private int assignmentId;	// PK
	private int lectureId;	// lecture_id FK
	private int creatorId;	// user_id FK
	
	private Integer weekNo;	// 주차(1주차, 2주차 ~~)
	private String title;	// 제목
	private String content;	// 내용
	
	private LocalDateTime dueAt;	// 마감일
	private LocalDateTime createdAt;	// 게시일
	private LocalDateTime updatedAt;	// 게시판 수정일
	
}
