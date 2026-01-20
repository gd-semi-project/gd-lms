package utils;

import java.time.LocalDate;

public final class AppDate {
	private AppDate() {}
	
	public static LocalDate now() {
		return AppTime.now().toLocalDate();
	}
}