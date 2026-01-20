package service.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("접근 권한이 없습니다.");
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
