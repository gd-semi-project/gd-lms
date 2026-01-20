package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import database.DBConnection;
import model.dto.DepartmentDTO;

public class DepartmentDAO {
	private static DepartmentDAO instance = new DepartmentDAO();
	
	private DepartmentDAO() {}
	
	public static DepartmentDAO getInstance() {
		return instance;
	}
	
	public DepartmentDTO finById(Long departmentId) {
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

	public ArrayList<DepartmentDTO> getDepartmentList() {
		String sql = "SELECT * FROM department ORDER BY department_name;";
		ArrayList<DepartmentDTO> list = new ArrayList<DepartmentDTO>();
		try (	Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				) {
			
			try (ResultSet rs = stmt.executeQuery(sql)){
				while(rs.next()) {
					DepartmentDTO dto = new DepartmentDTO();
					dto.setDepartmentId(rs.getLong("department_id"));
					dto.setCollegeId(rs.getLong("college_id"));
					dto.setDepartmentName(rs.getString("department_name"));
					dto.setAnnualQuota(rs.getInt("annual_quota"));
					dto.setDepartmentCode(rs.getString("department_code"));
					list.add(dto);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
}
