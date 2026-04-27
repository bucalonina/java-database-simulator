package aplikacija;

import java.io.*;
import java.util.List;

public class SQLFormat {

    private Database db;

    public SQLFormat(Database db) {
        this.db = db;
    }

    // Sačuvaj sve tabele i redove u SQL dump
    public void save(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Table t : db.getTables()) {
                // CREATE TABLE
                writer.write("CREATE TABLE " + t.getImeTabele() + " (" +
                        String.join(", ", t.getKolone()) + ");");
                writer.newLine();
                // INSERT INTO
                for (Record r : t.getRecords()) {
                    List<String> values = r.getValues();
                    StringBuilder sb = new StringBuilder();
                    sb.append("INSERT INTO ").append(t.getImeTabele()).append(" VALUES (");
                    for (int i = 0; i < values.size(); i++) {
                        String val = values.get(i);
                        // Ako je broj ostaje bez navodnika, ako je string dodaj ''
                        if (val.matches("-?\\d+(\\.\\d+)?")) {
                            sb.append(val);
                        } else {
                            sb.append("'").append(val.replace("'", "''")).append("'");
                        }
                        if (i < values.size() - 1) sb.append(", ");
                    }
                    sb.append(");");
                    writer.write(sb.toString());
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }

    // Učitaj SQL dump i rekonstruiši bazu
    public void load(File file) throws IOException {
        db.clear(); // očisti postojeću bazu

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Statement stmt = new Statement(line);
                stmt.execute(db);
            }
        }
    }
}

