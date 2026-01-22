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
	public void fileUpload(String boardType, Long refId, Collection<Part> partList) {

	    FileDAO fileDAO = FileDAO.getInstance();

	    for (Part part : partList) {

	        if (part.getSize() == 0 || part.getSubmittedFileName() == null) {
	            continue;
	        }
	        
	        List<String> allowFileExtenderList = Arrays.asList(
	    			".hwp",
	    			".jpg",
	    			".png",
	    			".jpeg",
	    			".docx",
	    			".xlsx",
	    			".pdf",
	    			".pptx"
	    		);

	        // 1. 파일 메타데이터 생성
	        UUID uuid = UUID.randomUUID();
	        String originalFilename = part.getSubmittedFileName();
	        int lastOfIndexDot = originalFilename.lastIndexOf(".");
	        String extender = originalFilename.substring(lastOfIndexDot);
	        
	        if (!allowFileExtenderList.contains(extender)) {
	        	// 포함되어있을 경우 제외하고 업로드 하는 방식
	        	// 개선방향: 예외 던져서 포함되어있을 경우 모든 파일 업로드 금지
	        	System.out.println("허용되지 않은 확장자 파일입니다.");
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
	        fileDAO.isnertFileUpload(fileDTO);
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
	        throw new RuntimeException("파일 저장 실패", e);
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
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return deleteFIleCount;
	}
	
	// fileDTO 리스트를 컨트롤러가 받고, 서비스에서 다운로드 로직을 별도로 수행
	// 리스트를 받아서 화면에 아이콘 + 주소? 방식으로 리스트 나열하고
	// 리스트의 각 객체를 클릭했을 때 fileDownload() 실행
	
	public List<FileDTO> getFileList (String boardType, Long refId) {
		FileDAO fileDAO = FileDAO.getInstance();
		List<FileDTO> fileList = fileDAO.selectFileListById(boardType, refId);
		
		for (FileDTO file : fileList) {
			String originalFilename = file.getOriginalFilename(); 
	        int lastOfIndexDot = originalFilename.lastIndexOf(".");
	        String extender = originalFilename.substring(lastOfIndexDot);
	        String icon = EXT_ICON_MAP.getOrDefault(extender, "📄");
	        file.setExtenderIco(icon);
		}
		
		return fileList; 
	}
	
	public byte[] fileDownload(String downloadDir, String uuid) throws FileNotFoundException, IOException {		
        File file = new File(downloadDir, uuid);
        byte[] fileData = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
	}
	
	public String getFileOriginalName(UUID uuid) {
		FileDAO fileDAO = FileDAO.getInstance();
		return fileDAO.selectFileNameByUUID(uuid);
	}
}
