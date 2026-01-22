package model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enumtype.NoticeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {
    private Long noticeId;         
    private Long lectureId;         
    private Long authorId;       
    private NoticeType noticeType;     
    private String title;          
    private String content;        
    private int viewCount;         
    private String pinned;               
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt; 
}
