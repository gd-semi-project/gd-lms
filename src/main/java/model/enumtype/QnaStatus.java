package model.enumtype;

public enum QnaStatus {
    OPEN("답변대기"),
    ANSWERED("답변완료"),
    CLOSED("종료");

    private final String displayName;

    QnaStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
