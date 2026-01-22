package model.enumtype;


public enum IsDeleted {
    Y("Y"), N("N");

    private final String dbValue;

    IsDeleted(String dbValue) {
        this.dbValue = dbValue;
    }

    public String toDb() {
        return dbValue;
    }

    public static IsDeleted fromDb(String db) {
        if (db == null) return N;
        String v = db.trim().toUpperCase();
        if ("Y".equals(v)) return Y;
        if ("N".equals(v)) return N;
        throw new IllegalArgumentException("Invalid IsDeleted db value: " + db);
    }
}