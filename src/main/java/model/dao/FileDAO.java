package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.DBConnection;
import lombok.NoArgsConstructor;
import model.dto.AccessDTO;
import model.dto.FileDTO;
import model.dto.UserDTO;
import model.enumtype.Gender;
import model.enumtype.Role;
import model.enumtype.Status;

@NoArgsConstructor
public class FileDAO {
	private static final FileDAO instance = new FileDAO();
	
	public static FileDAO getInstance() {
		return instance;
	}
	
	public List<FileDTO> selectFileListById(String boardType, long refId) {
		String sql = "SELECT * FROM fileupload WHERE board_type = ? AND ref_id = ?";
		FileDTO fileDTO = new FileDTO();
		List<FileDTO> fileList = new ArrayList<FileDTO>();
		
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardType);
			pstmt.setLong(2, refId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				fileDTO.setFileId(rs.getLong("file_id"));
				fileDTO.setOriginalFilename(rs.getString("original_filename"));
				String uuidStr = rs.getString("uuid");
				fileDTO.setUuid(UUID.fromString(uuidStr));
				fileList.add(fileDTO);
			}
			return fileList;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println("FileDAO selectFileListById" + e.getMessage());
		}
		return null;
	}
	
	public void isnertFileUpload (FileDTO fileDTO) {
		String sql = "INSERT INTO file_upload (board_type, ref_id, uuid, orginal_filename)"
				+ " values (?, ?, ?, ?)";
		
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fileDTO.getBoardType());
			pstmt.setLong(2, fileDTO.getRefId());
			pstmt.setString(3, fileDTO.getUuid().toString());
			pstmt.setString(4, fileDTO.getOriginalFilename());
			
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println("FileDAO isnertFileById" + e.getMessage());
		}
	}
}