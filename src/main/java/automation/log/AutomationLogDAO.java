package automation.log;

import java.time.LocalDate;

public interface AutomationLogDAO {
	boolean tryStart(String jobCode, LocalDate runDate);
	void markSuccess(String jobCode, LocalDate runDate, String message);
	void markFail(String jobCode, LocalDate runDate, String message);
}
