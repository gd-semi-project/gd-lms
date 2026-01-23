package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeOptionDTO {
	    private String value; // enum.name()
	    private String label; // 한글 표시
	}