package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
	
	public void fileUpload(String BoardType, Long assignmentId, Collection<Part> partCollection) {
		FileDAO fileDAO = FileDAO.getInstance();
		
		// 파트컬렉션에서 파일리스트 반환
		List<Part> fileList = extractFileParts(partCollection);
		
		// 업로드 테이블에 데이터만 추가하는것
		for (Part filePart : fileList) {
			FileDTO file = new FileDTO();
			
			file.setBoardType(BoardType);
			file.setRefId(assignmentId);
			String filename = filePart.getName();
			file.setOriginalFilename(filename);
			UUID uuid = UUID.randomUUID();
			file.setUuid(uuid);
			
			fileDAO.isnertFileUpload(file);
			
			// 실제 파일을 업로드하는 로직 필요
			try {
				filePart.write(getFilePath(uuid));
			} catch (IOException e) {
				// TODO : 예외처리
				e.printStackTrace();
			}
			
		}
	}
	
	// fileDTO 리스트를 컨트롤러가 받고, 서비스에서 다운로드 로직을 별도로 수행
	// 리스트를 받아서 화면에 아이콘 + 주소? 방식으로 리스트 나열하고
	// 리스트의 각 객체를 클릭했을 때 fileDownload() 실행
	
	
	// 컨트롤러에서 36글자의 uuid를 받음 주어졌을 때 파일 다운로드하는 서비스 로직
	// 권한체크 로직이 필요, 강의게시판에서 접속하는 경우에는 본인의 과제만 다운로드
	// 교수는 본인의 강의에 해당하는 학생들의 과제 다운로드 가능
	// 다운로드 시도 로그를 남기는것도 방법
	public byte[] fileDownload(String uuid) throws FileNotFoundException, IOException {
		
		String filePath = getFilePath(UUID.fromString(uuid));
		
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
	}
	
	
	
	// 파일업로드 유틸
	
	// uuid값과 파일 경로를 조합해서 파일경로를 반환하는 메소드
	// 파일경로가 게시판마다 다를 수 있으니, 미리 분리
	public String getFilePath(UUID uuid) {
		String FileServerPath = "D:/upload/";
		return FileServerPath + uuid.toString();
	}
	
	// Part리스트를 반환
	public List<Part> extractFileParts (Collection<Part> partCollection) {
		List<Part> partList = new ArrayList<Part>();
		for (Part part : partCollection) {
			if (part.getSubmittedFileName() != null) {
				partList.add(part);
			}
		}
		return partList;
	}
}
