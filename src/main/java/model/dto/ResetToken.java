package model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetToken {
	private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime verifiedAt;
}