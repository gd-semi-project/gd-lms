package service;

import java.sql.Connection;
import java.time.LocalDate;

import database.DBConnection;
import model.dao.LectureAccessDAO;
import model.dao.SchoolScheduleDAO;
import model.dto.LectureDTO;
import model.dto.SchoolScheduleDTO;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;
import model.enumtype.Role;
import model.enumtype.ScheduleCode;
import utils.AppTime;
import exception.AccessDeniedException;

public class LectureAccessService {

    private final LectureAccessDAO accessDAO = LectureAccessDAO.getInstance();

    /**
     * 강의 접근 권한 가드(실패 시 예외)
     */
    public void assertCanAccessLecture(long userId, long lectureId, Role role) {
        if (userId <= 0 || role == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        if (lectureId <= 0) {
            throw new AccessDeniedException("강의 정보가 올바르지 않습니다.");
        }

        try (Connection conn = DBConnection.getConnection()) {

            // 1) 강의 존재 확인
            if (!accessDAO.lectureExists(conn, lectureId)) {
                throw new AccessDeniedException("존재하지 않는 강의입니다.");
            }

            // 2) 역할별 접근
            switch (role) {
                case ADMIN:
                    return;

                case INSTRUCTOR:
                    if (!accessDAO.isInstructorOfLecture(conn, userId, lectureId)) {
                        throw new AccessDeniedException("본인 강의만 접근 가능합니다.");
                    }
                    return;

                case STUDENT:
                    if (!accessDAO.isEnrolledStudent(conn, userId, lectureId)) {
                        throw new AccessDeniedException("수강 중인 강의만 접근 가능합니다.");
                    }
                    return;

                default:
                    throw new AccessDeniedException("접근 권한이 없습니다.");
            }

        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("LectureAccessService.assertCanAccessLecture error", e);
        }
    }
    
    public void assertLectureIsOpen(LectureDTO lecture) {

        if (lecture == null) {
            throw new AccessDeniedException("강의 정보가 존재하지 않습니다.");
        }
        if (lecture.getValidation() != LectureValidation.CONFIRMED) {
            throw new AccessDeniedException("승인되지 않은 강의입니다.");
        }
        if (lecture.getStatus() != LectureStatus.ONGOING) {
            throw new AccessDeniedException("현재 진행 중인 강의가 아닙니다.");
        }
    }
    
}

