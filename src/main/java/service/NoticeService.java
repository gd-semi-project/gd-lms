package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import database.DBConnection;
import model.dao.NoticeDAO;
import model.dto.NoticeDTO;

public class NoticeService {

    private final NoticeDAO noticeDAO = NoticeDAO.getInstance();

    // -------------------------
    // List / Count (조회는 트랜잭션 불필요)
    // -------------------------

    public int countAll(String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.countAll(items, text);
    }

    public List<NoticeDTO> findPageAll(int limit, int offset, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.findPageAll(limit, offset, items, text);
    }

    public int countByLecture(long lectureId, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.countByLecture(lectureId, items, text);
    }

    public List<NoticeDTO> findPageByLecture(long lectureId, int limit, int offset, String items, String text, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);
        return noticeDAO.findPageByLecture(lectureId, limit, offset, items, text);
    }

    // -------------------------
    // Detail (조회수 증가 + 상세조회 트랜잭션)
    // -------------------------

    public NoticeDTO getNoticeDetail(long noticeId, Long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireReadable(role);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1) 조회수 증가
            int updated = noticeDAO.increaseViewCount(conn, noticeId);
            // 공지가 없으면 updated=0일 수 있음 -> 아래 상세조회에서 null 처리

            // 2) 상세조회
            NoticeDTO notice = (lectureId == null)
                    ? noticeDAO.findById(conn, noticeId)
                    : noticeDAO.findByIdAndLecture(conn, noticeId, lectureId);

            if (notice == null) {
                conn.rollback();
                return null;
            }

            conn.commit();
            return notice;

        } catch (Exception e) {
            throw new RuntimeException("NoticeService.getNoticeDetail error", e);
        }
    }

	// 수정 폼용 조회 (조회수 증가 없음)
    public NoticeDTO getNoticeForEdit(Long noticeId, Long lectureId, Long userId, String role)  {
        requireLogin(userId, role);
        requireReadable(role);

        try (Connection conn = DBConnection.getConnection()) {

        	long nid = requirePositive(noticeId, "noticeId");

        	NoticeDTO notice = (lectureId == null)
        	        ? noticeDAO.findById(conn, nid)
        	        : noticeDAO.findByIdAndLecture(conn, nid, lectureId.longValue());

            if (notice == null) return null;

            // 수정 가능 여부는 폼 진입 시점에도 막아주는 게 UX/보안상 좋음
            if (!canWrite(role, userId, notice)) {
                throw new AccessDeniedException("수정 권한이 없습니다.");
            }

            return notice;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("NoticeService.getNoticeForEdit error", e);
        }
    }

    // -------------------------
    // Create / Update / Delete (권한 + 트랜잭션)
    // -------------------------

    public long createNotice(NoticeDTO dto, Long userId, String role) {
        requireLogin(userId, role);
        requireCreatable(role, dto);

        // authorId는 세션 기반으로 강제(클라이언트 신뢰 금지)
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

            // 기존 글 조회(권한/작성자 확인용)
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
                throw new RuntimeException("공지사항 수정 실패(영향 받은 행 0).");
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
                throw new RuntimeException("공지사항 삭제 실패(영향 받은 행 0).");
            }

            conn.commit();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("NoticeService.deleteNotice error", e);
        }
    }

    // -------------------------
    // 권한/검증 로직
    // -------------------------

    private void requireLogin(Long userId, String role) {
        if (userId == null || role == null || role.isBlank()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
    }

    private void requireReadable(String role) {
        // 읽기: ADMIN/INSTRUCTOR/STUDENT 모두 허용
        if (!isRole(role)) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
    }

    private void requireCreatable(String role, NoticeDTO dto) {
        if (!isRole(role)) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");

        // ADMIN: 전체 허용
        if ("ADMIN".equals(role)) return;

        // INSTRUCTOR: 강의 공지(lectureId != null)만 허용
        if ("INSTRUCTOR".equals(role)) {
            if (dto.getLectureId() == null) {
                throw new AccessDeniedException("강사는 전체 공지를 작성할 수 없습니다. (lectureId 필요)");
            }
            return;
        }

        // STUDENT: 공지 작성 불가
        throw new AccessDeniedException("공지 작성 권한이 없습니다.");
    }

    private void requireUpdatable(String role) {
        if (!isRole(role)) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        if ("STUDENT".equals(role)) throw new AccessDeniedException("공지 수정 권한이 없습니다.");
    }

    private void requireDeletable(String role) {
        if (!isRole(role)) throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        if ("STUDENT".equals(role)) throw new AccessDeniedException("공지 삭제 권한이 없습니다.");
    }

    /**
     * 수정/삭제 가능 여부:
     * - ADMIN: 모든 공지 가능
     * - INSTRUCTOR: 본인 글만 가능 + (실무적으로 강의공지에 한정하는 경우가 많음)
     */
    private boolean canWrite(String role, Long userId, NoticeDTO existing) {
        if ("ADMIN".equals(role)) return true;

        if ("INSTRUCTOR".equals(role)) {
            // 강사는 강의 공지만 수정 허용(정책)
            if (existing.getLectureId() == null) return false;
            return existing.getAuthorId() != null && existing.getAuthorId().equals(userId);
        }

        return false;
    }

    private boolean isRole(String role) {
        return "ADMIN".equals(role) || "INSTRUCTOR".equals(role) || "STUDENT".equals(role);
    }

    private long requirePositive(Long v, String name) {
        if (v == null || v <= 0) throw new IllegalArgumentException(name + " is required.");
        return v;
    }

    // -------------------------
    // 예외 타입(런타임)
    // -------------------------

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) { super(message); }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { super(message); }
    }
}
