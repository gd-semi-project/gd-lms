package service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import database.DBConnection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import model.dao.DepartmentDAO;
import model.dao.EnrollmentDAO;
import model.dao.FileDAO;
import model.dao.InstructorDAO;
import model.dao.LectureDAO;
import model.dao.LectureRequestDAO;
import model.dao.StudentDAO;
import model.dao.StudentInfoUpdateRequestDAO;
import model.dao.UserDAO;
import model.dto.DepartmentDTO;
import model.dto.FileDTO;
import model.dto.InstructorDTO;
import model.dto.LectureCountByValidationDTO;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.LectureScheduleDTO;
import model.dto.MypageDTO;
import model.dto.StudentDTO;
import model.dto.StudentInfoUpdateRequestDTO;
import model.dto.UserDTO;
import model.enumtype.Gender;
import model.enumtype.StudentStatus;
import utils.AppTime;

public class AdminService {
	private LectureDAO lectureDAO= LectureDAO.getInstance();
	private LectureRequestDAO lectureRequestDAO = LectureRequestDAO.getInstance();
	private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
	private DepartmentDAO departmentDAO = DepartmentDAO.getInstance();
	private InstructorDAO instructorDAO = InstructorDAO.getInstance();
	private FileDAO fileDAO = FileDAO.getInstance();
	private static final String UPLOAD_DIR = "D:/upload";
	private static final AdminService instance = new AdminService();
	private AdminService() {}
	
	
	public static AdminService getInstance() {
		return instance;
	}

	public int getLectureCount() {
		return lectureDAO.getLectureCount();
	}
	
	public int getTotalLectureCount() {
		return lectureDAO.getTotalLectureCount();
	}
	
	public int getLectureFillRate() {
		return lectureDAO.getLectureFillRate();
	}
	
	public int getLowFillRateLecture() {
		return lectureDAO.getLowFillRateLecture();
	}
	
	public int getTotalLectureCapacity() {
		return lectureDAO.getTotalLectureCapacity();
	}
	
	public int getTotalEnrollment() {
		return lectureDAO.getTotalEnrollment();
	}
	
	
	public ArrayList<LectureRequestDTO> getPendingLectureList(Long departmentId){
		
		String validation = "PENDING";
		
		ArrayList<LectureRequestDTO> list = enrollmentDAO.getLectureList(validation, departmentId);
		
		return list;
	}
	
	public ArrayList<LectureRequestDTO> getCanceledLectureList(Long departmentId){
		
		String validation = "CANCELED";
		
		ArrayList<LectureRequestDTO> list = enrollmentDAO.getLectureList(validation, departmentId);
		
		return list;
	}
	
	public ArrayList<LectureRequestDTO> getConfirmedLectureList(Long departmentId){
		
		String validation = "CONFIRMED";
		
		ArrayList<LectureRequestDTO> list = enrollmentDAO.getLectureList(validation, departmentId);
		
		return list;
	}
	
	
	public void LectureValidate(Long lectureId, String validation) {
		
		lectureDAO.setLectureValidation(validation, lectureId);
	}


	public ArrayList<DepartmentDTO> getDepartmentList() {
		return departmentDAO.getDepartmentList();
	}
	
	public DepartmentDTO getDepartmentById(Long departmentId) {
		return departmentDAO.getDepartmentById(departmentId);
	}
	
	public ArrayList<InstructorDTO> getAllInstructorByDepartment(Long departmentId, String status){
		return InstructorDAO.getAllInstructorByDepartment(departmentId, status);
	};
	
	public ArrayList<StudentDTO> getAllStudentByDepartment(Long departmentId, String status){
		return StudentDAO.getAllStudentByDepartment(departmentId, status);
	}
	
	public LectureCountByValidationDTO getLectureCountByValidation() {
		return lectureRequestDAO.getLectureCountByValidation();
	}


	public Long studentInfoUpdateRequest(
											Long studentId, 
											HttpServletRequest request, 
											Map<String, Part> parts
											
											) {
		
		StudentInfoUpdateRequestDTO dto = bindStudentInfoUpdateReequestDTO(studentId, request);
		
		List<String> errors = validate(dto, parts);
		
		if(!errors.isEmpty()) {
			throw new IllegalArgumentException(String.join("\n", errors));
		}
		
		Long requestId = StudentInfoUpdateRequestDAO.getInstance().insert(dto);
		
		saveIfPresent(parts.get("docName"), "CHANGE_NAME", requestId);
		saveIfPresent(parts.get("docGender"), "CHANGE_GENDER", requestId);
		saveIfPresent(parts.get("docAccountNo"), "CHANGE_ACCOUNT", requestId);
		saveIfPresent(parts.get("docDepartment"), "CHANGE_DEPARTMENT", requestId);
		saveIfPresent(parts.get("docAcademicStatus"), "CHANGE_ACASTATUS", requestId);
		
		
		return requestId;
	}


