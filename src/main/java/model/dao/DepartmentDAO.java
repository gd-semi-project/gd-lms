package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DBConnection;
import model.dto.DepartmentDTO;

public class DepartmentDAO {
	private static DepartmentDAO instance = new DepartmentDAO();
	
	private DepartmentDAO() {}
	
	public static DepartmentDAO getInstance() {
		return instance;
	}
	
	public DepartmentDTO findById(Long departmentId) {
		DepartmentDTO depart = new DepartmentDTO();
		
		String sql = "SELECT * FROM department WHERE department_id = ?";
		
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, departmentId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				depart.setDepartmentName(rs.getString("department_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return depart;
	}
}
