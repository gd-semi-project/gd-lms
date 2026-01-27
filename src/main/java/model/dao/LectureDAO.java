package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DBConnection;
import model.dto.LectureDTO;
import model.dto.LectureForEnrollDTO;
import model.dto.LectureStudentDTO;
import model.dto.MyLectureDTO;
import model.dto.MyscheduleDTO;
import model.dto.UserDTO;
import model.enumtype.EnrollmentStatus;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;

public class LectureDAO {

	private static final LectureDAO instance = new LectureDAO();

	private LectureDAO() {
	}

	public static LectureDAO getInstance() {
		return instance;
	}

	public int getLectureCount() { // 개설된 강의 개수 (이름 중첩x)
		final String sql = """
				SELECT COUNT(DISTINCT lecture_title) AS cnt
				FROM lecture
				WHERE status = 'PLANNED'
				AND validation = 'CONFIRMED'
				""";
		return queryForInt(sql);
	}

	public int getTotalLectureCount() { // 모든 강의 개수 (이름 중첩o)
		final String sql = """
				SELECT COUNT(*) AS cnt
				FROM lecture
				WHERE status = 'PLANNED'
				AND validation = 'CONFIRMED'
				""";
		return queryForInt(sql);
	}

	public int getLectureFillRate() { // 정원/인원
		final String sql = """
				SELECT
					COALESCE(SUM(l.capacity), 0) AS cap_sum,
					COALESCE(SUM(CASE WHEN e.status = 'ENROLLED' THEN 1 ELSE 0 END), 0) AS cur_sum
				FROM lecture l
				LEFT JOIN enrollment e ON e.lecture_id = l.lecture_id
				WHERE l.status = 'PLANNED'
				AND l.validation = 'CONFIRMED'
				""";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			if (!rs.next())
				return 0;

			int capSum = rs.getInt("cap_sum");
			int curSum = rs.getInt("cur_sum");

			if (capSum <= 0)
				return 0;

			return (curSum * 100) / capSum;
            } catch (Exception e) {
            	//TODO sql 문법 오류나 DB 가 없는 경우 에러가 날 수 있습니다
                e.printStackTrace();
                return 0;
            }
	}

	public int getLowFillRateLecture() { // 정원/인원이 50% 미만인 모든 강의 수
		final String sql = """

				SELECT COUNT(*) AS cnt
				FROM (
				    SELECT
				        l.lecture_id,
				        l.capacity,
				        COALESCE(SUM(CASE WHEN e.status='ENROLLED' THEN 1 ELSE 0 END), 0) AS enrolled_cnt
				    FROM lecture l
				    LEFT JOIN enrollment e
				      ON e.lecture_id = l.lecture_id
				    WHERE l.status = 'PLANNED'
				      AND l.validation = 'CONFIRMED'
				    GROUP BY l.lecture_id, l.capacity
				) t
				WHERE t.capacity > 0
				  AND (t.enrolled_cnt * 100) < (t.capacity * 50)
				""";
		return queryForInt(sql);
	}

	public int getTotalLectureCapacity() { // 모든 정원 수
		final String sql =

				"""
						SELECT COALESCE(SUM(capacity), 0) AS cnt
						FROM lecture
						WHERE status = 'PLANNED'
						AND validation = 'CONFIRMED'
						""";

		return queryForInt(sql);
	}

	public int getTotalEnrollment() { // 모든 수강 인원 수
		final String sql = """
				SELECT COUNT(*) AS cnt
				FROM `user`
				WHERE role = 'STUDENT'
				AND status = 'ACTIVE'
				""";
		return queryForInt(sql);

	}

	// ======================================================
	// 1) 교수 강의 목록 (validation = CONFIRMED)
	// ======================================================
	public List<LectureDTO> selectLecturesByInstructor(Connection conn, long instructorId, String status)
			throws SQLException {

		String sql = """
				    SELECT
				        lecture_id,
				        user_id,
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
				      AND status = ?
				    ORDER BY start_date DESC
				""";

		List<LectureDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, instructorId);
			pstmt.setString(2, status);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(mapLecture(rs));
				}
			}
		}
		return list;
	}

	// ======================================================
	// 2) 학생 수강 강의 목록
	// enrollment.user_id 기준
	// ======================================================
	public List<LectureDTO> selectLecturesByStudent(Connection conn, long userId) throws SQLException {

		String sql = """
				    SELECT
				        l.lecture_id,
				        l.user_id,
				        l.lecture_title,
				        l.lecture_round,
				        l.section,
				        l.start_date,
				        l.end_date,
				        l.room,
				        l.capacity,
				        l.status,
				        l.validation,
				        l.created_at,
				        l.updated_at
				        
				    FROM enrollment e
				    JOIN lecture l ON e.lecture_id = l.lecture_id
				    WHERE e.user_id = ?
				      AND e.status = 'ENROLLED'
				      AND l.validation = 'CONFIRMED'
				      AND l.status = 'ONGOING'
				    ORDER BY l.start_date DESC
				""";

		List<LectureDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(mapLecture(rs));
				}
			}
		}
		return list;
	}

	// ======================================================
	// 3) 강의별 수강생 목록
	// - enrollment.user_id 기반
	// - student 테이블은 부가정보(학번/학년/학생PK) 필요할 때만 LEFT JOIN
	// - enrollment에 applied_at 없으므로 created_at을 등록일로 사용
	// ======================================================
	public List<LectureStudentDTO> selectLectureStudents(Connection conn, long lectureId) throws SQLException {

		String sql = """
				    SELECT
				        s.student_id,
				        u.user_id,
				        u.name AS student_name,
				        s.student_number,
				        s.student_grade,
				        e.status AS enrollment_status,
				        e.created_at AS enrolled_at
				    FROM enrollment e
				    JOIN user u ON e.user_id = u.user_id
				    LEFT JOIN student s ON s.user_id = e.user_id
				    WHERE e.lecture_id = ?
				      AND e.status = 'ENROLLED'
				    ORDER BY s.student_number IS NULL, s.student_number, u.user_id
				""";

		List<LectureStudentDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LectureStudentDTO dto = new LectureStudentDTO();

					// student_id는 LEFT JOIN이라 NULL 가능
					long sid = rs.getLong("student_id");
					if (rs.wasNull())
						sid = 0L;
					dto.setStudentId(sid);

					dto.setUserId(rs.getLong("user_id"));
					dto.setStudentName(rs.getString("student_name"));

					int studentNumber = rs.getInt("student_number");
					if (rs.wasNull())
						studentNumber = 0;
					dto.setStudentNumber(studentNumber);

					int studentGrade = rs.getInt("student_grade");
					if (rs.wasNull())
						studentGrade = 0;
					dto.setStudentGrade(studentGrade);

					dto.setEnrollmentStatus(EnrollmentStatus.valueOf(rs.getString("enrollment_status")));

					// 기존 DTO에 appliedAt만 있다면 enrolled_at(created_at)으로 채워도 됨
					Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
					dto.setAppliedAt(enrolledAt != null ? enrolledAt.toLocalDateTime() : null);

					list.add(dto);
				}
			}
		}
		return list;
	}

	// ======================================================
	// 4) 강의 상세 조회
	// ======================================================
	public LectureDTO selectLectureById(Connection conn, long lectureId) throws SQLException {

		String sql = """
				    SELECT
				        lecture_id,
				        user_id,
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
				    WHERE lecture_id = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next())
					return null;
				return mapLecture(rs);
			}
		}
	}

	// ======================================================
	// 공지사항 화면에서 쓰는 “단순 조회” 래퍼들
	// ======================================================
	
	
	// 전체 강의 조회(확정된)
	public List<LectureDTO> findAll(Connection conn) throws SQLException {
		String sql = "SELECT lecture_id, user_id, lecture_title, lecture_round, section, "
				+ "       start_date, end_date, room, capacity, status, validation, created_at, updated_at "
				+ "FROM lecture " + "WHERE validation = 'CONFIRMED' " + "ORDER BY lecture_title, lecture_round";

		List<LectureDTO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {

			while (rs.next())
				list.add(mapLecture(rs));
			return list;

		}
	}

	// 교수 기준 강의 조회
	public List<LectureDTO> findByInstructor(Connection conn,long instructorId) throws SQLException{
		
		String status = "ONGOING";
		return selectLecturesByInstructor(conn, instructorId, status);
		
	}

	// 학생 기준 강의 조회
	public List<LectureDTO> findByStudent(Connection conn,long userId) throws SQLException {
		
		return selectLecturesByStudent(conn, userId);
		
	}

	// 강의 단건 조회
	public LectureDTO findById(Connection conn, long lectureId) throws SQLException{

		return selectLectureById(conn, lectureId);
		
		
	}

	// ======================================================
	// validation 업데이트 (오타 수정: valdidation -> validation)
	// ======================================================
	public int setLectureValidation(String validation, long lectureId) {
		String sql = "UPDATE lecture SET validation = ? WHERE lecture_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, validation);
			pstmt.setLong(2, lectureId);
			return pstmt.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("LectureDAO.setLectureValidation error", e);
		}
	}

	// ======================================================
	// Mapper
	// ======================================================
	private LectureDTO mapLecture(ResultSet rs) throws SQLException {
		LectureDTO dto = new LectureDTO();

		dto.setLectureId(rs.getLong("lecture_id"));
		dto.setUserId(rs.getLong("user_id"));
		dto.setLectureTitle(rs.getString("lecture_title"));
		dto.setLectureRound(rs.getInt("lecture_round"));
		dto.setSection(rs.getString("section"));

		Date startDate = rs.getDate("start_date");
		Date endDate = rs.getDate("end_date");
		dto.setStartDate(startDate != null ? startDate.toLocalDate() : null);
		dto.setEndDate(endDate != null ? endDate.toLocalDate() : null);

		dto.setRoom(rs.getString("room"));
		dto.setCapacity(rs.getInt("capacity"));

		String status = rs.getString("status");
		dto.setStatus(status != null ? LectureStatus.valueOf(status) : null);

		String validation = rs.getString("validation");
		dto.setValidation(validation != null ? LectureValidation.valueOf(validation) : null);

		Timestamp createdAt = rs.getTimestamp("created_at");
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		dto.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
		dto.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

		try {
			dto.setDepartmentId(rs.getLong("department_id"));
			dto.setInstructorName(rs.getString("instructor_name"));
		} catch (Exception e) {
		}

		return dto;
	}

	// 학생 개인이 수강하고있는 강의목록
	public List<MyLectureDTO> selectMyEnrollmentedLecture(long userId) {
		String sql = """
				           SELECT
				    l.lecture_id,
				    l.lecture_title,
				    l.section,
				    l.room,
				    l.start_date,
				    l.end_date,
				    u.name AS instructor_name,
				    GROUP_CONCAT(
				        CONCAT(
				            CASE s.week_day
				                WHEN 'MON' THEN '월'
				                WHEN 'TUE' THEN '화'
				                WHEN 'WED' THEN '수'
				                WHEN 'THU' THEN '목'
				                WHEN 'FRI' THEN '금'
				                WHEN 'SAT' THEN '토'
				                WHEN 'SUN' THEN '일'
				            END,
				            ' ',
				            TIME_FORMAT(s.start_time, '%H:%i'),
				            '~',
				            TIME_FORMAT(s.end_time, '%H:%i')
				        )
				        ORDER BY FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN')
				        SEPARATOR ' / '
				    ) AS schedule
				FROM enrollment e
				JOIN lecture l ON e.lecture_id = l.lecture_id
				JOIN user u ON l.user_id = u.user_id
				JOIN lecture_schedule s ON l.lecture_id = s.lecture_id
				WHERE e.user_id = ?
				  AND e.status = 'ENROLLED'
				  AND l.validation = 'CONFIRMED'
				  AND l.status = 'ONGOING'
				GROUP BY
				    l.lecture_id,
				    l.lecture_title,
				    l.section,
				    l.room,
				    l.start_date,
				    l.end_date,
				    u.name
				ORDER BY l.start_date
				        """;

		List<MyLectureDTO> list = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, userId);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				MyLectureDTO lecture = new MyLectureDTO();
				lecture.setLectureId(rs.getLong("lecture_id"));
				lecture.setLectureTitle(rs.getString("lecture_title"));
				lecture.setSection(rs.getString("section"));
				lecture.setRoom(rs.getString("room"));
				lecture.setStartDate(rs.getDate("start_date").toLocalDate());
				lecture.setEndDate(rs.getDate("end_date").toLocalDate());
				lecture.setInstructorName(rs.getString("instructor_name"));
				lecture.setSchedule(rs.getString("schedule"));
				list.add(lecture);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return list;
	}

	// 학생 개인의 시간표
	public List<MyscheduleDTO> selectMySchedule(long userId) {
		String sql = """
				    SELECT
				        s.week_day,
				        HOUR(s.start_time) AS start_hour,
				        HOUR(s.end_time)   AS end_hour,
				        l.lecture_title
				    FROM enrollment e
				    JOIN lecture l ON e.lecture_id = l.lecture_id
				    JOIN lecture_schedule s ON l.lecture_id = s.lecture_id
				    WHERE e.user_id = ?
				      AND e.status = 'ENROLLED'
				      AND l.validation = 'CONFIRMED'
				AND l.status = 'ONGOING'
				    ORDER BY FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN'),
				             s.start_time
				""";

		List<MyscheduleDTO> list = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, userId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				MyscheduleDTO dto = new MyscheduleDTO();
				dto.setWeekDay(rs.getString("week_day"));
				dto.setStartHour(rs.getInt("start_hour"));
				dto.setEndHour(rs.getInt("end_hour"));
				dto.setLectureTitle(rs.getString("lecture_title"));
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// 학생 수강신청 관련

	// 수강신청페이지에 들어갈 시 떠야하는 개설 강의목록을 나타내주는 메소드
	public List<LectureForEnrollDTO> findAvailableLecturesForEnroll(Long departmentId, String keyword) {
		StringBuilder sql = new StringBuilder("""
				    SELECT
				        l.lecture_id,
				        d.department_name AS departmentName,
				        l.lecture_title   AS lectureTitle,
				        u.name            AS instructorName,
				        l.room            AS room,
				        l.capacity        AS capacity,

				        COUNT(e.enrollment_id) AS currentCount,

				        GROUP_CONCAT(
				            CONCAT(
				                ls.week_day, ' ',
				                DATE_FORMAT(ls.start_time, '%H:%i'),
				                '~',
				                DATE_FORMAT(ls.end_time, '%H:%i')
				            )
				            ORDER BY FIELD(ls.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN'),
				                     ls.start_time
				            SEPARATOR ', '
				        ) AS schedule

				    FROM lecture l
				    JOIN department d ON l.department_id = d.department_id
				    JOIN user u       ON l.user_id = u.user_id
				    LEFT JOIN lecture_schedule ls ON l.lecture_id = ls.lecture_id
				    LEFT JOIN enrollment e
				           ON l.lecture_id = e.lecture_id
				          AND e.status = 'ENROLLED'

				    WHERE l.status IN ('PLANNED','ONGOING')
				      AND l.validation = 'CONFIRMED'
				""");

		List<Object> params = new ArrayList<>();
		
		// 학과 검색
		if (departmentId != null) {
			sql.append(" AND l.department_id = ? ");
			params.add(departmentId);
		}

		// 과목명 검색
		if (keyword != null && !keyword.isBlank()) {
			sql.append(" AND l.lecture_title LIKE ? ");
			params.add("%" + keyword + "%");
		}

		sql.append("""
				    GROUP BY
				        l.lecture_id,
				        d.department_name,
				        l.lecture_title,
				        u.name,
				        l.room,
				        l.capacity
				    ORDER BY l.lecture_id DESC
				""");

		List<LectureForEnrollDTO> list = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

			int idx = 1;

			if (departmentId != null) {
				pstmt.setLong(idx++, departmentId);
			}

			if (keyword != null && !keyword.isBlank()) {
				pstmt.setString(idx++, "%" + keyword + "%");
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LectureForEnrollDTO dto = new LectureForEnrollDTO();

					dto.setLectureId(rs.getLong("lecture_id"));
					dto.setDepartmentName(rs.getString("departmentName"));
					dto.setLectureTitle(rs.getString("lectureTitle"));
					dto.setInstructorName(rs.getString("instructorName"));
					dto.setRoom(rs.getString("room"));
					dto.setCapacity(rs.getInt("capacity"));
					dto.setCurrentCount(rs.getInt("currentCount"));
					dto.setSchedule(rs.getString("schedule"));

					list.add(dto);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int updateStatus(Connection conn, LectureStatus from, LectureStatus to) throws SQLException {

		String sql = """
				    UPDATE lecture
				    SET status = ?
				    WHERE status = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, to.name());
			pstmt.setString(2, from.name());
			return pstmt.executeUpdate();
		}
	}

	public int cancelExpiredLectureRequest() {
		final String sql = """
				UPDATE lecture
				SET validation = 'CANCELED'
				WHERE validation = 'PENDING'
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			return pstmt.executeUpdate(); // 취소된 건수 반환

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int markOnGoing(LocalDate today) {
		final String sql = """
				UPDATE lecture
				      SET status = 'ONGOING'
				      WHERE validation = 'CONFIRMED'
				        AND status = 'PLANNED'
				        AND start_date <= ?
				  """;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);

		) {

			pstmt.setDate(1, java.sql.Date.valueOf(today));

			return pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("markOnGoing(): 실패");
			e.printStackTrace();

			return 0;
		}

	};

	public int markEnded(LocalDate today) {
		final String sql = """
				    UPDATE lecture
				    SET status = 'ENDED'
				    WHERE status <> 'ENDED'
				      AND end_date < ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDate(1, java.sql.Date.valueOf(today));

			return pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("markOnGoing(): 실패");
			e.printStackTrace();

			return 0;
		}
	}

	public List<LectureDTO> findLectureByDepartment(long departmentId) {

		final String sql = """
				SELECT
				  l.lecture_id,
				  l.lecture_title,
				  l.lecture_round,
				  l.user_id,
				  u.name AS instructor_name,
				  l.department_id AS department_id,
				  l.start_date,
				  l.end_date,
				  l.room,
				  l.capacity,
				  l.status,
				  l.validation,
				  l.section,
				  l.created_at,
				  l.updated_at
				FROM lecture l
				JOIN user u ON u.user_id = l.user_id
				WHERE l.department_id = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {

			pstmt.setLong(1, departmentId);

			try (ResultSet rs = pstmt.executeQuery()) {

				List<LectureDTO> list = new ArrayList<LectureDTO>();
				while (rs.next()) {
					list.add(mapLecture(rs));
				}
				return list;
			}

		} catch (Exception e) {
			System.out.println("findLectureByDepartment():실패");
			return null;
		}
	}

	public List<LectureDTO> findByDepartmentAndStatus(long departmentId, String lectureStatus) {
		final String sql = """
				SELECT
				  l.lecture_id,
				  l.lecture_title,
				  l.lecture_round,
				  l.user_id,
				  u.name AS instructor_name,
				  l.department_id,
				  l.start_date,
				  l.end_date,
				  l.room,
				  l.capacity,
				  l.status,
				  l.validation,
				  l.section,
				  l.created_at,
				  l.updated_at
				FROM lecture l
				JOIN user u ON u.user_id = l.user_id
				WHERE l.department_id = ?
				AND l.status = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, departmentId);
			pstmt.setString(2, lectureStatus);

			try (ResultSet rs = pstmt.executeQuery()) {

				List<LectureDTO> list = new ArrayList<LectureDTO>();
				while (rs.next())
					list.add(mapLecture(rs));
				return list;
			}
		} catch (Exception e) {
			System.out.println("findByDepartmentAndStatus(): 실패");
			return null;
		}
	}

	public Map<Long, Integer> selectEnrollCountMapByLectureIds(List<Long> lectureIds) {
		if (lectureIds == null || lectureIds.isEmpty()) {
			return Collections.emptyMap();
		}

		String placeholders = lectureIds.stream().map(id -> "?").collect(java.util.stream.Collectors.joining(","));

		String sql = "SELECT lecture_id, COUNT(*) AS cnt " + "FROM enrollment " + "WHERE status = 'ENROLLED' "
				+ "AND lecture_id IN (" + placeholders + ") " + "GROUP BY lecture_id";

		Map<Long, Integer> map = new HashMap<Long, Integer>();

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			int idx = 1;
			for (Long id : lectureIds) {
				pstmt.setLong(idx++, id);
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					long lectureId = rs.getLong("lecture_id");
					int cnt = rs.getInt("cnt");
					map.put(lectureId, cnt);
				}
			}

		} catch (Exception e) {
			System.out.println("selectEnrol 어쩌고 그 긴거 오류났음");
		}
		return map;

	}

	// 정원 체크(정원이 모두 차면 신청불가)
	public boolean checkCapacity(Connection conn, long lectureId) {
		String sql = """
				    SELECT
				        l.capacity,
				        COUNT(e.enrollment_id) AS enrolled_count
				    FROM lecture l
				    LEFT JOIN enrollment e
				      ON l.lecture_id = e.lecture_id
				     AND e.status = 'ENROLLED'
				    WHERE l.lecture_id = ?
				    GROUP BY l.capacity
				    FOR UPDATE
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, lectureId);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				// 강의 자체가 없으면 신청 불가
				return false;
			}

			int capacity = rs.getInt("capacity");
			int enrolledCount = rs.getInt("enrolled_count");

			// 정원보다 작으면 신청 가능
			return enrolledCount < capacity;

		} catch (Exception e) {
			throw new RuntimeException("정원 체크 실패", e);
		}
	}

	private int queryForInt(String sql) {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next())
				return rs.getInt("cnt");
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	public List<LectureDTO> getAllLecture() {
		final String sql = """
				      SELECT
				        l.lecture_id,
				        l.lecture_title,
				        l.lecture_round,
				        l.user_id,
				        u.name AS instructor_name,
				        l.department_id AS department_id,
				        l.start_date,
				        l.end_date,
				        l.room,
				        l.capacity,
				        l.status,
				        l.validation,
				        l.section,

				COALESCE(
				     GROUP_CONCAT(
				       CONCAT(
				         s.week_day, ' ',
				         TIME_FORMAT(s.start_time, '%H:%i'), '~',
				         TIME_FORMAT(s.end_time, '%H:%i')
				       )
				       ORDER BY FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN'),
				                s.start_time
				       SEPARATOR ' <br> '
				     ),
				     '-'
				   ) AS scheduleHtml

				      FROM lecture l
				      JOIN user u ON u.user_id = l.user_id
				      LEFT JOIN lecture_schedule s ON s.lecture_id = l.lecture_id
				      WHERE l.status = 'PLANNED'
				      AND l.validation = 'CONFIRMED'
				      GROUP BY
				      l.lecture_id,
				   l.lecture_title,
				   l.section,
				   l.capacity,
				   l.status,
				   l.validation,
				   l.created_at,
				   u.name,
				   l.department_id
				      """;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {

			try (ResultSet rs = pstmt.executeQuery()) {

				List<LectureDTO> list = new ArrayList<LectureDTO>();
				while (rs.next()) {
					LectureDTO dto = new LectureDTO();
					dto.setLectureId(rs.getLong("lecture_id"));
					dto.setLectureTitle(rs.getString("lecture_title"));
					dto.setSection(rs.getString("section"));
					dto.setCapacity(rs.getInt("capacity"));
					dto.setInstructorName(rs.getString("instructor_name"));
					dto.setDepartmentId(rs.getLong("department_id"));
					dto.setScheduleHtml(rs.getString("scheduleHtml"));
					dto.setRoom(rs.getString("room"));
					Date startDate = rs.getDate("start_date");
					Date endDate = rs.getDate("end_date");
					dto.setStartDate(startDate != null ? startDate.toLocalDate() : null);
					dto.setEndDate(endDate != null ? endDate.toLocalDate() : null);
					list.add(dto);
				}
				return list;
			}

		} catch (Exception e) {
			System.out.println("getAllLecture():실패");
			e.printStackTrace();
			return null;
		}
	}

}