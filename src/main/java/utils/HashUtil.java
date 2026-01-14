package utils;

import java.security.MessageDigest;

public class HashUtil {
	public static String sha256(String passwd) {
		if (passwd == null) return null;
		
		try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(passwd.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            // 예외 처리
        }
		return null; // 예외 처리 구문 포함 후 삭제처리 필요
	}
}
