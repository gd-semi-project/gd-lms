package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DBConnection;
import model.dto.ProfessorDTO;

public class ProfessorDAO {
	private static ProfessorDAO instance = new ProfessorDAO();
	
	private ProfessorDAO() {}
	
	public static ProfessorDAO getInstance() {
		return instance;
	}
	
	public ProfessorDTO findProfessorByUserId(int userId) {
		ProfessorDTO professor = new ProfessorDTO();
		
		String sql = "SELECT * FROM professor WHERE user_id = ?";
		
		try(Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				/*
				 * 
				 * 불러올 테이블 정보
				 * 
				 */
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return professor;
		
	}
}
