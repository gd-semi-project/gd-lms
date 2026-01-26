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
				//TODO sql 오류가 날 수 있습니다
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
