package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstructorDTO {

    // PK, FK → users.user_id
    private long userId;

    private String instructorNo;    // 교번
    private String department;    // 소속 학과
    private String officeRoom;    // 연구실
    private String officePhone;   // 연구실 전화
    private LocalDate hireDate;   // 임용일

    private LocalDateTime createdAt;   // 생성일
    private LocalDateTime updatedAt;   // 수정일
}