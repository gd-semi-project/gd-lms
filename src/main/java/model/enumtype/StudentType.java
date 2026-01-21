package model.enumtype;

public enum StudentType {
    UNDERGRADUATE("학부"),
    GRADUATE("대학원");

    private final String label;

    StudentType(String label) {
        this.label = label;
    }

    public static StudentType fromLabel(String label) {
        for (StudentType t : values()) {
            if (t.label.equals(label)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown StudentType: " + label);
    }
}

