package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import exception.InternalServerException;

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

        } catch (InternalServerException | NoSuchAlgorithmException e) {
            throw new InternalServerException(e);
        }
	}
}
