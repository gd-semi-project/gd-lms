package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import database.DBConnection;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.dto.ResetToken;
import utils.HashUtil;

@NoArgsConstructor
public class TokenDAO {
	public static final TokenDAO instance = new TokenDAO();
	
	public static TokenDAO getInstance () {
		return instance;
	}
	

    /**
     * 토큰 생성 후 DB에 저장
     * @param userId 토큰을 발급할 사용자 ID
     * @param ip 발급 요청 IP (선택적)
     * @return 생성된 평문 토큰
     */
    public String createToken(Long userId, String token_type, String ip) {
        // 1. 랜덤 평문 토큰 생성 (UUID 사용)
        String plainToken = UUID.randomUUID().toString().replace("-", ""); // 32자리

        // 2. 해시값 생성
        String hashedToken = HashUtil.sha256(plainToken);

        // 3. DB INSERT
        String sql = "INSERT INTO auth_token (user_id, token_value, token_type, created_at, expires_at,issued_ip) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setString(2, hashedToken); // DB에는 해시값 저장
            pstmt.setString(3, token_type);
            pstmt.setObject(4, LocalDateTime.now()); // created_at
            pstmt.setTimestamp(5, Timestamp.valueOf(expiresAt));
            pstmt.setString(6, ip);

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
    
    public ResetToken findValidToken(Long userId, String token) {
        String sql = """
            SELECT token_value, expires_at, verified_at
              FROM auth_token
             WHERE user_id = ?
               AND token_value = ?
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, token);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ResetToken rt = new ResetToken();
                rt.setToken(rs.getString("token_value"));
                rt.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                // TODO: used_yn 컬럼없어서 만들어서 넣어줘야함.
                if (rs.getTimestamp("verified_at") != null) {
                	LocalDateTime verifiedAt = rs.getTimestamp("verified_at").toLocalDateTime();
                	rt.setVerifiedAt(verifiedAt);
                }
                return rt;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("findValidToken error", e);
        }
    }

    /**
     * 토큰 사용 처리 (재사용 방지)
     */
    public int markTokenAsUsed(String token) {
    	String sql = """
    	        UPDATE auth_token
    	        SET verified_at = NOW()
    	        WHERE token_value = ?
    	    """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, token);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("markTokenAsUsed error", e);
        }
    }
    
}
