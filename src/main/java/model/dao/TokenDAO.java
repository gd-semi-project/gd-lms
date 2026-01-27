package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import database.DBConnection;
import exception.InternalServerException;
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
            throw new InternalServerException(e);
        }

        // 4. 평문 토큰 반환 (사용자에게 전달용)
        return plainToken;
    }
    
    public Long getUserIdByToken(String token) {
        String sql = "SELECT user_id, expires_at, verified_at FROM auth_token WHERE token_value = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	Timestamp verifiedAt = rs.getTimestamp("verified_at");
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    LocalDateTime now = LocalDateTime.now();

                    if (verifiedAt != null) {
                        System.out.println("토큰 이미 사용됨");
                        return null;
                    }

                    if (expiresAt.toLocalDateTime().isBefore(now)) {
                        System.out.println("토큰 만료됨");
                        return null;
                    }

                    return rs.getLong("user_id");
                } else {
                    System.out.println("토큰 없음");
                    return null;
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
        	throw new InternalServerException(e);
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
        }  catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
    }
    
}
