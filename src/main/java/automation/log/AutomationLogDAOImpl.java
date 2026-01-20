package automation.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import database.DBConnection;

public class AutomationLogDAOImpl implements AutomationLogDAO {

	@Override
	public boolean tryStart(String jobCode, LocalDate runDate) {
		final String sql = """
				
				INSERT INTO automation_log (job_code, run_date, status, message)
				VALUES (?, ?, 'SUCCESS', 'START')
				
				""";
		
		try (	Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				) {
			
			pstmt.setString(1, jobCode);
			pstmt.setDate(2, java.sql.Date.valueOf(runDate));
			pstmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			if (isDuplicateKey(e)) return false;
			throw new RuntimeException("automation_log tryStart 실패", e);
		} catch (Exception e) {
			e.printStackTrace(); return false;
		}
		
		
	}

	@Override
	public void markSuccess(String jobCode, LocalDate runDate, String message) {
		updateStatus(jobCode, runDate, "SUCCESS", message);
	}


	@Override
	public void markFail(String jobCode, LocalDate runDate, String message) {
		updateStatus(jobCode, runDate, "FAIL", message);
	}

	private void updateStatus(String jobCode, LocalDate runDate, String status, String message) {
		final String sql = """
				
				UPDATE automation_log
				SET status = ?, message = ?
				WHERE job_code = ? AND run_date = ?
				
				""";
		
		try (	Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql) ){
			
			pstmt.setString(1, status);
			pstmt.setString(2, message);
			pstmt.setString(3, jobCode);
			pstmt.setDate(4, java.sql.Date.valueOf(runDate));
			pstmt.executeUpdate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isDuplicateKey(SQLException e) {
		return "23000".equals(e.getSQLState()) || e.getErrorCode() == 1062;
		
		// 23000 : 무결성 제약 위반
		// 1062 : MySQL의 Duplicate entry 에러 코드
		
		// 둘 중 하나라도 걸리면 중복키라는 거임
	}
}
