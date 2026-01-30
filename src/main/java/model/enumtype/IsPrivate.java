package model.enumtype;

public enum IsPrivate {
    Y("비공개"),
    N("공개");

    private final String label;

    IsPrivate(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static IsPrivate fromDb(String db) {
        return "Y".equals(db) ? Y : N;
    }

    public String toDb() {
        return name(); // Y / N
    }
}
