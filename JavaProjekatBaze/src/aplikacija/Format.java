package aplikacija;

import java.io.File;

public abstract class Format {

    protected Database db;

    public Format(Database db) {
        this.db = db;
    }

    // Snimi bazu u fajl
    public abstract void save(File file) throws Exception;

    // Učitaj bazu iz fajla
    public abstract void load(File file) throws Exception;
}
