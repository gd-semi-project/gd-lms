package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.dto.EnrollmentDTO;
import model.dto.LectureRequestDTO;
import model.dto.LectureStudentDTO;
import model.enumtype.EnrollmentStatus;
import model.enumtype.LectureValidation;

public class EnrollmentDAO {

	private static final EnrollmentDAO instance = new EnrollmentDAO();

	private EnrollmentDAO() {
	}

	public static EnrollmentDAO getInstance() {
		return instance;
	}

	public ArrayList<LectureRequestDTO> getLectureList(String validation, Long departmentId) {

		if (validation == null || validation.isEmpty())
			validation = "CONFIRMED";
		String sql = """
				SELECT
				    l.lecture_id    AS lectureId,
				    l.lecture_title AS lectureTitle,
				    l.section       AS section,
				    l.capacity      AS capacity,
				    l.validation    AS validation,
				    l.created_at    AS createdAt,
				    u.name          AS instructorName,
				    d.department_id AS departmentId,
				    GROUP_CONCAT(
				        CONCAT(
				            s.week_day, ' ',
				            TIME_FORMAT(s.start_time, '%H:%i'), '~',
				            TIME_FORMAT(s.end_time, '%H:%i')
				        )
				        ORDER BY FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN')
				        SEPARATOR ' <br> '
				    ) AS schedule
				FROM lecture l
				JOIN lecture_schedule s
				    ON l.lecture_id = s.lecture_id
				JOIN user u
				    ON u.user_id = l.user_id
				JOIN department d
				    ON d.department_id = l.department_id
				WHERE l.validation = ?
				""";
		if (departmentId != null) {
			sql += " AND d.department_id = ?";
		}
		sql += """
				   GROUP BY
				       l.lecture_id,
				       l.lecture_title,
				       l.section,
				       l.capacity,
				       l.validation,
				       l.created_at,
				       u.name,
				       d.department_id
				   ORDER BY
				       l.lecture_id,
				       l.section;
				""";

		ArrayList<LectureRequestDTO> list = new ArrayList<LectureRequestDTO>();

		// TODO 뭔가 정리 기준을 추가할 수 있는 기능 sql+="ORDER BY =?" 수정 및 추가

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, validation);
			if (departmentId != null) {
				pstmt.setLong(2, departmentId);
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LectureRequestDTO dto = new LectureRequestDTO();
					dto.setLectureId(rs.getLong("lectureId"));
					dto.setLectureTitle(rs.getString("lectureTitle"));
					dto.setSection(rs.getString("section"));
					dto.setSchedule(rs.getString("schedule"));
					dto.setCapacity(rs.getInt("capacity"));
					dto.setValidation(LectureValidation.valueOf(rs.getString("validation")));
					dto.setCreatedAt((rs.getTimestamp("createdAt").toLocalDateTime()));
					dto.setInstructorName(rs.getString("instructorName"));
					list.add(dto);
				}
			}
		} catch (Exception e) {
			// TODO 에러가 난다면 sql 문법이나 틀린 값을 받았을 경우입니다.
			System.out.println("getLectureList() 예외 발생");
			e.printStackTrace();
		}

		return list;
	}

	// 학생ID로 특정강의ID 이수 확인 메소드
	public boolean isStudentEnrolled(Connection conn, long userId, long lectureId) throws SQLException {
		String sql = """
				    SELECT 1
				    FROM enrollment
				    WHERE user_id = ?
				      AND lecture_id = ?
				      AND status = ?
				    LIMIT 1
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, lectureId);
			ps.setString(3, EnrollmentStatus.ENROLLED.name()); // "ENROLLED"

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	// 특정 학생이 듣는 모든 강의 리스트 메소드
	public List<Long> findEnrolledLectureIds(Connection conn, long userId) throws SQLException {
		String sql = """
				    SELECT lecture_id
				    FROM enrollment
				    WHERE user_id = ?
				      AND status = ?
				""";

		List<Long> lectureIds = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setString(2, EnrollmentStatus.ENROLLED.name());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lectureIds.add(rs.getLong("lecture_id"));
				}
			}
		}
		return lectureIds;
	}

	// 해당 강의르 몇명이 수강하고있냐를 알아내는 메소드
	public int countStudentsByLecture(Connection conn, long lectureId) throws SQLException {
		String sql = """
				    SELECT COUNT(*) AS cnt
				    FROM enrollment
				    WHERE lecture_id = ?
				      AND status = ?
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, lectureId);
			ps.setString(2, EnrollmentStatus.ENROLLED.name());

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getInt("cnt") : 0;
			}
		}
	}

	public List<Long> findMyLectureId(long studentId) {
		String sql = """
				    SELECT lecture_id
				    FROM enrollment
				    WHERE user_id = ?
				      AND status = 'ENROLLED'
				""";

		List<Long> list = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, studentId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(rs.getLong("lecture_id"));
			}

		} catch (Exception e) {
			throw new RuntimeException("내 수강 강의 ID 조회 실패", e);
		}

		return list;
	}

	// (수강신청) 중복과목 신청유무 확인
	public boolean dpCheck(Connection conn, long studentId, long lectureId) {
		String sql = """
				SELECT 1
				FROM enrollment
				WHERE user_id = ?
				  AND lecture_id = ?
				  AND status = 'ENROLLED'
				""";

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, studentId);
			pstmt.setLong(2, lectureId);

			ResultSet rs = pstmt.executeQuery();

			return rs.next();

		} catch (Exception e) {
			throw new RuntimeException("중복 신청 체크 실패", e);
		}
	}

	// 시간표 겹침 체크(같은 시간대에 시간표가 겹쳐서는 안됨)
	public boolean checkSchedule(Connection conn, long studentId, long lectureId) {
		String sql = """
				    SELECT 1
				    FROM enrollment e
				    JOIN lecture_schedule s1
				      ON e.lecture_id = s1.lecture_id   -- 이미 신청한 강의의 시간표
				    JOIN lecture_schedule s2
				      ON s2.lecture_id = ?              -- 새로 신청하려는 강의의 시간표
				    WHERE e.user_id = ?                 -- 같은 학생
				      AND e.status = 'ENROLLED'          -- 현재 수강 중인 강의만
				      AND s1.week_day = s2.week_day     -- 같은 요일
				      AND s1.start_time < s2.end_time   -- 시간 겹침 조건 ①
				      AND s1.end_time   > s2.start_time -- 시간 겹침 조건 ②
				    LIMIT 1
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, lectureId);
			pstmt.setLong(2, studentId);

			ResultSet rs = pstmt.executeQuery();

			return rs.next();

		} catch (Exception e) {
			throw new RuntimeException("시간표 겹침 체크 실패", e);
		}
	}

	// 수강신청 등록
	public void insertLecture(Connection conn, long studentId, long lectureId) {
		String sql = """
				    INSERT INTO enrollment (lecture_id, user_id, status)
				    VALUES (?, ?, 'ENROLLED')
				    ON DUPLICATE KEY UPDATE
				        status = 'ENROLLED',
				        updated_at = CURRENT_TIMESTAMP
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, lectureId);
			pstmt.setLong(2, studentId);

			pstmt.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("수강신청 등록 실패", e);
		}
	}

	// 수강 신청 상태 확인
	public boolean isEnrolled(Connection conn, long userId, long lectureId) {
		String sql = """
				    SELECT 1
				    FROM enrollment
				    WHERE user_id = ?
				      AND lecture_id = ?
				      AND status = 'ENROLLED'
				    LIMIT 1
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, userId);
			pstmt.setLong(2, lectureId);

			ResultSet rs = pstmt.executeQuery();
			return rs.next();

		} catch (Exception e) {
			throw new RuntimeException("수강 상태 확인 실패", e);
		}
	}

	// 수강취소 처리
	public void dropLecture(Connection conn, long userId, long lectureId) {
		String sql = """
				    UPDATE enrollment
				    SET status = 'DROPPED',
				        updated_at = CURRENT_TIMESTAMP
				    WHERE user_id = ?
				      AND lecture_id = ?
				      AND status = 'ENROLLED'
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, userId);
			pstmt.setLong(2, lectureId);

			pstmt.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("수강취소 실패", e);
		}

	}

	// (수강신청)내가 신청한 강의
	public List<EnrollmentDTO> findByStudentId(long studentId) {
		String sql = """
						        SELECT
				    l.lecture_id,
				    d.department_name,
				    l.lecture_title,
				    u.name AS instructor_name,
				    l.room,
				    l.capacity,
				    COUNT(e2.enrollment_id) AS current_count,
				    GROUP_CONCAT(
				        CONCAT(
				            s.week_day, ' ',
				            TIME_FORMAT(s.start_time, '%H:%i'), '~',
				            TIME_FORMAT(s.end_time, '%H:%i')
				        )
				        ORDER BY FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN')
				        SEPARATOR ', '
				    ) AS schedule
				FROM enrollment e
				JOIN lecture l ON e.lecture_id = l.lecture_id
				JOIN department d ON l.department_id = d.department_id
				JOIN user u ON l.user_id = u.user_id
				LEFT JOIN lecture_schedule s ON l.lecture_id = s.lecture_id
				LEFT JOIN enrollment e2
				       ON e2.lecture_id = l.lecture_id
				      AND e2.status = 'ENROLLED'
				WHERE e.user_id = ?
				  AND e.status = 'ENROLLED'
				  AND l.status IN ('PLANNED','ONGOING')
				GROUP BY
				    l.lecture_id,
				    d.department_name,
				    l.lecture_title,
				    u.name,
				    l.room,
				    l.capacity;
						    """;

		List<EnrollmentDTO> list = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, studentId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				EnrollmentDTO dto = new EnrollmentDTO();
				dto.setLectureId(rs.getLong("lecture_id"));
				dto.setDepartmentName(rs.getString("department_name"));
				dto.setLectureTitle(rs.getString("lecture_title"));
				dto.setInstructorName(rs.getString("instructor_name"));
				dto.setRoom(rs.getString("room"));
				dto.setSchedule(rs.getString("schedule"));
				dto.setCapacity(rs.getInt("capacity"));
				dto.setCurrentCount(rs.getInt("current_count"));

				list.add(dto);
			}

		} catch (Exception e) {
			throw new RuntimeException("수강신청 내역 조회 실패", e);
		}

		return list;
	}

}