package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dao.NoticeDAO;
import model.dto.LectureDTO;
import model.dto.NoticeDTO;
import model.enumtype.NoticeType;
import model.enumtype.Role;

public class NoticeService {

    private final NoticeDAO noticeDAO = NoticeDAO.getInstance();
    private final EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
    private final LectureDAO lectureDAO = LectureDAO.getInstance();

    // ========== 전체 공지사항만 (lecture_id IS NULL) ==========

    public int countAllNotices(String items, String text, Long userId, Role role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.countOnlyGlobalNotices(items, text);
    }

    public List<NoticeDTO> findPageAllNotices(int limit, int offset, String items, String text, Long userId, Role role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.findPageOnlyGlobalNotices(limit, offset, items, text);
    }

    // ========== 강의 공지사항만 (lecture_id IS NOT NULL) ==========

    public int countAllLectureNotices(String items, String text, Long userId, Role role) {
        requireLogin(userId, role);
        requireReadable(role);

        if (role == Role.STUDENT) {
            return noticeDAO.countAllLectureNoticesForStudent(userId, items, text);
        } else if (role == Role.INSTRUCTOR) {
            return noticeDAO.countAllLectureNoticesForInstructor(userId, items, text);
        } else {
            // ADMIN
            return noticeDAO.countAllLectureNotices(items, text);
        }
    }

    public List<NoticeDTO> findPageAllLectureNotices(int limit, int offset, String items, String text, Long userId, Role role) {
        requireLogin(userId, role);
        requireReadable(role);

        if (role == Role.STUDENT) {
            return noticeDAO.findPageAllLectureNoticesForStudent(userId, limit, offset, items, text);
        } else if (role == Role.INSTRUCTOR) {
            return noticeDAO.findPageAllLectureNoticesForInstructor(userId, limit, offset, items, text);
        } else {
            // ADMIN
            return noticeDAO.findPageAllLectureNotices(limit, offset, items, text);
        }
    }

    // ========== 특정 강의 공지사항 ==========

    public int countByLecture(long lectureId, String items, String text, Long userId, Role role) {
        requireLogin(userId, role);
        requireReadable(role);

        if (role == Role.STUDENT) {
            requireStudentEnrolled(userId, lectureId);
        } else if (role == Role.INSTRUCTOR) {
            requireInstructorOwnsLecture(userId, lectureId);
        }
        // ADMIN은 제한 없음

        return noticeDAO.countByLecture(lectureId, items, text);
    }

    public List<NoticeDTO> findPageByLecture(long lectureId, int limit, int offset, String items, String text, Long userId, Role role) {
        requireLogin(userId, role);
        requireReadable(role);

        if (role == Role.STUDENT) {
            requireStudentEnrolled(userId, lectureId);
        } else if (role == Role.INSTRUCTOR) {
            requireInstructorOwnsLecture(userId, lectureId);
        }
        // ADMIN은 제한 없음

        return noticeDAO.findPageByLecture(lectureId, limit, offset, items, text);
    }

    // ========== Detail ==========

