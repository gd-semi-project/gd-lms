package automation.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import database.DBConnection;
import model.dto.SchoolScheduleDTO;
import model.enumtype.ScheduleCode;

public class SchoolScheduleDAOImpl implements SchoolScheduleDAO {

	@Override
	public LocalDate findEndDateByCode(String scheduleCode) {
		
		final String sql = """
				SELECT end_date
				FROM schoolSchedule
				WHERE schedule_code = ?
				LIMIT 1
				""";
		
		try (
				Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				){
			
			pstmt.setString(1, scheduleCode);
			
			try (ResultSet rs = pstmt.executeQuery()){
				if (!rs.next()) return null;
				return rs.getDate("end_date").toLocalDate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public SchoolScheduleDTO findByCode(String scheduleCode) {
	    final String sql = """
				            SELECT schedule_code, title, start_date, end_date, memo
				            FROM schoolSchedule
				            WHERE schedule_code = ?
				            LIMIT 1
    					""";
	    
	    try (	Connection conn = DBConnection.getConnection();
	    		PreparedStatement pstmt = conn.prepareStatement(sql)
	    		){
	    	
	    	pstmt.setString(1, scheduleCode);
	    	
	    	try(ResultSet rs = pstmt.executeQuery()){
	    		if (!rs.next()) return null;
	    		
	    		SchoolScheduleDTO dto = new SchoolScheduleDTO();
	            dto.setScheduleCode(ScheduleCode.valueOf(rs.getString("schedule_code")));
	            dto.setTitle(rs.getString("title"));
	            dto.setStartDate(rs.getDate("start_date").toLocalDate());
	            dto.setEndDate(rs.getDate("end_date").toLocalDate());
	            dto.setMemo(rs.getString("memo"));
	            return dto;
	    	}
	    } catch (Exception e ) {
	    	e.printStackTrace();
	    	return null;
	    }
	}

	@Override
	public SchoolScheduleDTO findByCodeAndEndDate(String scheduleCode, LocalDate endDate) {
	  final String sql = """
		        SELECT schedule_code, title, start_date, end_date, memo
		        FROM schoolSchedule
		        WHERE schedule_code = ?
		          AND end_date = ?
		        LIMIT 1
		    """;

		    try (Connection conn = DBConnection.getConnection();
		         PreparedStatement pstmt = conn.prepareStatement(sql)) {

		        pstmt.setString(1, scheduleCode);
		        pstmt.setDate(2, java.sql.Date.valueOf(endDate));

		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (!rs.next()) return null;

		            SchoolScheduleDTO dto = new SchoolScheduleDTO();
		            dto.setScheduleCode(ScheduleCode.valueOf(rs.getString("schedule_code")));
		            dto.setTitle(rs.getString("title"));
		            dto.setStartDate(rs.getDate("start_date").toLocalDate());
		            dto.setEndDate(rs.getDate("end_date").toLocalDate());
		            dto.setMemo(rs.getString("memo"));
		            return dto;
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
	}

	
	
}
