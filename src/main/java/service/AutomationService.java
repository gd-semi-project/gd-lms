package service;

import java.time.LocalDateTime;
import java.util.List;

import automation.AutomationJob;

public class AutomationService {
	private final List<AutomationJob> jobs;
	
	public AutomationService(List<AutomationJob> jobs) {
		this.jobs = jobs;
	}
	
	public void runDueJobs(LocalDateTime now) {
		for (AutomationJob job : jobs) {
			System.out.println("[AutomationService] schedule check: "+job.code());
			try {
				if (job.shouldRun(now)) {
					System.out.println("[AutomationService] schedule execute: "+job.code());
					job.run(now);
				}
			} catch (Exception e) {
				System.out.println("runDueJobs실패");
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
