package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.LectureDAO;
import model.dao.ProfessorDAO;
import model.dto.LectureDTO;
import model.dto.ProfessorDTO;

public class ProfessorService {

    private static final ProfessorService instance = new ProfessorService();

    private ProfessorDAO professorDAO = ProfessorDAO.getInstance();
    private LectureDAO lectureDAO = LectureDAO.getInstance();

    private ProfessorService() {}

    public static ProfessorService getInstance() {
        return instance;
    }

    // 교수 본인 정보 조회
    public ProfessorDTO getProfessorInfo(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            return professorDAO.selectProfessorInfo(conn, userId);
        } catch (Exception e) {
            throw new RuntimeException("교수 정보 조회 실패", e);
        }
    }

    // 교수 담당 강의 목록 조회
    public List<LectureDTO> getMyLectures(int professorId) {
        try (Connection conn = DBConnection.getConnection()) {
            return lectureDAO.selectLecturesByProfessor(conn, professorId);
        } catch (Exception e) {
            throw new RuntimeException("교수 강의 목록 조회 실패", e);
        }
    }
}