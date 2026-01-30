package service;

import java.sql.Connection;

import database.DBConnection;
import exception.BadRequestException;
import exception.InternalServerException;
import model.dao.ScorePolicyDAO;
import model.dto.ScorePolicyDTO;

public class ScorePolicyService {

    private static final ScorePolicyService instance =
            new ScorePolicyService();

    private final ScorePolicyDAO dao =
            ScorePolicyDAO.getInstance();

    private ScorePolicyService() {}

    public static ScorePolicyService getInstance() {
        return instance;
    }

    // 배점 등록
    public void createPolicy(ScorePolicyDTO dto) {

        validate(dto);

        try (Connection conn =
                     DBConnection.getConnection()) {

            dao.insert(conn, dto);

        } catch (Exception e) {
            throw new InternalServerException("성적 배점 등록 실패", e);
        }
    }

    // 배점 조회
    public ScorePolicyDTO getPolicy(Long lectureId) {

        try (Connection conn =
                     DBConnection.getConnection()) {

            return dao.findByLectureId(conn, lectureId);

        } catch (Exception e) {
            throw new InternalServerException("성적 배점 조회 실패", e);
        }
    }

    // 배점 수정
    public void updatePolicy(ScorePolicyDTO dto) {

        validate(dto);

        try (Connection conn =
                     DBConnection.getConnection()) {

            dao.update(conn, dto);

        } catch (Exception e) {
            throw new InternalServerException("성적 배점 수정 실패", e);
        }
    }

    // 배점 확정
    public void confirmPolicy(Long lectureId) {

        try (Connection conn =
                     DBConnection.getConnection()) {

            dao.confirm(conn, lectureId);

        } catch (Exception e) {
            throw new InternalServerException("성적 배점 확정 실패", e);
        }
    }

    // 검증 로직
    private void validate(ScorePolicyDTO dto) {


        if (dto.getTotalWeight() != 100) {
            throw new BadRequestException("성적 배점의 합은 반드시 100이어야 합니다.");
        }

        if (dto.getAttendanceWeight() < 0 ||
            dto.getAssignmentWeight() < 0 ||
            dto.getMidtermWeight() < 0 ||
            dto.getFinalWeight() < 0) {

        	throw new BadRequestException("배점은 음수가 될 수 없습니다.");
        }
    }
}