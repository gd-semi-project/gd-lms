package model.enumtype;

public enum StudentStatus {

    ENROLLED("ENROLLED"),
    LEAVE("LEAVE"),
    GRADUATED("GRADUATED");

    private final String label;

    StudentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StudentStatus fromLabel(String label) {
        for (StudentStatus s : values()) {
            if (s.label.equals(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown StudentStatus: " + label);
    }
}
