package automation;

import java.time.LocalDateTime;

public interface AutomationJob {
	String code();
	boolean shouldRun(LocalDateTime now);
	void run(LocalDateTime now);
}
