package model.enumtype;

public enum Role {
	STUDENT("학생"),
    ADMIN("관리자"),
    INSTRUCTOR("교수");

    private final String label;
    Role(String label) { this.label = label; }
    public String getLabel() { return label; }

    public static Role fromLabel(String label) {
        for (Role r : Role.values()) {
            if (r.label.equals(label)) return r;
        }
        // TODO : 예외
        throw new IllegalArgumentException("Unknown role: " + label);
    }

}
