package service;

import model.dao.DepartmentDAO;
import model.dao.InstructorDAO;
import model.dao.StudentDAO;
import model.dao.UserDAO;
import model.dto.DepartmentDTO;
import model.dto.InstructorDTO;
import model.dto.MypageDTO;
import model.dto.StudentDTO;
import model.dto.UserDTO;
import model.enumtype.Role;
import utils.HashUtil;

//	 로그인한 사용자의 role에 따라 필요한 데이터만 조회해서 MypageDTO조립
public class MyPageService {
	// DAO 불러오기
	private UserDAO userDAO = UserDAO.getInstance();
	private StudentDAO studentDAO = StudentDAO.getInstance();
	private InstructorDAO instructorDAO = InstructorDAO.getInstance();
	private DepartmentDAO departmentDAO = DepartmentDAO.getInstance();

	public MypageDTO getMypageDTO(String Id) {

		// users 테이블 조회
		UserDTO user = userDAO.selectUsersById(Id);

		// 결과 DTO 생성
		MypageDTO mypage = new MypageDTO();
		mypage.setUser(user);

		// 역할(학생, 교수, 관리자) 조건문
		// 학생이면
		if (user.getRole() == Role.STUDENT) {
			buildStudentPage(mypage, user);
		} // 교수면
		else if (user.getRole() == Role.INSTRUCTOR) {
			buildProfessorPage(mypage, user);
		} // 관리자면
		else if (user.getRole() == Role.ADMIN) {
			// 관리자는 users정보만 사용
		}

		return mypage;

	}

	// 학생일때 볼 수 있는 페이지
	private void buildStudentPage(MypageDTO mypage, UserDTO user) {
		StudentDTO student =
                studentDAO.findStudentByUserId(user.getUserId());

		if(student == null) {
			return;
		}
		
		DepartmentDTO department = departmentDAO.findById(student.getDepartmentId());

		mypage.setStudent(student);
		mypage.setDepartment(department);
	}
	
	// 학생 정보 수정
	public void updateStudentInfo(String loginId, UserDTO userDTO, StudentDTO studentsDTO ) {
		// user 테이블 수정
		userDAO.updateUserInfo(userDTO);
		// student 테이블 수정
		studentDAO.updateStudentInfo(studentsDTO, loginId);
		
	}
	
	// ID일치 확인(비밀번호변경 페이지)
//	public boolean checkStudentId(String loginId) {
//		return studentDAO.checkUserIdBychangeAccount(loginId);
//	}
//	
	// 비밀번호 일치유무 확인
	public boolean checkCurrentPassword(String loginId, String rawPassword) {
		UserDTO user = userDAO.selectUsersById(loginId);
	    return rawPassword.equals(user.getPassword());
	}
	
	// 비밀번호 변경
	public void changePassword(String loginId, String newPassword) {
		userDAO.updatePassword(loginId, newPassword);
	}

	// 교수가 볼 수 있는 페이지
	private void buildProfessorPage(MypageDTO mypage, UserDTO user) {
		InstructorDTO instructor = instructorDAO.selectInstructorInfo(user.getUserId());
		
		DepartmentDTO department = departmentDAO.findById(instructor.getDepartmentId());
		
		mypage.setProfessor(instructor);
		mypage.setDepartment(department);
	}
}
