package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.dto.LectureDTO;
import java.util.ArrayList;

import database.DBConnection;
import model.dto.LectureDTO;
import model.dto.LectureScheduleDTO;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;

public class LectureDAO {
	public static final LectureDAO instance = new LectureDAO();

	public static LectureDAO getInstance() {
		return instance;
	}

	public int getLectureCount() { // 개설된 강의 개수 (이름 중첩x)
		// TODO LectureDB에서 이름 중첩 안 되게 count(*) 가져와서 리턴해주기
		return 0;
	}

	public int getTotalLectureCount() { // 모든 강의 개수 (이름 중첩o)
		// TODO LectureDB에서 모든 강의 count(*) 가져와서 리턴해주기
		return 0;
	}

	public int getLectureFillRate() { // 정원/인원
		// TODO (LectureDB에 있는 모든 정원수) 나누기 (EnrollmentDB에서 status가 Enrolled상태인 모든 인원) 리턴
		return 0;
	}

	public int getLowFillRateLecture() { // 정원/인원이 50% 미만인 모든 강의 수
		// TODO (LectureDB에 있는 정원 수)나누기(해당 Lecture의 Enrollment 수)<50의 수 모두 가져와서 리턴해주기
		return 0;
	}

	public int getTotalLectureCapacity() { // 모든 정원 수
		// TODO LectureDB에 있는 모든 정원 수 더해서 리턴하기
		return 0;
	}

	public int getTotalEnrollment() { // 모든 수강 인원 수
		// TODO usersDB 에서 role이 student 인 모든 인원 수 중 status가 active인 모든 인원 수 리턴
		return 0;
	}

	public int getLectureRequestCount() { // 강의 개설 요청 총 수
		// TODO 강의 개설 요청상태를 알리는 컬럼부터 고민
		return 0;
	}


	public void setLectureValidation(String validation, Long lectureId) { // 강의 개설 상태 업데이트


		String sql = "UPDATE lecture SET validation = ? WHERE lecture_id = ?;";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {

			pstmt.setString(1, validation);
			pstmt.setLong(2, lectureId);

			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("setLectureValidation() 예외 발생");
			e.printStackTrace();
		}
	}
	
	

