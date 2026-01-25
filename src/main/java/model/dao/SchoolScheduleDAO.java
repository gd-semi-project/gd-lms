package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

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