	private void saveIfPresent(Part part, String boardType, Long requestId) {
		if (!hasFile(part)) return;
		
		
		try {
			UUID uuid = UUID.randomUUID();
			String original = safeFileName(part.getSubmittedFileName());
			String extIco = extractExtIco(original);
			
			Path dir = Paths.get(UPLOAD_DIR, boardType);
	        Files.createDirectories(dir);
	        
	        String storedName = uuid + "_" + original; // or uuid + "." + ext
	        Path target = dir.resolve(storedName);
	        
	        try (var in = part.getInputStream()) {
	            Files.copy(in, target);
	        }
	        
	        FileDTO fileDTO = new FileDTO();
	        fileDTO.setBoardType(boardType);
	        fileDTO.setRefId(requestId);
	        fileDTO.setUuid(uuid);
	        fileDTO.setOriginalFilename(original);
	        fileDTO.setUploadedAt(AppTime.now());
	        fileDTO.setExtenderIco(extIco);
	        
	        
	        FileDAO.getInstance().insertFileUpload(fileDTO);
		} catch (Exception e) {
			throw new RuntimeException("파일 저장 실패: " + boardType, e);
		}
		
	}


	private String extractExtIco(String filename) {
	    if (filename == null) return "file";

	    int dot = filename.lastIndexOf('.');
	    if (dot < 0 || dot == filename.length() - 1) {
	        return "file";
	    }

	    return filename.substring(dot + 1).toLowerCase();
	}


	private String safeFileName(String filename) {
	    if (filename == null) return "file";

	    // 경로 제거 (보안)
	    filename = filename.replace("\\", "/");
	    int idx = filename.lastIndexOf('/');
	    if (idx >= 0) {
	        filename = filename.substring(idx + 1);
	    }

	    // 아주 기본적인 금칙 문자 제거 (선택)
	    filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");

	    return filename;
	}



	private List<String> validate(StudentInfoUpdateRequestDTO dto, Map<String, Part> parts) {
		List<String> errors = new ArrayList<String>();
		
		if(!"Y".equalsIgnoreCase(dto.getAgree())) {
			errors.add("동의 체크가 필요합니다.");
		}
		boolean hasAnyChange =
				dto.getNewName() != null ||
				dto.getNewGender() != null ||
				dto.getNewAccountNo() != null ||
				dto.getNewDepartmentId() != null ||
				dto.getNewAcademicStatus() != null;
		
		if (!hasAnyChange) {
			errors.add("변경할 항목이 없습니다.");
		}
		
		if(dto.getNewName() != null && !hasFile(parts.get("docName"))) {
			errors.add("이름 변경 시 증빙 서유 파일이 필요합니다.");
		}
		if(dto.getNewGender() != null && !hasFile(parts.get("docGender"))) {
			errors.add("이름 변경 시 증빙 서유 파일이 필요합니다.");
		}
		if(dto.getNewAccountNo() != null && !hasFile(parts.get("docAccountNo"))) {
			errors.add("이름 변경 시 증빙 서유 파일이 필요합니다.");
		}
		if(dto.getNewDepartmentId() != null && !hasFile(parts.get("docDepartment"))) {
			errors.add("이름 변경 시 증빙 서유 파일이 필요합니다.");
		}
		if(dto.getNewAcademicStatus() != null && !hasFile(parts.get("docAcademicStatus"))) {
			errors.add("이름 변경 시 증빙 서유 파일이 필요합니다.");
		}
		
		return errors;
	}


	private boolean hasFile(Part part) {
		return part != null && part.getSize() > 0;
	}


	private StudentInfoUpdateRequestDTO bindStudentInfoUpdateReequestDTO(Long studentId, HttpServletRequest request) {
		 StudentInfoUpdateRequestDTO dto = new StudentInfoUpdateRequestDTO();
	        dto.setStudentId(studentId);

	        dto.setNewName(trimToNull(request.getParameter("newName")));
	        dto.setNewAccountNo(trimToNull(request.getParameter("newAccountNo")));
	        dto.setNewDepartmentId(parseLongOrNull(trimToNull(request.getParameter("newDepartmentId"))));
	        dto.setReason(trimToNull(request.getParameter("reason")));
	        dto.setAgree(trimToNull(request.getParameter("agree"))); // "Y"

	        dto.setNewGender(parseEnumOrNull(Gender.class, trimToNull(request.getParameter("newGender"))));

	        dto.setNewAcademicStatus(parseEnumOrNull(StudentStatus.class, trimToNull(request.getParameter("newAcademicStatus"))));

	        return dto;
	}