    // 교수 담당 강의 목록 : 지윤
    public List<LectureDTO> selectLecturesByInstructor(
            Connection conn, long instructorId) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                lecture_title,
                lecture_round,
                section,
                start_date,
                end_date,
                room,
                capacity,
                status,
                validation,
                created_at,
                updated_at
            FROM lecture
            WHERE user_id = ?
            ORDER BY start_date DESC
        """;

        List<LectureDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, instructorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureDTO lecture = new LectureDTO();

                    lecture.setLectureId(rs.getLong("lecture_id"));
                    lecture.setLectureTitle(rs.getString("lecture_title"));
                    lecture.setLectureRound(rs.getInt("lecture_round"));
                    lecture.setSection(rs.getString("section"));

                    lecture.setStartDate(
                        rs.getDate("start_date") != null
                            ? rs.getDate("start_date").toLocalDate()
                            : null
                    );

                    lecture.setEndDate(
                        rs.getDate("end_date") != null
                            ? rs.getDate("end_date").toLocalDate()
                            : null
                    );

                    lecture.setRoom(rs.getString("room"));
                    lecture.setCapacity(rs.getInt("capacity"));

                    lecture.setStatus(
                        LectureStatus.valueOf(rs.getString("status"))
                    );

                    lecture.setValidation(
                        LectureValidation.valueOf(rs.getString("validation"))
                    );

                    lecture.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    lecture.setUpdatedAt(
                        rs.getTimestamp("updated_at").toLocalDateTime()
                    );

                    list.add(lecture);
                }
            }
        }

        return list;
    }

    // 강의 상세 조회 : 지윤
    public LectureDTO selectLectureById(
            Connection conn, long lectureId) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                lecture_title,
                lecture_round,
                section,
                user_id,
                start_date,
                end_date,
                room,
                capacity,
                status,
                validation,
                created_at,
                updated_at
            FROM lecture
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                LectureDTO lecture = new LectureDTO();

                lecture.setLectureId(rs.getLong("lecture_id"));
                lecture.setLectureTitle(rs.getString("lecture_title"));
                lecture.setLectureRound(rs.getInt("lecture_round"));
                lecture.setSection(rs.getString("section"));
                lecture.setUserId(rs.getLong("user_id"));

                lecture.setStartDate(
                    rs.getDate("start_date") != null
                        ? rs.getDate("start_date").toLocalDate()
                        : null
                );

                lecture.setEndDate(
                    rs.getDate("end_date") != null
                        ? rs.getDate("end_date").toLocalDate()
                        : null
                );

                lecture.setRoom(rs.getString("room"));
                lecture.setCapacity(rs.getInt("capacity"));

                lecture.setStatus(
                    LectureStatus.valueOf(rs.getString("status"))
                );

                lecture.setValidation(
                    LectureValidation.valueOf(rs.getString("validation"))
                );

                lecture.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
                );

                lecture.setUpdatedAt(
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );

                return lecture;
            }
        }
    }

		
	
	
	// ========== 공지사항용 메서드 추가 ==========
	
	/**
	 * 모든 강의 목록 조회 (관리자용)
	 */
	public List<LectureDTO> findAll() {
		String sql = 
			"SELECT lecture_id, user_id, lecture_title, lecture_round, " +
			"       start_date, end_date, status, room, capacity, " +
			"       created_at, updated_at, validation, section " +
			"FROM lecture " +
			"WHERE validation = 'CONFIRMED' " +
			"ORDER BY lecture_title, lecture_round";
		
		List<LectureDTO> list = new ArrayList<>();
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			
			while (rs.next()) {
				list.add(mapResultSetToDTO(rs));
			}
			
			return list;
			
		} catch (Exception e) {
			throw new RuntimeException("LectureDAO.findAll error", e);
		}
	}

	/**
	 * 특정 교수의 강의 목록 조회 (교수용)
	 */
	public List<LectureDTO> findByInstructor(Long instructorId) {
		String sql = 
			"SELECT lecture_id, user_id, lecture_title, lecture_round, " +
			"       start_date, end_date, status, room, capacity, " +
			"       created_at, updated_at, validation, section " +
			"FROM lecture " +
			"WHERE validation = 'CONFIRMED' AND user_id = ? " +
			"ORDER BY lecture_title, lecture_round";
		
		List<LectureDTO> list = new ArrayList<>();
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setLong(1, instructorId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToDTO(rs));
				}
			}
			
			return list;
			
		} catch (Exception e) {
			throw new RuntimeException("LectureDAO.findByInstructor error", e);
		}
	}

	/**
	 * 특정 학생이 수강 중인 강의 목록 조회 (학생용)
	 */
	public List<LectureDTO> findByStudent(Long studentId) {
		String sql = 
			"SELECT l.lecture_id, l.user_id, l.lecture_title, l.lecture_round, " +
			"       l.start_date, l.end_date, l.status, l.room, l.capacity, " +
			"       l.created_at, l.updated_at, l.validation, l.section " +
			"FROM lecture l " +
			"INNER JOIN enrollments e ON l.lecture_id = e.lecture_id " +
			"WHERE l.validation = 'CONFIRMED' AND e.user_id = ? AND e.status = 'ACTIVE' " +
			"ORDER BY l.lecture_title, l.lecture_round";
		
		List<LectureDTO> list = new ArrayList<>();
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setLong(1, studentId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToDTO(rs));
				}
			}
			
			return list;
			
		} catch (Exception e) {
			throw new RuntimeException("LectureDAO.findByStudent error", e);
		}
	}

	/**
	 * 강의 ID로 강의 정보 조회
	 */
	public LectureDTO findById(Long lectureId) {
		String sql = 
			"SELECT lecture_id, user_id, lecture_title, lecture_round, " +
			"       start_date, end_date, status, room, capacity, " +
			"       created_at, updated_at, validation, section " +
			"FROM lecture " +
			"WHERE lecture_id = ?";
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setLong(1, lectureId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToDTO(rs);
				}
			}
			
			return null;
			
		} catch (Exception e) {
			throw new RuntimeException("LectureDAO.findById error", e);
		}
	}

	/**
	 * ResultSet을 LectureDTO로 매핑
	 */
	private LectureDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
		LectureDTO dto = new LectureDTO();
		
		dto.setLectureId(rs.getLong("lecture_id"));
		dto.setUserId(rs.getLong("user_id"));
		dto.setLectureTitle(rs.getString("lecture_title"));
		dto.setLectureRound(rs.getInt("lecture_round"));
		
		// Date -> LocalDate 변환
		Date startDate = rs.getDate("start_date");
		Date endDate = rs.getDate("end_date");
		dto.setStartDate(startDate != null ? startDate.toLocalDate() : null);
		dto.setEndDate(endDate != null ? endDate.toLocalDate() : null);
		
		// String -> Enum 변환
		String statusStr = rs.getString("status");
		dto.setStatus(statusStr != null ? LectureStatus.valueOf(statusStr) : null);
		
		String validationStr = rs.getString("validation");
		dto.setValidation(validationStr != null ? LectureValidation.valueOf(validationStr) : null);
		
		dto.setRoom(rs.getString("room"));
		dto.setCapacity(rs.getInt("capacity"));
		dto.setSection(rs.getString("section"));
		
		// Timestamp -> LocalDateTime 변환
		Timestamp createdAt = rs.getTimestamp("created_at");
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		dto.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
		dto.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
		
		return dto;
	}
	
	
	
	
}
