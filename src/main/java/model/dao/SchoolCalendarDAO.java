package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.dto.SchoolScheduleDTO;

public class SchoolCalendarDAO {
	private static final SchoolCalendarDAO instance = new SchoolCalendarDAO();
	
	public static SchoolCalendarDAO getInstance() {
		return instance;
	}
	
	public List<SchoolScheduleDTO> selectByRange(LocalDate from, LocalDate to){
		
		String sql = """
				SELECT id, title, start_date, end_date, memo
				FROM schoolSchedule
				WHERE start_date <= ?
				AND end_date >= ?
				ORDER BY start_date ASC
				""";
		
		List<SchoolScheduleDTO> list = new ArrayList<SchoolScheduleDTO>();
		
		try (
				Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				) {
			
			pstmt.setDate(1, Date.valueOf(to));
			pstmt.setDate(2, Date.valueOf(from));
			
			try (ResultSet rs = pstmt.executeQuery()){
				while (rs.next()) {
					SchoolScheduleDTO schoolScheduleDTO = new SchoolScheduleDTO();
					schoolScheduleDTO.setId(rs.getLong("id"));
					schoolScheduleDTO.setTitle(rs.getString("title"));
					
					Date sd = rs.getDate("start_date");
					Date ed = rs.getDate("end_date");
					
					schoolScheduleDTO.setStartDate(sd==null? null : sd.toLocalDate());
					schoolScheduleDTO.setEndDate(ed==null ? null : ed.toLocalDate());
					schoolScheduleDTO.setMemo(rs.getString("memo"));
					
					list.add(schoolScheduleDTO);
				}
			}
			
		} catch (Exception e) {
			System.out.println("selectByRange(): 실패");
			e.printStackTrace();
		}
		
		return list;
		
	};
	
	public SchoolScheduleDTO selectById(Long id) {
		String sql = """
				
				SELECT id, title, start_date, end_date, memo
				FROM schoolSchedule
				WHERE id = ?
				
				""";
		try (
				Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				) {
			
			pstmt.setLong(1, id);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if(rs.next()) {
					SchoolScheduleDTO dto = new SchoolScheduleDTO();
					dto.setId(rs.getLong("id"));
					dto.setTitle(rs.getString("title"));
					
					Date sd = rs.getDate("start_date");
					Date ed = rs.getDate("end_date");
					
					dto.setStartDate(sd == null ? null : sd.toLocalDate());
					dto.setEndDate(ed == null ? null : ed.toLocalDate());
					dto.setMemo(rs.getString("memo"));
					
					return dto;
				}
			}
			
			
		} catch (Exception e) {
			System.out.println("selectById(): 실패");
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
