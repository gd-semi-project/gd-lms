package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import database.DBConnection;
import model.dto.SchoolScheduleDTO;
import model.enumtype.ScheduleCode;

public class SchoolScheduleDAO {

	private static final SchoolScheduleDAO instance = new SchoolScheduleDAO();

	private SchoolScheduleDAO() {
	}

	public static SchoolScheduleDAO getInstance() {
		return instance;
	}

	// 가장 가까운 날짜
	public LocalDate findNearestScheduleDate(Connection conn, ScheduleCode code, LocalDate 기준일, boolean isStart) {
		String sql = """
				    SELECT start_date, end_date
				    FROM schoolSchedule
				    WHERE schedule_code = ?
				      AND start_date >= ?
				    ORDER BY start_date ASC
				    LIMIT 1
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, code.name());
			pstmt.setDate(2, java.sql.Date.valueOf(기준일));

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return isStart ? rs.getDate("start_date").toLocalDate() : rs.getDate("end_date").toLocalDate();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("가장 가까운 학사 일정 조회 실패: " + code, e);
		}

		throw new IllegalStateException("다가오는 학사 일정이 존재하지 않습니다: " + code);
	}

	// 가장 가까운 일정 dto
	public SchoolScheduleDTO findNearestSchedule(Connection conn, ScheduleCode code, LocalDate 기준일) {
		String sql = """
				    SELECT id, schedule_code, title, start_date, end_date
				    FROM schoolSchedule
				    WHERE schedule_code = ?
				      AND end_date >= ?
				    ORDER BY start_date ASC
				    LIMIT 1
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, code.name());
			pstmt.setDate(2, java.sql.Date.valueOf(기준일));

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					SchoolScheduleDTO dto = new SchoolScheduleDTO();
					dto.setId(rs.getLong("id"));
					dto.setScheduleCode(code);
					dto.setTitle(rs.getString("title"));
					dto.setStartDate(rs.getDate("start_date").toLocalDate());
					dto.setEndDate(rs.getDate("end_date").toLocalDate());
					return dto;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("가장 가까운 학사 일정 조회 실패", e);
		}

		return null;
	}

	public void scheduleAdd(SchoolScheduleDTO dto) {

		final String sql = """

				INSERT INTO schoolSchedule (
					schedule_code,
					title,
					start_date,
					end_date,
					memo
				) VALUES (?,?,?,?,?)

				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {

			pstmt.setString(1, dto.getScheduleCode().name());
			pstmt.setString(2, dto.getTitle());
			pstmt.setDate(3, Date.valueOf(dto.getStartDate()));
			pstmt.setDate(4, Date.valueOf(dto.getEndDate()));
			pstmt.setString(5, dto.getMemo());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("scheduleAdd(): 실패");
			e.printStackTrace();
		}

	}

	public void scheduleDelete(long id) {
		String sql = "DELETE FROM schoolSchedule WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, id);
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("scheduleDelete(): 실패");
		}
	}

	public void scheduleUpdate(SchoolScheduleDTO dto) {
		String sql = """
				    UPDATE schoolSchedule
				       SET schedule_code = ?,
				           title         = ?,
				           start_date    = ?,
				           end_date      = ?,
				           memo          = ?
				     WHERE id = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, dto.getScheduleCode().name());
			pstmt.setString(2, dto.getTitle().trim());
			pstmt.setDate(3, java.sql.Date.valueOf(dto.getStartDate()));
			pstmt.setDate(4, java.sql.Date.valueOf(dto.getEndDate()));
			pstmt.setString(5, dto.getMemo());
			pstmt.setLong(6, dto.getId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("scheduleUpdate(): 실패");
		}

	}

	public SchoolScheduleDTO findById(long scheduleId) {
		String sql = """
				    SELECT id, schedule_code, title, start_date, end_date, memo, created_at, updated_at
				      FROM schoolSchedule
				     WHERE id = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, scheduleId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next())
					return null;

				SchoolScheduleDTO dto = new SchoolScheduleDTO();
				dto.setId(rs.getLong("id"));

				String code = rs.getString("schedule_code");
				dto.setScheduleCode(code != null ? ScheduleCode.valueOf(code) : null);

				dto.setTitle(rs.getString("title"));

				java.sql.Date sd = rs.getDate("start_date");
				java.sql.Date ed = rs.getDate("end_date");
				dto.setStartDate(sd != null ? sd.toLocalDate() : null);
				dto.setEndDate(ed != null ? ed.toLocalDate() : null);

				dto.setMemo(rs.getString("memo"));

				return dto;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("scheduleDAO findById(): 실패");
			return null;
		}
	}

	// 일정 기간 내인가?
	public boolean isWithinPeriod(Connection conn, ScheduleCode code, LocalDate today) {
		String sql = """
				    SELECT COUNT(*)
				    FROM schoolSchedule
				    WHERE schedule_code = ?
				      AND start_date <= ?
				      AND end_date >= ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, code.name());
			pstmt.setDate(2, java.sql.Date.valueOf(today));
			pstmt.setDate(3, java.sql.Date.valueOf(today));

			ResultSet rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;

		} catch (Exception e) {
			throw new RuntimeException("학사 일정 기간 체크 실패: " + code, e);
		}
	}

}