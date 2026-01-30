package automation.job;

import java.time.LocalDate;
import java.time.LocalDateTime;

import automation.AutomationJob;
import automation.log.AutomationLogDAO;
import service.LectureService;
import utils.AppTime;

public class LectureStatusSyncJob implements AutomationJob{
	
	private final AutomationLogDAO logDAO;
	private final LectureService lectureService;
	
	public LectureStatusSyncJob(
			AutomationLogDAO logDAO,
			LectureService lectureService
			) {
		this.logDAO = logDAO;
		this.lectureService = lectureService;
		
	}
	
	
	
	
	@Override
	public String code() {
		return "LECTURE_STATUS_SYNC";
	}
	@Override
	public boolean shouldRun(LocalDateTime now) {
		//매일 00:00 에 실행함
		return now.getHour() == 0 && now.getMinute() == 0;
	}
	@Override
	public void run(LocalDateTime now) {
		System.out.println("[LectureStatusSyncJob] run START: "+now);
		LocalDate today = AppTime.now().toLocalDate();
		
		if(!logDAO.tryStart(code(), today)) {
			System.out.println("[LectureRequestExpireJob] already executed today");
			return;
		}
		
		try {
            int[] result = lectureService.syncLectureStatusByDate(today);
            int ongoingCount = result[0];
            int endedCount = result[1];

            String msg = "ongoing=" + ongoingCount + ", ended=" + endedCount;
            logDAO.markSuccess(code(), today, msg);

            System.out.println("[LectureStatusSyncJob] SUCCESS " + msg);

		} catch (Exception e) {
			logDAO.markFail(code(), today, e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	
}
