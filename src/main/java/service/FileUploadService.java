package service;

import java.io.File;
import java.io.FileInputStream;
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

    // ✅ OS 독립 경로 (Windows/Mac/Linux 공통)
    private String getUploadDir() {
        return System.getProperty("user.home")
                + File.separator + "gd-lms"
                + File.separator + "upload";
    }

    // ================= 파일 업로드 =================
    public String fileUpload(String boardType, Long refId, Collection<Part> partList) {

        List<String> allowFileExtenderList = Arrays.asList(
                ".hwp", ".jpg", ".png", ".jpeg", ".docx",
                ".xlsx", ".pdf", ".pptx"
        );

        List<String> allowLower = allowFileExtenderList.stream()
                .map(String::toLowerCase)
                .toList();

        FileDAO fileDAO = FileDAO.getInstance();
        String resultMessage = null;

        for (Part part : partList) {

            UUID uuid = UUID.randomUUID();

            try {
                if (part.getSize() == 0 || part.getSubmittedFileName() == null) continue;

                String originalFilename = part.getSubmittedFileName();
                int lastOfIndexDot = originalFilename.lastIndexOf(".");
                if (lastOfIndexDot < 0) continue;

                String extender = originalFilename.substring(lastOfIndexDot).toLowerCase();

                if (!allowLower.contains(extender)) {
                    if (resultMessage != null) resultMessage += ",";
                    resultMessage += originalFilename;
                    continue;
                }

                FileDTO fileDTO = new FileDTO();
                fileDTO.setBoardType(boardType);
                fileDTO.setRefId(refId);
                fileDTO.setUuid(uuid);
                fileDTO.setOriginalFilename(originalFilename);

                // 실제 파일 저장
                saveFileToDisk(part, uuid);

                // DB 저장
                fileDAO.insertFileUpload(fileDTO);

            } catch (Exception e) {
                File file = new File(getUploadDir(), uuid.toString());
                if (file.exists()) file.delete();
                throw new InternalServerException("파일 업로드를 실패했습니다.", e);
            }
        }

        if (resultMessage == null) {
            return "파일업로드를 완료했습니다.";
        } else {
            return resultMessage + ": 해당 파일은 허용되지 않은 확장자입니다.";
        }
    }

    // ================= 실제 파일 저장 =================
    private void saveFileToDisk(Part part, UUID uuid) {

        String uploadDir = getUploadDir();
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

    // ================= 파일 삭제(DB) =================
    public int deleteFile(String boardType, Long refId) {

        FileDAO fileDAO = FileDAO.getInstance();
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            return fileDAO.deleteFileByBoardTypeAndRefId(conn, boardType, refId);

        } catch (Exception e) {
            throw new InternalServerException("파일 삭제 중 에러가 발생했습니다.", e);

        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    // fileDTO 리스트를 컨트롤러가 받고, 서비스에서 다운로드 로직을 별도로 수행
 	// 리스트를 받아서 화면에 아이콘 + 주소? 방식으로 리스트 나열하고
 	// 리스트의 각 객체를 클릭했을 때 fileDownload() 실행

    public List<FileDTO> getFileList(String boardType, Long refId) {

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
                }

                file.setExtenderIco(icon);
            }

            return fileList;

        } catch (Exception e) {
            throw new InternalServerException("첨부파일 로딩 오류 발생", e);
        }
    }

    // ================= 파일 다운로드 =================
    public byte[] fileDownload(String uuid) {

        File file = new File(getUploadDir(), uuid);

        if (!file.exists()) {
            throw new InternalServerException("다운로드할 파일이 존재하지 않습니다.");
        }

        try (FileInputStream fis = new FileInputStream(file)) {

            byte[] fileData = new byte[(int) file.length()];
            fis.read(fileData);
            return fileData;

        } catch (IOException e) {
            throw new InternalServerException("파일 읽기 중 오류가 발생했습니다.", e);
        }
    }

    // ================= 원본 파일명 조회 =================
    public String getFileOriginalName(UUID uuid) {

        try {
            FileDAO fileDAO = FileDAO.getInstance();
            return fileDAO.selectFileNameByUUID(uuid);

        } catch (Exception e) {
            throw new InternalServerException("파일을 찾을 수 없습니다.", e);
        }
    }
}