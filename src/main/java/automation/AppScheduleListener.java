package automation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import automation.job.LectureRequestExpireJob;
import automation.log.AutomationLogDAO;
import automation.log.AutomationLogDAOImpl;
import automation.schedule.SchoolScheduleDAO;
import automation.schedule.SchoolScheduleDAOImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.AutomationService;
import service.LectureService;
import service.SchedulePolicyService;
import utils.AppTime;

@WebListener
public class AppScheduleListener implements ServletContextListener{
	private ScheduledExecutorService scheduler;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("AppScheduleListener 가동");
		AutomationLogDAO logDAO = new AutomationLogDAOImpl();
		LectureService lectureService = LectureService.getInstance();
		
		SchoolScheduleDAO scheduleDAO = new SchoolScheduleDAOImpl();
		SchedulePolicyService policy = new SchedulePolicyService(scheduleDAO);
		
		
		
		AutomationService automationService = new AutomationService(
				List.of( 
						// jobLists
						
						new LectureRequestExpireJob(logDAO, lectureService, policy)
						
						
						
					)
				);
		
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			LocalDateTime now = AppTime.now();
			System.out.println("[AppScheduleListener] tick: "+AppTime.now());
			automationService.runDueJobs(now);
		}, 0, 1, TimeUnit.MINUTES);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(scheduler != null) scheduler.shutdownNow();
	}
}
