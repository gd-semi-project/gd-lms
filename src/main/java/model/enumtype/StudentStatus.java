package model.enumtype;

public enum StudentStatus {

    ENROLLED("재학"),
    LEAVE("휴학"),
    GRADUATED("졸업");

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
