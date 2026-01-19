package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentsDTO {
	private long assignmentId;	// PK
	private long lectureId;	// lecture_id FK
	private long creatorId;	// user_id FK
	
	private Integer weekNo;	// 주차(1주차, 2주차 ~~)
	private String title;	// 제목
	private String content;	// 내용
	
	private LocalDateTime dueAt;	// 마감일
	private LocalDateTime createdAt;	// 게시일
	private LocalDateTime updatedAt;	// 게시판 수정일
	
}
