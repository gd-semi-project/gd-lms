package service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import database.DBConnection;
import model.dao.InstructorDAO;
import model.dao.UserDAO;

public class InstructorService {

    private static final InstructorService instance = new InstructorService();

    private InstructorService() {}

    public static InstructorService getInstance() {
        return instance;
    }

    private InstructorDAO instructorDAO = InstructorDAO.getInstance();
    private UserDAO userDAO = UserDAO.getInstance();

    // 강사 프로필
    public Map<String, Object> getInstructorProfile(long userId) {
        try (Connection conn = DBConnection.getConnection()) {
            Map<String, Object> map = new HashMap<>();
            map.put("instructor",
                    instructorDAO.selectInstructorInfo(userId));
//            map.put("user",
//                    userDAO.selectUserByUserId(conn, userId));
            return map;
        } catch (Exception e) {
            throw new RuntimeException("강사 프로필 조회 실패", e);
        }
    }
}