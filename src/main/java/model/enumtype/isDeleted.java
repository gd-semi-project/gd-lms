package model.enumtype;


public enum isDeleted {
    Y("Y"), N("N");

    private final String dbValue;

    isDeleted(String dbValue) {
        this.dbValue = dbValue;
    }

    public String toDb() {
        return dbValue;
    }

    public static isDeleted fromDb(String db) {
        if (db == null) return N;
        String v = db.trim().toUpperCase();
        if ("Y".equals(v)) return Y;
        if ("N".equals(v)) return N;
        throw new IllegalArgumentException("Invalid isDeleted db value: " + db);
    }
}