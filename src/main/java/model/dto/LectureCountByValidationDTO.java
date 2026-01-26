package model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class LectureCountByValidationDTO {
	private int totalCount;
	private int pendingCount;
	private int confirmedCount;
	private int canceledCount;

}
