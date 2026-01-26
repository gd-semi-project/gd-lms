package service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import database.DBConnection;
import model.dao.InstructorDAO;
import model.dao.LectureDAO;
import model.dao.UserDAO;
import model.dto.InstructorDTO;
import model.dto.UserDTO;

public class InstructorService {

    private static final InstructorService instance = new InstructorService();

    private InstructorService() {} 

    public static InstructorService getInstance() {
        return instance;
    }

    private InstructorDAO instructorDAO = InstructorDAO.getInstance();
    private LectureDAO lectureDAO = LectureDAO.getInstance();
    private UserDAO userDAO = UserDAO.getInstance();

    public Map<String, Object> getInstructorProfile(long userId) {
        try (Connection conn = DBConnection.getConnection()) {

            InstructorDTO instructor =
                instructorDAO.selectInstructorInfo(userId);
            UserDTO user =
                userDAO.selectUserByUserId(userId);

            if (user == null) {
                // TODO : 404 Not Found (존재하지 않는 사용자)
                throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("instructor", instructor);
            map.put("user", user);
            return map;

        } catch (IllegalArgumentException e) {
            // TODO : 404 Not Found (비즈니스 예외)
            throw e;

        } catch (Exception e) {
            // TODO : 500 Internal Server Error (DB / 시스템 오류)
            throw new RuntimeException("강사 프로필 조회 실패", e);
        }
    }

}