package utils;

import java.time.LocalDate;

public final class EnrollmentPeriod {
	
	// 수강신청 시작일
	public static final LocalDate START = LocalDate.of(2026, 2, 16);
	
	// 수강신청 종료일
	public static final LocalDate END = LocalDate.of(2026, 2, 20);
	
	EnrollmentPeriod(){}
	
	public static boolean isOpen() {
		LocalDate today = LocalDate.now();
		return !today.isBefore(START) && !today.isAfter(END);
	}
}

