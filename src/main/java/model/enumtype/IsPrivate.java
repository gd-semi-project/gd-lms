package model.enumtype;

public enum IsPrivate {
    Y("Y"), N("N");

    private final String dbValue;

    IsPrivate(String dbValue) {
        this.dbValue = dbValue;
    }

    public String toDb() {
        return dbValue;
    }

    public static IsPrivate fromDb(String db) {
        if (db == null) return N;
        String v = db.trim().toUpperCase();
        if ("Y".equals(v)) return Y;
        if ("N".equals(v)) return N;
        throw new IllegalArgumentException("Invalid IsPrivate db value: " + db);
    }
}
