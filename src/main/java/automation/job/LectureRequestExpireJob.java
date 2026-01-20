package automation.job;

import java.time.LocalDate;
import java.time.LocalDateTime;

import automation.AutomationJob;
import automation.log.AutomationLogDAO;
import model.enumtype.ScheduleCode;
import service.LectureService;
import service.SchedulePolicyService;

public class LectureRequestExpireJob implements AutomationJob{
	
	private final AutomationLogDAO logDAO;
	private final LectureService lectureService;
	SchedulePolicyService policy = SchedulePolicyService.getInstance();
	
	public LectureRequestExpireJob(
			AutomationLogDAO logDAO,
			LectureService lectureService,
			SchedulePolicyService policy
			) {
		this.logDAO = logDAO;
		this.lectureService = lectureService;
		this.policy = policy;
		
	}
	
	
	
	
	@Override
	public String code() {
		return "LECTURE_REQUEST_EXPIRE";
	}
	@Override
	public boolean shouldRun(LocalDateTime now) {
		return policy.isTriggerDayAfterEnd(ScheduleCode.LECTURE_OPEN_APPROVAL_ADMIN.name(), now);
	}
	@Override
	public void run(LocalDateTime now) {
		LocalDate today = now.toLocalDate();
		
		if(!logDAO.tryStart(code(), today)) {
			System.out.println("[LectureRequestExpireJob] already executed today");
			return;
		}
		
		try {
			int canceledCount = lectureService.cancelExpiredLectureRequest();
			System.out.println("[LectureRequestExpireJob] run START: "+ now);
			logDAO.markSuccess(code(), today, "canceled = " + canceledCount);;
		} catch (Exception e) {
			logDAO.markFail(code(), today, e.getMessage());
			throw e;
		}
	}
	
	
	
	
}