	private static <E extends Enum<E>> E parseEnumOrNull(Class<E> enumType, String s) {
        if (s == null) return null;
        try { return Enum.valueOf(enumType, s); }
        catch (Exception e) { return null; }
	}


	private Long parseLongOrNull(String s) {
        if (s == null) return null;
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) { return null; }
    }

	private String trimToNull(String s) {
		if (s == null) return null;
		String trim = s.trim();
		return trim.isEmpty() ? null : trim;
	}

	public int getPendingStudentInfoUpdateCount() {
	    return StudentInfoUpdateRequestDAO.getInstance().countPending();
	}


	public List<StudentInfoUpdateRequestDTO> getStudentInfoUpdateRequests() {
		return StudentInfoUpdateRequestDAO.getInstance().selectPendingList();
	}
	
	
	public Map<String, Object> getStudentInfoUpdateRequestDetail(Long requestId) {
	    Map<String, Object> out = new HashMap<>();

	    // 1) 요청 본문
	    StudentInfoUpdateRequestDTO req =
	        StudentInfoUpdateRequestDAO.getInstance().selectById(requestId);
	    if (req == null) return out;

	    // 2) 현재 학생/유저/학과
	    StudentDTO student = StudentDAO.getInstance().findStudentByStudentId(req.getStudentId());
	    UserDTO user = (student == null) ? null : UserDAO.getInstance().selectUserByUserId(student.getUserId());
	    DepartmentDTO dept = (student == null) ? null : DepartmentDAO.getInstance().findById(student.getDepartmentId());

	    // 3) 첨부파일들 (요청 refId=requestId)
	    Map<String, List<FileDTO>> filesByType = new HashMap<>();
	    FileDAO fileDAO = FileDAO.getInstance();

	    filesByType.put("CHANGE_NAME", fileDAO.selectFileListById("CHANGE_NAME", requestId));
	    filesByType.put("CHANGE_GENDER", fileDAO.selectFileListById("CHANGE_GENDER", requestId));
	    filesByType.put("CHANGE_ACCOUNT", fileDAO.selectFileListById("CHANGE_ACCOUNT", requestId));
	    filesByType.put("CHANGE_DEPARTMENT", fileDAO.selectFileListById("CHANGE_DEPARTMENT", requestId));
	    filesByType.put("CHANGE_ACASTATUS", fileDAO.selectFileListById("CHANGE_ACASTATUS", requestId));
	    
	    out.put("req", req);
	    out.put("currentStudent", student);
	    out.put("currentUser", user);
	    out.put("currentDept", dept);
	    out.put("filesByType", filesByType);
	    return out;
	}


	public void applyStudentInfoUpdate(Long requestId, Long studentId, HttpServletRequest request) {
	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        StudentInfoUpdateRequestDTO req =
	            StudentInfoUpdateRequestDAO.getInstance().selectById(requestId);
	        if (req == null) throw new IllegalStateException("요청이 없습니다.");

	        StudentDTO student = StudentDAO.getInstance().findStudentByStudentId(studentId);
	        if (student == null) throw new IllegalStateException("학생이 없습니다.");

	        UserDTO user = UserDAO.getInstance().selectUserByUserId(student.getUserId());
	        if (user == null) throw new IllegalStateException("유저가 없습니다.");

	        // 요청된 항목만 반영
	        if (req.getNewName() != null) {
	            user.setName(request.getParameter("name"));
	        }
	        if (req.getNewGender() != null) {
	            user.setGender(Gender.valueOf(request.getParameter("gender")));
	        }
	        if (req.getNewAccountNo() != null) {
	            student.setTuitionAccount(request.getParameter("accountNo"));
	        }
	        if (req.getNewDepartmentId() != null) {
	            student.setDepartmentId(Long.parseLong(request.getParameter("departmentId")));
	        }
	        if (req.getNewAcademicStatus() != null) {
	            student.setStudentStatus(StudentStatus.valueOf(request.getParameter("academicStatus")));
	        }

	        // DB 업데이트
	        UserDAO.getInstance().updateUserByAdmin(conn, user);
	        StudentDAO.getInstance().updateStudentByAdmin(conn, student);
	        StudentInfoUpdateRequestDAO.getInstance().markCompleted(conn, requestId);
	        conn.commit();
	    } catch (Exception e) {
	        throw new RuntimeException("학생 정보 반영 실패", e);
	    }
	}


}