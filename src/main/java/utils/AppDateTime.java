package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AppDateTime {
	private AppDateTime() {}
		
	public static LocalDateTime now() {
		return AppTime.now();
	}
	
	public static LocalDate today() {
        return AppTime.now().toLocalDate();
    }
}
