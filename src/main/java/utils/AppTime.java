package utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class AppTime {
	private enum Mode {
		REAL,
		KERONBALL
	}
	
	private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
	
	private static volatile Mode mode = Mode.REAL;
	private static volatile long keronBallSeconds = 0L;
	
	private AppTime() {}
	
	static LocalDateTime now() {
		if (mode == Mode.KERONBALL) {
			return LocalDateTime.now(ZONE).plusSeconds(keronBallSeconds);
		} else return LocalDateTime.now(ZONE);
	}
	
	static synchronized void setReal() {	
		mode = Mode.REAL;
		keronBallSeconds = 0L;
	}
	
	static synchronized void setKeronBallSeconds(long seconds) {
		mode = Mode.KERONBALL;
		keronBallSeconds = seconds;
	}
}

