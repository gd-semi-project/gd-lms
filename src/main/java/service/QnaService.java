package service;

import database.DBConnection;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dao.QnaAnswerDAO;
import model.dao.QnaPostDAO;
import model.dto.LectureDTO;
import model.dto.QnaAnswerDTO;
import model.dto.QnaPostDTO;
import model.enumtype.QnaStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class QnaService {

    private final QnaPostDAO postDAO = QnaPostDAO.getInstance();
    private final QnaAnswerDAO answerDAO = QnaAnswerDAO.getInstance();
    private final EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
    private final LectureDAO lectureDAO = LectureDAO.getInstance();

    // ====== 목록 ======
    public int countByLecture(long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            if ("STUDENT".equals(role)) {
                return postDAO.countByLectureForStudent(conn, lectureId, userId);
            }
            return postDAO.countByLecture(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("QnaService.countByLecture error", e);
        }
    }

    public List<QnaPostDTO> listByLecture(long lectureId, int limit, int offset, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            if ("STUDENT".equals(role)) {
                return postDAO.findByLectureForStudent(conn, lectureId, userId, limit, offset);
            }
            return postDAO.findByLecture(conn, lectureId, limit, offset);
        } catch (Exception e) {
            throw new RuntimeException("QnaService.listByLecture error", e);
        }
    }

    // ====== 상세 (요구조건: 학생은 본인 글만 상세 진입 가능) ======
    public QnaPostDTO getPostDetail(long qnaId, long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            QnaPostDTO post = postDAO.findById(conn, qnaId);
            if (post == null) return null;

            // URL 변조 방지: 강의ID 일치 검증 (Long 비교 안전 처리)
            if (post.getLectureId() == null || !Objects.equals(post.getLectureId(), lectureId)) {
                throw new AccessDeniedException("잘못된 접근입니다(강의 불일치).");
            }

            // 학생은 본인 글만 상세 가능 (Long 비교 안전 처리)
            if ("STUDENT".equals(role) && !Objects.equals(post.getAuthorId(), userId)) {
                throw new AccessDeniedException("학생은 본인이 작성한 Q&A만 상세 조회할 수 있습니다.");
            }

            return post;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("QnaService.getPostDetail error", e);
        }
    }

    public List<QnaAnswerDTO> getAnswers(long qnaId, Long userId, String role, long lectureId) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            return answerDAO.findByQnaId(conn, qnaId);
        } catch (Exception e) {
            throw new RuntimeException("QnaService.getAnswers error", e);
        }
    }

    // ====== 질문 작성/수정/삭제 ======
    public long createPost(QnaPostDTO dto, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, dto.getLectureId());

        if (!"STUDENT".equals(role)) {
            throw new AccessDeniedException("Q&A 질문 작성은 학생만 가능합니다.");
        }

        dto.setAuthorId(userId);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            long id = postDAO.insert(conn, dto);
            conn.commit();
            return id;
        } catch (Exception e) {
            throw new RuntimeException("QnaService.createPost error", e);
        }
    }

    public void updatePost(QnaPostDTO dto, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, dto.getLectureId());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            QnaPostDTO existing = postDAO.findById(conn, dto.getQnaId());
            if (existing == null) throw new NotFoundException("Q&A가 존재하지 않습니다.");

            if ("STUDENT".equals(role)) {
                if (!Objects.equals(existing.getAuthorId(), userId)) {
                    throw new AccessDeniedException("수정 권한이 없습니다.");
                }
            } else if ("INSTRUCTOR".equals(role)) {
                // 강의 접근 권한 체크로 충분
            } else if (!"ADMIN".equals(role)) {
                throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
            }

            int updated = postDAO.update(conn, dto);
            if (updated == 0) throw new RuntimeException("Q&A 수정 실패");
            conn.commit();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("QnaService.updatePost error", e);
        }
    }

    public void deletePost(long qnaId, long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, lectureId);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            QnaPostDTO existing = postDAO.findById(conn, qnaId);
            if (existing == null) throw new NotFoundException("Q&A가 존재하지 않습니다.");

            if ("STUDENT".equals(role)) {
                if (!Objects.equals(existing.getAuthorId(), userId)) {
                    throw new AccessDeniedException("삭제 권한이 없습니다.");
                }
            } else if ("INSTRUCTOR".equals(role)) {
                // 본인 강의 접근권한 통과
            } else if (!"ADMIN".equals(role)) {
                throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
            }

            int deleted = postDAO.softDelete(conn, qnaId);
            if (deleted == 0) throw new RuntimeException("Q&A 삭제 실패");
            conn.commit();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("QnaService.deletePost error", e);
        }
    }

    // ====== 답변 작성(교수/관리자) ======
    public long addAnswer(QnaAnswerDTO dto, long lectureId, Long userId, String role) {
        requireLogin(userId, role);
        requireLectureAccessible(userId, role, lectureId);

        if (!("INSTRUCTOR".equals(role) || "ADMIN".equals(role))) {
            throw new AccessDeniedException("답변 권한이 없습니다.");
        }

        dto.setInstructorId(userId);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            QnaPostDTO post = postDAO.findById(conn, dto.getQnaId());
            if (post == null) throw new NotFoundException("질문글이 존재하지 않습니다.");

            // lecture 매칭 검증 (Long 비교 안전 처리)
            if (post.getLectureId() == null || !Objects.equals(post.getLectureId(), lectureId)) {
                throw new AccessDeniedException("잘못된 접근입니다(강의 불일치).");
            }

            long id = answerDAO.insert(conn, dto);

            // ★ enum으로 상태 갱신
            postDAO.updateStatus(conn, dto.getQnaId(), QnaStatus.ANSWERED);

            conn.commit();
            return id;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("QnaService.addAnswer error", e);
        }
    }

    // ====== 공통 권한 체크 ======
    private void requireLogin(Long userId, String role) {
        if (userId == null || role == null || role.isBlank()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
    }

    private void requireLectureAccessible(Long userId, String role, Long lectureId) {
        if (lectureId == null || lectureId <= 0) throw new IllegalArgumentException("lectureId is required.");

        if ("ADMIN".equals(role)) return;

        if ("INSTRUCTOR".equals(role)) {
            LectureDTO lec = lectureDAO.findById(lectureId);
            if (lec == null || lec.getUserId() == null || !lec.getUserId().equals(userId)) {
                throw new AccessDeniedException("본인 강의의 Q&A만 접근할 수 있습니다.");
            }
            return;
        }

        if ("STUDENT".equals(role)) {
            try (Connection conn = DBConnection.getConnection()) {
                boolean enrolled = enrollmentDAO.isStudentEnrolled(conn, userId, lectureId);
                if (!enrolled) throw new AccessDeniedException("수강 중인 강의의 Q&A만 접근할 수 있습니다.");
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("수강 여부 확인 중 오류", e);
            }
            return;
        }

        throw new AccessDeniedException("권한 정보가 올바르지 않습니다.");
    }

    // ====== 예외 ======
    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String msg) { super(msg); }
    }
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }
}