    public NoticeDTO getNoticeDetail(long noticeId, Long lectureId, Long userId, Role role) {
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
            if (role == Role.STUDENT && notice.getLectureId() != null) {
                boolean enrolled = enrollmentDAO.isStudentEnrolled(conn, userId, notice.getLectureId());
                if (!enrolled) {
                    conn.rollback();
                    throw new AccessDeniedException("수강하지 않는 강의의 공지사항입니다.");
                }
            }

            // 교수: 본인 강의만
            if (role == Role.INSTRUCTOR && notice.getLectureId() != null) {
                LectureDTO lecture = lectureDAO.findById(notice.getLectureId());
                if (lecture == null || lecture.getUserId() == null || !lecture.getUserId().equals(userId)) {
                    conn.rollback();
                    throw new AccessDeniedException("담당하지 않는 강의의 공지사항입니다.");
                }
            }

            // ADMIN: 제한 없음

            conn.commit();
            return notice;

        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("NoticeService.getNoticeDetail error", e);
        }
    }

    public NoticeDTO getNoticeForEdit(Long noticeId, Long lectureId, Long userId, Role role) {
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

        } catch (Exception e) {
            throw new RuntimeException("NoticeService.getNoticeForEdit error", e);
        }
    }

    // ========== Create / Update / Delete ==========

    public long createNotice(NoticeDTO dto, Long userId, Role role) {
        requireLogin(userId, role);
        requireCreatable(role);

        // ✅ enum 도입 이후 정합성 체크
        normalizeAndValidateType(dto);

        // INSTRUCTOR: 본인 강의인지 확인 (LECTURE 공지일 때만 의미 있음)
        if (role == Role.INSTRUCTOR && dto.getLectureId() != null) {
            LectureDTO lecture = lectureDAO.findById(dto.getLectureId());
            if (lecture == null || lecture.getUserId() == null || !lecture.getUserId().equals(userId)) {
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

    public void updateNotice(NoticeDTO dto, Long userId, Role role) {
        requireLogin(userId, role);
        requireUpdatable(role);

        long noticeId = requirePositive(dto.getNoticeId(), "noticeId");

        // ✅ enum 도입 이후 정합성 체크
        normalizeAndValidateType(dto);

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

    public void deleteNotice(long noticeId, Long lectureId, Long userId, Role role) {
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

    public List<LectureDTO> getAvailableLectures(Long userId, Role role) {
        requireLogin(userId, role);

        if (role == Role.ADMIN) {
            return lectureDAO.findAll();
        } else if (role == Role.INSTRUCTOR) {
            return lectureDAO.findByInstructor(userId);
        } else {
            throw new AccessDeniedException("강의 목록 조회 권한이 없습니다.");
        }
    }

    public List<LectureDTO> getUserLectures(Long userId, Role role) {
        requireLogin(userId, role);

        if (role == Role.ADMIN) {
            return lectureDAO.findAll();
        } else if (role == Role.INSTRUCTOR) {
            return lectureDAO.findByInstructor(userId);
        } else if (role == Role.STUDENT) {
            return lectureDAO.findByStudent(userId);
        }
        return List.of();
    }

    // ========== 권한/검증 로직 ==========

    private void requireLogin(Long userId, Role role) {
        if (userId == null || role == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
    }

    private void requireReadable(Role role) {
        if (role == null) {
            throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        }
    }

    private void requireCreatable(Role role) {
        if (role == null) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        if (role == Role.STUDENT) throw new AccessDeniedException("공지 작성 권한이 없습니다.");
        // ADMIN/INSTRUCTOR 가능
    }

    private void requireUpdatable(Role role) {
        if (role == null) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        if (role == Role.STUDENT) throw new AccessDeniedException("공지 수정 권한이 없습니다.");
        // ADMIN/INSTRUCTOR 가능(단, canWrite에서 최종 판단)
    }

    private void requireDeletable(Role role) {
        if (role == null) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        if (role == Role.STUDENT) throw new AccessDeniedException("공지 삭제 권한이 없습니다.");
        // ADMIN/INSTRUCTOR 가능(단, canWrite에서 최종 판단)
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
        if (lecture == null || lecture.getUserId() == null || !lecture.getUserId().equals(userId)) {
            throw new AccessDeniedException("담당하지 않는 강의의 공지사항에 접근할 수 없습니다.");
        }
    }

    private boolean canWrite(Role role, Long userId, NoticeDTO existing) {
        if (role == Role.ADMIN) return true;

        if (role == Role.INSTRUCTOR) {
            // 강사는 전체공지(lecture_id null)는 수정/삭제 불가
            if (existing.getLectureId() == null) return false;
            // 강사는 본인이 작성한 것만 수정/삭제
            return existing.getAuthorId() != null && existing.getAuthorId().equals(userId);
        }

        return false; // STUDENT
    }

    private long requirePositive(Long v, String name) {
        if (v == null || v <= 0) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return v;
    }

    /**
     * ✅ NoticeType(enum) 기준으로 lectureId 정합성 강제
     * - ANNOUNCEMENT(전체공지): lectureId는 반드시 null
     * - LECTURE(강의공지): lectureId는 반드시 존재
     */
    private void normalizeAndValidateType(NoticeDTO dto) {
        if (dto == null) throw new IllegalArgumentException("dto is required.");
        if (dto.getNoticeType() == null) throw new IllegalArgumentException("noticeType is required.");

        if (dto.getNoticeType() == NoticeType.ANNOUNCEMENT) {
            // 전체공지면 lectureId를 null로 강제(실수 방지)
            dto.setLectureId(null);
        } else if (dto.getNoticeType() == NoticeType.LECTURE) {
            if (dto.getLectureId() == null || dto.getLectureId() <= 0) {
                throw new IllegalArgumentException("LECTURE 공지는 lectureId가 필수입니다.");
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 noticeType 입니다.");
        }
    }

    // ========== 예외 타입 ==========

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) { super(message); }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { super(message); }
    }
}
