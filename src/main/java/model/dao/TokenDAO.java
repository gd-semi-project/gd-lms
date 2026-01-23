package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import database.DBConnection;
import utils.HashUtil;

public class TokenDAO {

    /**
     * 토큰 생성 후 DB에 저장
     * @param userId 토큰을 발급할 사용자 ID
     * @param ip 발급 요청 IP (선택적)
     * @return 생성된 평문 토큰
     */
    public String createToken(String userId, String ip) {
        // 1. 랜덤 평문 토큰 생성 (UUID 사용)
        String plainToken = UUID.randomUUID().toString().replace("-", ""); // 32자리

        // 2. 해시값 생성
        String hashedToken = HashUtil.sha256(plainToken);

        // 3. DB INSERT
        String sql = "INSERT INTO token_table (user_id, token_value, created_at, issued_ip) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, hashedToken); // DB에는 해시값 저장
            pstmt.setObject(3, LocalDateTime.now()); // created_at
            pstmt.setString(4, ip);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("토큰 저장 실패");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("TokenDAO createToken: " + e.getMessage());
        }

        // 4. 평문 토큰 반환 (사용자에게 전달용)
        return plainToken;
    }
}
