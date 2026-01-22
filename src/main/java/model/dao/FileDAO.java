package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.DBConnection;
import lombok.NoArgsConstructor;
import model.dto.FileDTO;

@NoArgsConstructor
public class FileDAO {
	private static final FileDAO instance = new FileDAO();
	
	public static FileDAO getInstance() {
		return instance;
	}
	
	public List<FileDTO> selectFileListById(String boardType, Long refId) {
		String sql = "SELECT * FROM file_upload WHERE board_type = ? AND ref_id = ?";
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
			System.out.println("FileDAO selectFileListById: " + e.getMessage());
		}
		return null;
	}
	
	public void isnertFileUpload (FileDTO fileDTO) {
		String sql = "INSERT INTO file_upload (board_type, ref_id, uuid, original_filename)"
				+ " values (?, ?, ?, ?)";
		
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fileDTO.getBoardType());
			pstmt.setLong(2, fileDTO.getRefId());
			pstmt.setString(3, fileDTO.getUuid().toString());
			pstmt.setString(4, fileDTO.getOriginalFilename());
			pstmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println("FileDAO isnertFileById: " + e.getMessage());
		}
	}

	public int deleteFileByBoardTypeAndRefId(Connection conn, String boardType, Long refId)
	        throws SQLException {

		String sql = """
			    UPDATE file_upload
			    SET is_deleted = 'Y'
			    WHERE board_type = ?
			      AND ref_id = ?
			    """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, boardType);
	        ps.setLong(2, refId);
	        return ps.executeUpdate(); // 삭제된 파일 수
	    }
	}
	
	public String selectFileNameByUUID(UUID uuid) {
		String sql = "SELECT original_filename FROM file_upload WHERE uuid = ?";
		String originalFileName = null;
		try (Connection conn = DBConnection.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uuid.toString());
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				originalFileName = rs.getString("original_filename");
			}
			return originalFileName;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO: 예외처리 구문 작성 필요
			System.out.println("FileDAO selectFileNameByUUID: " + e.getMessage());
		}
		return null;
	}


}