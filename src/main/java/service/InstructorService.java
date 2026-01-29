package service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import database.DBConnection;
import exception.InternalServerException;
import exception.ResourceNotFoundException;
import model.dao.InstructorDAO;
import model.dao.UserDAO;
import model.dto.InstructorDTO;
import model.dto.UserDTO;

public class InstructorService {

    private static final InstructorService instance = new InstructorService();

    private InstructorService() {}

    public static InstructorService getInstance() {
        return instance;
    }

    private final InstructorDAO instructorDAO = InstructorDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    public Map<String, Object> getInstructorProfile(long userId) {

        try (Connection conn = DBConnection.getConnection()) {

            UserDTO user = userDAO.selectUserByUserId(userId);

            if (user == null) {
                throw new ResourceNotFoundException("존재하지 않는 사용자입니다.");
            }

            InstructorDTO instructor =
                instructorDAO.selectInstructorInfo(userId);

            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("instructor", instructor);

            return map;

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            throw new InternalServerException("강사 프로필 조회 중 서버 오류", e);
        }
    }

    public void updateInstructorProfile(
    	    Long userId,
    	    String name,
    	    String email,
    	    String phone,
    	    String officeRoom,
    	    String officePhone
    	) {
    	    instructorDAO.updateInstructorUserInfo(userId, name, email, phone);
    	    instructorDAO.updateInstructorOfficeInfo(userId, officeRoom, officePhone);
    	}
}