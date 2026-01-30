package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import database.DBConnection;
import exception.InternalServerException;
import jakarta.servlet.http.Part;
import lombok.NoArgsConstructor;
import model.dao.FileDAO;
import model.dto.FileDTO;

@NoArgsConstructor
public class FileUploadService {
	private static final FileUploadService instance = new FileUploadService();
		
	public static FileUploadService getInstance() {
		return instance;
	}
	
	private static final Map<String, String> EXT_ICON_MAP = Map.of(
	        ".hwp", "📄",
	        ".jpg", "🖼️",
	        ".jpeg", "🖼️",
	        ".png", "🖼️",
	        ".docx", "📃",
	        ".xlsx", "📊",
	        ".pptx", "📈",
	        ".pdf", "📕"
	);
	
	
	// 파일업로드
	public String fileUpload(String boardType, Long refId, Collection<Part> partList) {
        List<String> allowFileExtenderList = Arrays.asList(
    			".hwp", ".jpg", ".png", ".jpeg", ".docx",
    			".xlsx", ".pdf", ".pptx"
    		);
        List<String> allowLower = allowFileExtenderList.stream()
                .map(s -> s.toLowerCase())
                .toList();
        
	    FileDAO fileDAO = FileDAO.getInstance();
	    String resultMessage = null;
	    for (Part part : partList) {
	        // 1. 파일 메타데이터 생성
	        UUID uuid = UUID.randomUUID();
	    	try {
		    	if (part.getSize() == 0 || part.getSubmittedFileName() == null) {
		            continue;
		        }
		        String originalFilename = part.getSubmittedFileName();
		        int lastOfIndexDot = originalFilename.lastIndexOf(".");
		        String extender = originalFilename.substring(lastOfIndexDot);
		        String extLower = extender.toLowerCase();
		        if (!allowLower.contains(extLower)) {
		        	if (resultMessage != null) {
		        		resultMessage += ",";
		        	}
		        	resultMessage += originalFilename;
		            continue;
		        }
	        
		        FileDTO fileDTO = new FileDTO();
		        fileDTO.setBoardType(boardType);
		        fileDTO.setRefId(refId);
		        fileDTO.setUuid(uuid);
		        fileDTO.setOriginalFilename(originalFilename);

		        // 2. 실제 파일 write
		        saveFileToDisk(part, uuid);
	
		        // 3. DB insert
		        fileDAO.insertFileUpload(fileDTO);
		    } catch (InternalServerException e) {
		    	File file = new File("D:/upload" + "/" + uuid.toString());
		    	if (file.exists()) file.delete();
		    	throw new InternalServerException("파일 업로드를 실패했습니다.", e);
		    }
	    }
	    // 결과메시지 반환 로직
	    if (resultMessage == null) {
	    	resultMessage = "파일업로드를 완료했습니다.";
	    	return resultMessage;
	    } else {
	    	resultMessage += ": 해당 파일은 허용되지 않은 확장자입니다.";
	    	return resultMessage;
	    }
	}
	
	private void saveFileToDisk(Part part, UUID uuid) {
	    String uploadDir = "D:/upload";
	    File dir = new File(uploadDir);

	    if (!dir.exists()) {
	        dir.mkdirs();
	    }

	    File target = new File(dir, uuid.toString());

	    try {
	        part.write(target.getAbsolutePath());
	    } catch (IOException e) {
	        throw new InternalServerException("파일 업로드를 실패했습니다.", e);
	    }
	}

	
	public int deleteFile (String boardType, Long refId) {
		FileDAO fileDAO = FileDAO.getInstance();
		Connection conn = null;
		int deleteFIleCount = 0;
		try {
			conn = DBConnection.getConnection();
			
			deleteFIleCount = fileDAO.deleteFileByBoardTypeAndRefId(conn, boardType, refId);
			
			return deleteFIleCount;
		} catch (ClassNotFoundException | SQLException | InternalServerException e) {
			throw new InternalServerException("파일 삭제 중 에러가 발생했습니다.", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	// fileDTO 리스트를 컨트롤러가 받고, 서비스에서 다운로드 로직을 별도로 수행
	// 리스트를 받아서 화면에 아이콘 + 주소? 방식으로 리스트 나열하고
	// 리스트의 각 객체를 클릭했을 때 fileDownload() 실행
	
	public List<FileDTO> getFileList (String boardType, Long refId) {
		try {
			FileDAO fileDAO = FileDAO.getInstance();
			List<FileDTO> fileList = fileDAO.selectFileListById(boardType, refId);
			
			for (FileDTO file : fileList) {
				String originalFilename = file.getOriginalFilename(); 
		        int lastOfIndexDot = originalFilename.lastIndexOf(".");
		        String icon = "📄";
		        if (lastOfIndexDot > 0) {
			        String extender = originalFilename.substring(lastOfIndexDot);
			        icon = EXT_ICON_MAP.getOrDefault(extender, "📄");
			        file.setExtenderIco(icon);
		        }
			}
			return fileList;
		} catch (InternalServerException e) {
			throw new InternalServerException("첨부파일 로딩 오류 발생", e);
		}
	}
	
	public byte[] fileDownload(String downloadDir, String uuid) throws FileNotFoundException, IOException {		
        File file = new File(downloadDir, uuid);
        byte[] fileData = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        } catch (FileNotFoundException e) {
            throw new InternalServerException("다운로드할 파일이 존재하지 않습니다.", e);
        } catch (IOException e) {
            throw new InternalServerException("파일 읽기 중 오류가 발생했습니다.", e);
        }
        
        return fileData;
	}
	
	public String getFileOriginalName(UUID uuid) {
		try {
			FileDAO fileDAO = FileDAO.getInstance();
			return fileDAO.selectFileNameByUUID(uuid);
		} catch (InternalServerException e) {
			throw new InternalServerException("파일을 찾을 수 없습니다.",e);
		}
	}
}
