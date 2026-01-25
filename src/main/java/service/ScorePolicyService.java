package service;

import java.sql.Connection;

import database.DBConnection;
import model.dao.ScorePolicyDAO;
import model.dto.ScorePolicyDTO;

public class ScorePolicyService {

    private static final ScorePolicyService instance =
            new ScorePolicyService();

    public static ScorePolicyService getInstance() {
        return instance;
    }

    private final ScorePolicyDAO scorePolicyDAO =
            ScorePolicyDAO.getInstance();

    private ScorePolicyService() {}

    // 배점 조회
    public ScorePolicyDTO getPolicy(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return scorePolicyDAO.findByLecture(conn, lectureId);
        } catch (Exception e) {
            throw new RuntimeException("배점 조회 실패", e);
        }
    }

    // 배전 저장
    public void savePolicy(ScorePolicyDTO policy) {
        try (Connection conn = DBConnection.getConnection()) {

            if (scorePolicyDAO.isConfirmed(conn, policy.getLectureId())) {
                throw new IllegalStateException("이미 배점이 확정되었습니다.");
            }

            int sum =
                policy.getAttendanceWeight()
              + policy.getAssignmentWeight()
              + policy.getMidtermWeight()
              + policy.getFinalWeight();

            if (sum != 100) {
                throw new IllegalArgumentException("배점 합계는 100이어야 합니다.");
            }

            scorePolicyDAO.upsert(conn, policy);

        } catch (Exception e) {
            throw new RuntimeException("배점 저장 실패", e);
        }
    }

    // 배점 확정
    public void confirmPolicy(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {

            if (scorePolicyDAO.isConfirmed(conn, lectureId)) {
                throw new IllegalStateException("이미 확정된 배점입니다.");
            }

            scorePolicyDAO.confirmPolicy(conn, lectureId);

        } catch (Exception e) {
            throw new RuntimeException("배점 확정 실패", e);
        }
    }

    // 배점 확정 여부
    public boolean isConfirmed(long lectureId) {
        try (Connection conn = DBConnection.getConnection()) {
            return scorePolicyDAO.isConfirmed(conn, lectureId);
        } catch (Exception e) {
            return false;
        }
    }
}