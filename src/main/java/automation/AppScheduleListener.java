package automation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import automation.job.LectureRequestExpireJob;
import automation.job.LectureStatusSyncJob;
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
	private static AppScheduleListener INSTANCE;
	private AutomationService automationService;
	public static AppScheduleListener getInstance() {return INSTANCE;}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("AppScheduleListener 가동");
		INSTANCE = this;
		AutomationLogDAO logDAO = new AutomationLogDAOImpl();
		LectureService lectureService = LectureService.getInstance();
		
		SchoolScheduleDAO scheduleDAO = new SchoolScheduleDAOImpl();
		SchedulePolicyService policy = new SchedulePolicyService(scheduleDAO);
		
		
		
		this.automationService = new AutomationService(
				List.of( 
						// jobLists
						
						new LectureRequestExpireJob(logDAO, lectureService, policy),
						new LectureStatusSyncJob(logDAO, lectureService)
						
						
					)
				);
		
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.MINUTES);
	}
	
	public void tick() {
		LocalDateTime now = AppTime.now();
		System.out.println("[AppScheduleListener] tick: "+AppTime.now());
		automationService.runDueJobs(now);
	}
	
	public void forceTick() {
		System.out.println("[AppScheduleListener] forceTick: "+ AppTime.now());
		tick();
	}
	
	
	
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(scheduler != null) scheduler.shutdownNow();
	}
}
