package utils;

import java.security.SecureRandom;

import exception.InternalServerException;

public class PasswordUtil {
	// 혼동되는 문자 제거 (O, 0, I, l, 1)
    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGIT = "23456789";
    private static final String SPECIAL = "!@#$%";

    private static final SecureRandom random = new SecureRandom();

    /**
     * 임시 비밀번호 생성
     * - 기본 10자리
     * - 대문자, 소문자, 숫자, 특수문자 포함
     */
    public static String generateTempPassword() {
        return generate(10);
    }

    public static String generate(int length) {
    	try {
            if (length < 8) {
                throw new IllegalArgumentException("비밀번호 길이는 최소 8자 이상이어야 합니다.");
            }

            StringBuilder sb = new StringBuilder(length);

            // 정책 충족 보장
            sb.append(randomChar(UPPER));
            sb.append(randomChar(LOWER));
            sb.append(randomChar(DIGIT));
            sb.append(randomChar(SPECIAL));

            String all = UPPER + LOWER + DIGIT + SPECIAL;

            for (int i = 4; i < length; i++) {
                sb.append(randomChar(all));
            }
            return shuffle(sb.toString());
    	} catch (RuntimeException e) {
    		throw new InternalServerException(e);
    	}
    }

    private static char randomChar(String source) {
        return source.charAt(random.nextInt(source.length()));
    }

    private static String shuffle(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }
}
