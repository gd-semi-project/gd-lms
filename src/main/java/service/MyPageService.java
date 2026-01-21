package service;

import model.dao.DepartmentDAO;
import model.dao.InstructorDAO;
import model.dao.StudentDAO;
import model.dao.UserDAO;
import model.dto.DepartmentDTO;
import model.dto.InstructorDTO;
import model.dto.MypageDTO;
import model.dto.StudentsDTO;
import model.dto.UserDTO;
import model.enumtype.Role;

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
		StudentsDTO student =
                studentDAO.findStudentByLoginId(user.getLoginId());

		if(student == null) {
			return;
		}
		
		DepartmentDTO department = departmentDAO.findById(student.getDepartmentId());

		mypage.setStudent(student);
		mypage.setDepartment(department);
	}

	// 교수가 볼 수 있는 페이지
	private void buildProfessorPage(MypageDTO mypage, UserDTO user) {
		InstructorDTO instructor = instructorDAO.selectInstructorInfo(user.getUserId());
		
		DepartmentDTO department = departmentDAO.findById(instructor.getDepartmentId());
		
		mypage.setProfessor(instructor);
		mypage.setDepartment(department);
	}
}
