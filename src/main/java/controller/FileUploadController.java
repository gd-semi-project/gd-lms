package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.dao.FileDAO;
import service.FileUploadService;

import java.io.IOException;
import java.util.Collection;

@WebServlet("/FileUpload")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,  // 메모리에 저장할 임계값
	    maxFileSize = 1024 * 1024 * 50,   // 업로드 파일 최대 크기 (50MB)
	    maxRequestSize = 1024 * 1024 * 100 // 요청 전체 크기 (100MB)
	)

public class FileUploadController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = uri.substring(contextPath.length());
		
		if (actionPath.equals("/FileUpload")) {
			System.out.println("파일업로드 페이지는 임의로 접속할 수 없습니다.");
		} else if (actionPath.equals("/FileUpload/Down")) {
			FileUploadService fus = FileUploadService.getInstance();
			String fileUUID = request.getParameter("filename");
			// 입력된 길이값이 36일 때만 DB접근
			if (fileUUID.length() == 36) {
				// 서비스 호출해서 UUID로 파일 다운로드 시도
				fus.fileDownload(fileUUID);
			} else {
				// 잘못된 경로 요청 예외페이지?
				System.out.println("잘못된 경로값입니다.");
			}
		}
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 전송 눌렀을 때 doPost 실행
		// hiddenInput에 있는 값 불러오기
		
		// 파일 업로드 컨트롤러
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String actionPath = uri.substring(contextPath.length());
		
		if (actionPath.equals("/FileUpload")) {
			String boardType = request.getParameter("boardType");
			Long refId =  Long.parseLong(request.getParameter("refId"));
			FileUploadService fus = FileUploadService.getInstance();
			Collection<Part> partList = request.getParts();
			
			fus.fileUpload(boardType, refId, partList);
		}
		
		
		
	}

}
