package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import automation.schedule.SchoolScheduleDAO;
import model.dto.SchoolScheduleDTO;

public class SchedulePolicyService {
	
	private SchoolScheduleDAO ssDAO;
	
	public SchedulePolicyService(SchoolScheduleDAO scheduleDAO) {
		this.ssDAO = scheduleDAO;
	}
	
	public boolean isOpenByDate(String scheduleCode, LocalDateTime now) {
		LocalDate today = now.toLocalDate();
		SchoolScheduleDTO ssdto = ssDAO.findByCode(scheduleCode);
		if (ssdto == null) return false;
		
		return !today.isBefore(ssdto.getStartDate()) && !today.isAfter(ssdto.getEndDate());
	}
	
	public boolean isTriggerDayAfterEnd(String scheduleCode, LocalDateTime now) {
		LocalDate today = now.toLocalDate();
		LocalDate endDate = today.minusDays(1);
		SchoolScheduleDTO ssdto = ssDAO.findByCodeAndEndDate(scheduleCode, endDate); 
		return ssdto != null;
	}
	
    public Optional<SchoolScheduleDTO> getSchedule(String scheduleCode) {
        return Optional.ofNullable(ssDAO.findByCode(scheduleCode));
    }
    
    public String getStatusMessage(String scheduleCode, LocalDateTime now) {
        SchoolScheduleDTO s = ssDAO.findByCode(scheduleCode);
        if (s == null) return "일정 정보가 없습니다.";

        LocalDate today = now.toLocalDate();

        if (today.isBefore(s.getStartDate())) {
            return "아직 기간이 아닙니다. (시작: " + s.getStartDate() + ")";
        }
        if (today.isAfter(s.getEndDate())) {
            return "기간이 종료되었습니다. (종료: " + s.getEndDate() + ")";
        }
        return "현재 기간입니다. (" + s.getStartDate() + " ~ " + s.getEndDate() + ")";
    }
    
}
