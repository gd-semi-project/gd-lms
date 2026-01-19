package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import database.DBConnection;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dao.NoticeDAO;
import model.dto.LectureDTO;
import model.dto.NoticeDTO;

public class NoticeService {

    private final NoticeDAO noticeDAO = NoticeDAO.getInstance();
    private final EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
    private final LectureDAO lectureDAO = LectureDAO.getInstance();

    // ========== 전체 공지사항만 (lectureId = NULL) ==========

    public int countAllNotices(String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.countOnlyGlobalNotices(items, text);
    }

    public List<NoticeDTO> findPageAllNotices(int limit, int offset, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.findPageOnlyGlobalNotices(limit, offset, items, text);
    }

    // ========== 강의 공지사항만 (lectureId != NULL) ==========

    public int countAllLectureNotices(String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        if ("STUDENT".equals(role)) {
            return noticeDAO.countAllLectureNoticesForStudent(userId, items, text);
        } else if ("INSTRUCTOR".equals(role)) {
            return noticeDAO.countAllLectureNoticesForInstructor(userId, items, text);
        } else {
            // ADMIN
            return noticeDAO.countAllLectureNotices(items, text);
        }
    }

    public List<NoticeDTO> findPageAllLectureNotices(int limit, int offset, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        if ("STUDENT".equals(role)) {
            return noticeDAO.findPageAllLectureNoticesForStudent(userId, limit, offset, items, text);
        } else if ("INSTRUCTOR".equals(role)) {
            return noticeDAO.findPageAllLectureNoticesForInstructor(userId, limit, offset, items, text);
        } else {
            // ADMIN
            return noticeDAO.findPageAllLectureNotices(limit, offset, items, text);
        }
    }

    // ========== 특정 강의 공지사항 ==========

    public int countByLecture(long lectureId, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        if ("STUDENT".equals(role)) {
            requireStudentEnrolled(userId, lectureId);
        } else if ("INSTRUCTOR".equals(role)) {
            requireInstructorOwnsLecture(userId, lectureId);
        }

        return noticeDAO.countByLecture(lectureId, items, text);
    }

    public List<NoticeDTO> findPageByLecture(long lectureId, int limit, int offset, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        if ("STUDENT".equals(role)) {
            requireStudentEnrolled(userId, lectureId);
        } else if ("INSTRUCTOR".equals(role)) {
            requireInstructorOwnsLecture(userId, lectureId);
        }

        return noticeDAO.findPageByLecture(lectureId, limit, offset, items, text);
    }

    // ========== Detail ==========

