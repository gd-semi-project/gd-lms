package service;

import model.dao.LectureAccessDAO;
import model.dao.QnaAnswerDAO;
import model.dao.QnaPostDAO;
import model.dto.QnaAnswerDTO;
import model.dto.QnaPostDTO;
import model.enumtype.QnaStatus;
import model.enumtype.Role;
import model.enumtype.IsPrivate;
import database.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class QnaService {

    private final QnaPostDAO postDAO = QnaPostDAO.getInstance();
    private final QnaAnswerDAO answerDAO = QnaAnswerDAO.getInstance();
    private final LectureAccessDAO accessDAO = LectureAccessDAO.getInstance();

    /* =========================================================
     *  목록
     * ========================================================= */

    public int countByLecture(long lectureId, long userId, Role role) {
        requireLogin(userId, role);
        requirePositiveId("lectureId", lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);

            if (role == Role.STUDENT) {
                return postDAO.countByLectureForStudent(conn, lectureId, userId);
            }
            return postDAO.countByLecture(conn, lectureId);

        } catch (SQLException e) {
            throw new RuntimeException("QnaService.countByLecture error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("QnaService.countByLecture DB driver error", e);
        }
    }

    public List<QnaPostDTO> listByLecture(long lectureId, int limit, int offset, long userId, Role role) {
        requireLogin(userId, role);
        requirePositiveId("lectureId", lectureId);

        int safeLimit = sanitizeLimit(limit);
        int safeOffset = Math.max(offset, 0);

        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);

            if (role == Role.STUDENT) {
                return postDAO.findByLectureForStudent(conn, lectureId, userId, safeLimit, safeOffset);
            }
            return postDAO.findByLecture(conn, lectureId, safeLimit, safeOffset);

        } catch (SQLException e) {
            throw new RuntimeException("QnaService.listByLecture error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("QnaService.listByLecture DB driver error", e);
        }
    }

    /* =========================================================
     *  상세
     * ========================================================= */

    public QnaPostDTO getPostDetail(long qnaId, long lectureId, long userId, Role role) {
        requireLogin(userId, role);
        requirePositiveId("qnaId", qnaId);
        requirePositiveId("lectureId", lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);

            QnaPostDTO post = postDAO.findById(conn, qnaId, lectureId);
            if (post == null) return null;

            // 학생: 비공개글은 본인만
            assertStudentCanView(post, userId, role);

            return post;

        } catch (SQLException e) {
            throw new RuntimeException("QnaService.getPostDetail error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("QnaService.getPostDetail DB driver error", e);
        }
    }

    public List<QnaAnswerDTO> getAnswers(long qnaId, long lectureId, long userId, Role role) {
        requireLogin(userId, role);
        requirePositiveId("qnaId", qnaId);
        requirePositiveId("lectureId", lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);

            QnaPostDTO post = postDAO.findById(conn, qnaId, lectureId);
            if (post == null) return List.of();

            // 학생: 비공개글은 본인만
            assertStudentCanView(post, userId, role);

            return answerDAO.findByQnaId(conn, qnaId);

        } catch (SQLException e) {
            throw new RuntimeException("QnaService.getAnswers error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("QnaService.getAnswers DB driver error", e);
        }
    }

    /* =========================================================
     *  질문 작성/수정/삭제
     * ========================================================= */

    public long createPost(QnaPostDTO dto, long userId, Role role) {
        requireLogin(userId, role);
        if (dto == null || dto.getLectureId() == null) {
            throw new IllegalArgumentException("dto/lectureId is required.");
        }

        if (role != Role.STUDENT) {
            throw new AccessDeniedException("Q&A 질문 작성은 학생만 가능합니다.");
        }

        dto.setAuthorId(userId);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            assertCanAccessLecture(conn, userId, role, dto.getLectureId());

            long id = postDAO.insert(conn, dto);
            conn.commit();
            return id;

        } catch (RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("QnaService.createPost error", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public void updatePost(QnaPostDTO dto, long userId, Role role) {
        requireLogin(userId, role);
        if (dto == null || dto.getQnaId() == null || dto.getLectureId() == null) {
            throw new IllegalArgumentException("dto/qnaId/lectureId is required.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            assertCanAccessLecture(conn, userId, role, dto.getLectureId());

            // ★ 수정: lectureId로 조회
            QnaPostDTO existing = postDAO.findById(conn, dto.getQnaId(), dto.getLectureId());
            if (existing == null) {
                throw new NotFoundException("Q&A가 존재하지 않습니다.");
            }

            // 학생: 본인 글만 수정 가능
            if (role == Role.STUDENT && !Objects.equals(existing.getAuthorId(), userId)) {
                throw new AccessDeniedException("수정 권한이 없습니다.");
            }

            int updated = postDAO.update(conn, dto);
            if (updated == 0) {
                throw new RuntimeException("Q&A 수정 실패");
            }

            conn.commit();

        } catch (RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("QnaService.updatePost error", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public void deletePost(long qnaId, long lectureId, long userId, Role role) {
        requireLogin(userId, role);
        requirePositiveId("qnaId", qnaId);
        requirePositiveId("lectureId", lectureId);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            assertCanAccessLecture(conn, userId, role, lectureId);

            // ★ 수정: lectureId로 조회
            QnaPostDTO existing = postDAO.findById(conn, qnaId, lectureId);
            if (existing == null) {
                throw new NotFoundException("Q&A가 존재하지 않습니다.");
            }

            // 학생: 본인 글만 삭제 가능
            if (role == Role.STUDENT && !Objects.equals(existing.getAuthorId(), userId)) {
                throw new AccessDeniedException("삭제 권한이 없습니다.");
            }

            int deleted = postDAO.softDelete(conn, qnaId);
            if (deleted == 0) {
                throw new RuntimeException("Q&A 삭제 실패");
            }

            conn.commit();

        } catch (RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("QnaService.deletePost error", e);
        } finally {
            closeQuietly(conn);
        }
    }

    /* =========================================================
     *  답변 작성(교수/관리자)
     * ========================================================= */

    public long addAnswer(QnaAnswerDTO dto, long lectureId, long userId, Role role) {
        requireLogin(userId, role);
        requirePositiveId("lectureId", lectureId);
        if (dto == null || dto.getQnaId() == null) {
            throw new IllegalArgumentException("dto/qnaId is required.");
        }

        if (!(role == Role.INSTRUCTOR || role == Role.ADMIN)) {
            throw new AccessDeniedException("답변 권한이 없습니다.");
        }

        dto.setInstructorId(userId);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            assertCanAccessLecture(conn, userId, role, lectureId);

            // ★ 수정: lectureId로 조회
            QnaPostDTO post = postDAO.findById(conn, dto.getQnaId(), lectureId);
            if (post == null) {
                throw new NotFoundException("질문글이 존재하지 않습니다.");
            }

            long id = answerDAO.insert(conn, dto);

            postDAO.updateStatus(conn, dto.getQnaId(), QnaStatus.ANSWERED);

            conn.commit();
            return id;

        } catch (RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("QnaService.addAnswer error", e);
        } finally {
            closeQuietly(conn);
        }
    }

    /* =========================================================
     *  공통 헬퍼
     * ========================================================= */

    private void requireLogin(long userId, Role role) {
        if (userId <= 0 || role == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
    }

    private void requirePositiveId(String name, long value) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " is required.");
        }
    }

    private int sanitizeLimit(int limit) {
        if (limit <= 0) return 10;
        return Math.min(limit, 50);
    }

    /**
     * 학생 비공개글 접근 검증 (중복 제거용)
     */
    private void assertStudentCanView(QnaPostDTO post, long userId, Role role) {
        if (role != Role.STUDENT) return;

        IsPrivate priv = (post.getIsPrivate() == null) ? IsPrivate.N : post.getIsPrivate();
        if (priv == IsPrivate.Y && !Objects.equals(post.getAuthorId(), userId)) {
            throw new AccessDeniedException("비공개 질문은 작성자 본인만 조회할 수 있습니다.");
        }
    }

    /**
     * 강의 접근 권한 체크
     */
    private void assertCanAccessLecture(Connection conn, long userId, Role role, long lectureId) throws SQLException {
        if (!accessDAO.lectureExists(conn, lectureId)) {
            throw new NotFoundException("존재하지 않는 강의입니다.");
        }

        switch (role) {
            case ADMIN:
                return;
            case INSTRUCTOR:
                if (!accessDAO.isInstructorOfLecture(conn, userId, lectureId)) {
                    throw new AccessDeniedException("본인 강의의 Q&A만 접근할 수 있습니다.");
                }
                return;
            case STUDENT:
                if (!accessDAO.isEnrolledStudent(conn, userId, lectureId)) {
                    throw new AccessDeniedException("수강 중인 강의의 Q&A만 접근할 수 있습니다.");
                }
                return;
            default:
                throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn == null) return;
        try { conn.rollback(); } catch (Exception ignore) {}
    }

    private void closeQuietly(Connection conn) {
        if (conn == null) return;
        try { conn.close(); } catch (Exception ignore) {}
    }

    /* =========================================================
     *  예외
     * ========================================================= */

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String msg) { super(msg); }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }
}