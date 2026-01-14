package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {

    private int professorId;
    private String name;
    private String email;
    private String department;
    private String phone;
    private String status;
}