    public NoticeDTO getNoticeDetail(long noticeId, Long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            noticeDAO.increaseViewCount(conn, noticeId);

            NoticeDTO notice = (lectureId == null)
                    ? noticeDAO.findById(conn, noticeId)
                    : noticeDAO.findByIdAndLecture(conn, noticeId, lectureId);

            if (notice == null) {
                conn.rollback();
                return null;
            }

            // 학생: 수강 중인 강의만
            if ("STUDENT".equals(role) && notice.getLectureId() != null) {
                boolean enrolled = enrollmentDAO.isStudentEnrolled(conn, userId, notice.getLectureId());
                if (!enrolled) {
                    conn.rollback();
                    throw new AccessDeniedException("수강하지 않는 강의의 공지사항입니다.");
                }
            }

            // 교수: 본인 강의만
            if ("INSTRUCTOR".equals(role) && notice.getLectureId() != null) {
                LectureDTO lecture = lectureDAO.findById(notice.getLectureId());
                if (lecture == null || !lecture.getUserId().equals(userId)) {
                    conn.rollback();
                    throw new AccessDeniedException("담당하지 않는 강의의 공지사항입니다.");
                }
            }

            conn.commit();
            return notice;

        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("NoticeService.getNoticeDetail error", e);
        }
    }

    public NoticeDTO getNoticeForEdit(Long noticeId, Long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        try (Connection conn = DBConnection.getConnection()) {
            long nid = requirePositive(noticeId, "noticeId");

            NoticeDTO notice = (lectureId == null)
                    ? noticeDAO.findById(conn, nid)
                    : noticeDAO.findByIdAndLecture(conn, nid, lectureId);

            if (notice == null) return null;

            if (!canWrite(role, userId, notice)) {
                throw new AccessDeniedException("수정 권한이 없습니다.");
            }

            return notice;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("NoticeService.getNoticeForEdit error", e);
        }
    }

    // ========== Create / Update / Delete ==========

    public long createNotice(NoticeDTO dto, Long userId, String role) {
        requireLogin(userId, role);
        requireCreatable(role, dto);

        // INSTRUCTOR: 본인 강의인지 확인
        if ("INSTRUCTOR".equals(role) && dto.getLectureId() != null) {
            LectureDTO lecture = lectureDAO.findById(dto.getLectureId());
            if (lecture == null || !lecture.getUserId().equals(userId)) {
                throw new AccessDeniedException("본인이 담당하는 강의에만 공지를 작성할 수 있습니다.");
            }
        }

        dto.setAuthorId(userId);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            long newId = noticeDAO.insert(conn, dto);
            conn.commit();
            return newId;
        } catch (Exception e) {
            throw new RuntimeException("NoticeService.createNotice error", e);
        }
    }

    public void updateNotice(NoticeDTO dto, Long userId, String role) {
        requireLogin(userId, role);
        requireUpdatable(role);

        long noticeId = requirePositive(dto.getNoticeId(), "noticeId");

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            NoticeDTO existing = (dto.getLectureId() == null)
                    ? noticeDAO.findById(conn, noticeId)
                    : noticeDAO.findByIdAndLecture(conn, noticeId, dto.getLectureId());

            if (existing == null) {
                conn.rollback();
                throw new NotFoundException("공지사항이 존재하지 않습니다.");
            }

            if (!canWrite(role, userId, existing)) {
                conn.rollback();
                throw new AccessDeniedException("수정 권한이 없습니다.");
            }

            int updated = noticeDAO.update(conn, dto);
            if (updated == 0) {
                conn.rollback();
                throw new RuntimeException("공지사항 수정 실패.");
            }

            conn.commit();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("NoticeService.updateNotice error", e);
        }
    }

    public void deleteNotice(long noticeId, Long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireDeletable(role);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            NoticeDTO existing = (lectureId == null)
                    ? noticeDAO.findById(conn, noticeId)
                    : noticeDAO.findByIdAndLecture(conn, noticeId, lectureId);

            if (existing == null) {
                conn.rollback();
                throw new NotFoundException("공지사항이 존재하지 않습니다.");
            }

            if (!canWrite(role, userId, existing)) {
                conn.rollback();
                throw new AccessDeniedException("삭제 권한이 없습니다.");
            }

            int deleted = noticeDAO.softDelete(conn, noticeId, lectureId);
            if (deleted == 0) {
                conn.rollback();
                throw new RuntimeException("공지사항 삭제 실패.");
            }

            conn.commit();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("NoticeService.deleteNotice error", e);
        }
    }

    // ========== 강의 목록 조회 ==========

    public List<LectureDTO> getAvailableLectures(Long userId, String role) {
        requireLogin(userId, role);

        if ("ADMIN".equals(role)) {
            return lectureDAO.findAll();
        } else if ("INSTRUCTOR".equals(role)) {
            return lectureDAO.findByInstructor(userId);
        } else {
            throw new AccessDeniedException("강의 목록 조회 권한이 없습니다.");
        }
    }

    public List<LectureDTO> getUserLectures(Long userId, String role) {
        requireLogin(userId, role);

        if ("ADMIN".equals(role)) {
            return lectureDAO.findAll();
        } else if ("INSTRUCTOR".equals(role)) {
            return lectureDAO.findByInstructor(userId);
        } else if ("STUDENT".equals(role)) {
            return lectureDAO.findByStudent(userId);
        }
        return List.of();
    }

    // ========== 권한/검증 로직 ==========

    private void requireLogin(Long userId, String role) {
        if (userId == null || role == null || role.isBlank()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
    }

    private void requireReadable(String role) {
        if (!isRole(role)) {
            throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        }
    }

    private void requireCreatable(String role, NoticeDTO dto) {
        if (!isRole(role)) {
            throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        }

        if ("ADMIN".equals(role)) return;

        if ("INSTRUCTOR".equals(role)) {
            if (dto.getLectureId() == null) {
                throw new AccessDeniedException("강사는 전체 공지를 작성할 수 없습니다. 강의를 선택해주세요.");
            }
            return;
        }

        throw new AccessDeniedException("공지 작성 권한이 없습니다.");
    }

    private void requireUpdatable(String role) {
        if (!isRole(role)) {
            throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        }
        if ("STUDENT".equals(role)) {
            throw new AccessDeniedException("공지 수정 권한이 없습니다.");
        }
    }

    private void requireDeletable(String role) {
        if (!isRole(role)) {
            throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        }
        if ("STUDENT".equals(role)) {
            throw new AccessDeniedException("공지 삭제 권한이 없습니다.");
        }
    }

    private void requireStudentEnrolled(Long userId, Long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            boolean enrolled = enrollmentDAO.isStudentEnrolled(conn, userId, lectureId);
            if (!enrolled) {
                throw new AccessDeniedException("수강하지 않는 강의의 공지사항에 접근할 수 없습니다.");
            }
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("수강 여부 확인 중 오류", e);
        }
    }

    private void requireInstructorOwnsLecture(Long userId, Long lectureId) {
        LectureDTO lecture = lectureDAO.findById(lectureId);
        if (lecture == null || !lecture.getUserId().equals(userId)) {
            throw new AccessDeniedException("담당하지 않는 강의의 공지사항에 접근할 수 없습니다.");
        }
    }

    private boolean canWrite(String role, Long userId, NoticeDTO existing) {
        if ("ADMIN".equals(role)) return true;

        if ("INSTRUCTOR".equals(role)) {
            if (existing.getLectureId() == null) return false;
            return existing.getAuthorId() != null && existing.getAuthorId().equals(userId);
        }

        return false;
    }

    private boolean isRole(String role) {
        return "ADMIN".equals(role) || "INSTRUCTOR".equals(role) || "STUDENT".equals(role);
    }

    private long requirePositive(Long v, String name) {
        if (v == null || v <= 0) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return v;
    }

    // ========== 예외 타입 ==========

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) { 
            super(message); 
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { 
            super(message); 
        }
    }
}