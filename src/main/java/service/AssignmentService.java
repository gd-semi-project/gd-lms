// AssignmentService.java
package service;

import model.dao.AssignmentDAO;
import model.dao.AssignmentSubmissionDAO;
import model.dao.LectureAccessDAO;
import model.dto.AssignmentDTO;
import model.dto.AssignmentSubmissionDTO;
import model.enumtype.Role;
import database.DBConnection;
import jakarta.servlet.http.Part;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import exception.AccessDeniedException;
import exception.ResourceNotFoundException;

public class AssignmentService {
    private static final AssignmentService instance = new AssignmentService();
    private AssignmentService() {}
    public static AssignmentService getInstance() { return instance; }

    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final AssignmentSubmissionDAO submissionDAO = AssignmentSubmissionDAO.getInstance();
    private final LectureAccessDAO accessDAO = LectureAccessDAO.getInstance();

    // 과제 목록
    public List<AssignmentDTO> getAssignmentsByLecture(long lectureId, long userId, Role role) {
        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);
            return assignmentDAO.selectByLecture(conn, lectureId);
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            throw new RuntimeException("과제 목록 조회 실패", e);
        }
    }

    // 과제 상세
    public AssignmentDTO getAssignmentDetail(long assignmentId, long lectureId, long userId, Role role) {
        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);
            return assignmentDAO.selectById(conn, assignmentId, lectureId);
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            throw new RuntimeException("과제 상세 조회 실패", e);
        }
    }

    // 과제 생성
    public long createAssignment(AssignmentDTO dto, long userId, Role role, Collection<Part> partList) {
        if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
            throw new AccessDeniedException("과제 생성 권한이 없습니다.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            FileUploadService fus = FileUploadService.getInstance();
            conn.setAutoCommit(false);
            assertCanAccessLecture(conn, userId, role, dto.getLectureId());
            long id = assignmentDAO.insert(conn, dto);
            
            String boardType = "ASSIGNMENT";
            fus.fileUpload(boardType, id, partList);
            
            conn.commit();
            return id;
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("과제 생성 실패", e);
        } finally {
            closeQuietly(conn);
        }
    }

    // 과제 수정
    public void updateAssignment(AssignmentDTO dto, long userId, Role role, Collection<Part> partList) {
        if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
            throw new AccessDeniedException("과제 수정 권한이 없습니다.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            FileUploadService fus = FileUploadService.getInstance();
            conn.setAutoCommit(false);
            assertCanAccessLecture(conn, userId, role, dto.getLectureId());
            int updated = assignmentDAO.update(conn, dto);
            if (updated == 0) {
                throw new ResourceNotFoundException("과제를 찾을 수 없습니다.");
            }
            
            String boardType = "ASSIGNMENT";
            fus.deleteFile(boardType, dto.getAssignmentId());
            fus.fileUpload(boardType, dto.getAssignmentId(), partList);
            
            conn.commit();
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("과제 수정 실패", e);
        } finally {
            closeQuietly(conn);
        }
    }

    // 과제 삭제
    public void deleteAssignment(long assignmentId, long lectureId, long userId, Role role) {
        if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
            throw new AccessDeniedException("과제 삭제 권한이 없습니다.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            FileUploadService fus = FileUploadService.getInstance();
            conn.setAutoCommit(false);
            assertCanAccessLecture(conn, userId, role, lectureId);
            int deleted = assignmentDAO.softDelete(conn, assignmentId);
            if (deleted == 0) {
                throw new ResourceNotFoundException("과제를 찾을 수 없습니다.");
            }
            String boardType = "ASSIGNMENT";
            fus.deleteFile(boardType, assignmentId);
            
            conn.commit();
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("과제 삭제 실패", e);
        } finally {
            closeQuietly(conn);
        }
    }

    // 제출 목록 (교수용)
    public List<AssignmentSubmissionDTO> getSubmissions(long assignmentId, long lectureId, long userId, Role role) {
        if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
            throw new AccessDeniedException("제출 목록 조회 권한이 없습니다.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);
            return submissionDAO.selectByAssignment(conn, assignmentId);
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            throw new RuntimeException("제출 목록 조회 실패", e);
        }
    }

    // 본인 제출 조회 (학생용)
    public AssignmentSubmissionDTO getMySubmission(long assignmentId, long lectureId, long userId, Role role) {
        if (role != Role.STUDENT) {
            throw new AccessDeniedException("학생만 조회 가능합니다.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            assertCanAccessLecture(conn, userId, role, lectureId);
            return submissionDAO.selectByStudentAndAssignment(conn, userId, assignmentId);
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            throw new RuntimeException("제출 조회 실패", e);
        }
    }

    // 과제 제출 (학생)
    public Long submitAssignment(AssignmentSubmissionDTO dto, long lectureId, long userId, Role role, Collection<Part> partList) {
        if (role != Role.STUDENT) {
            throw new AccessDeniedException("학생만 제출 가능합니다.");
        }

        dto.setStudentId(userId);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            FileUploadService fus = FileUploadService.getInstance();
            conn.setAutoCommit(false);
            assertCanAccessLecture(conn, userId, role, lectureId);

            AssignmentSubmissionDTO existing = submissionDAO.selectByStudentAndAssignment(
                conn, userId, dto.getAssignmentId()
            );
            
            Long submissionId;
            Long assignmentId = dto.getAssignmentId();
            
            // boardType 지정, 참조아이디 지정
            // 재제출: 내용update - 파일is_deleted 변경 - 파일upload
            // 제출: 내용insert - 파일upload
            String boardType = "ASSIGNMENT_SUBMISSION/" + assignmentId;
            if (existing != null) {
                dto.setSubmissionId(existing.getSubmissionId());
                submissionDAO.update(conn, dto);
                submissionId = dto.getSubmissionId();
                fus.deleteFile(boardType, submissionId);
                fus.fileUpload(boardType, submissionId, partList);
            } else {
            	submissionId = submissionDAO.insert(conn, dto);
                fus.fileUpload(boardType, submissionId, partList);
            }
            
            conn.commit();
            return submissionId;
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("과제 제출 실패", e);
        } finally {
            closeQuietly(conn);
        }
    }

    // 채점 (교수)
    public void gradeSubmission(long submissionId, long lectureId, int score, String feedback, long userId, Role role) {
        if (role != Role.INSTRUCTOR && role != Role.ADMIN) {
            throw new AccessDeniedException("채점 권한이 없습니다.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            assertCanAccessLecture(conn, userId, role, lectureId);
            int updated = submissionDAO.updateGrade(conn, submissionId, score, feedback);
            if (updated == 0) {
                throw new ResourceNotFoundException("제출물을 찾을 수 없습니다.");
            }
            conn.commit();
        } catch (AccessDeniedException | ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("채점 실패", e);
        } finally {
            closeQuietly(conn);
        }
    }

    // 공통 헬퍼
    private void assertCanAccessLecture(Connection conn, long userId, Role role, long lectureId) throws SQLException {
        if (!accessDAO.lectureExists(conn, lectureId)) {
            throw new ResourceNotFoundException("존재하지 않는 강의입니다.");
        }

        switch (role) {
            case ADMIN: return;
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
    }

    private void rollbackQuietly(Connection conn) {
        if (conn == null) return;
        try { conn.rollback(); } catch (Exception ignore) {}
    }

    private void closeQuietly(Connection conn) {
        if (conn == null) return;
        try { conn.close(); } catch (Exception ignore) {}
    }


}