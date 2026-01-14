package utils;

import java.time.LocalDateTime;

public final class AppDateTime {
	private AppDateTime() {}
		
	public static LocalDateTime now() {
		return AppTime.now();
	}
}
