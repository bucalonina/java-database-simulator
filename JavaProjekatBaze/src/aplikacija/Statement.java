package aplikacija;

import java.util.*;

public class Statement {
    private String upit;

    public Statement(String up) {
        this.upit = up.trim();                 //uklanja razmake pri pocetku i pri kraju, unutrasnje ne
    }

    public Result execute(Database db) {
        try {
            String[] delovi = upit.split("\\s+");              //funkcija koja vraca niz stringova u odnosu na delitelj \\s+
            String komanda = delovi[0].toUpperCase();            //pretvara sve karaktere u stringu u velika slova

            switch (komanda) {
                case "CREATE":
                    return handleCreate(db);
                case "INSERT":
                    return handleInsert(db);
                case "SELECT":
                    return handleSelect(db);
                case "DROP":
                    return handleDrop(db);
                case "UPDATE":
                    return handleUpdate(db);
                case "DELETE":
                    return handleDelete(db);
                default:
                    return new Error("Nepoznata komanda: " + komanda);
            }
        } catch (Exception e) {
            return new Error(e.getMessage());
        }
    }

    private Result handleCreate(Database db) {
    	// Podelimo upit na reči
        String[] reci = upit.trim().split("\\s+");
        
        // Proveravamo da li postoji druga reč i da li je TABLE
        if (reci.length < 2 || !reci[1].equalsIgnoreCase("TABLE")) {
            return new Error("Neispravna komanda: " + reci[0]);
        }
    	
        // CREATE TABLE users (id, name, age)
        int startIndex = upit.toUpperCase().indexOf("TABLE") + 5; // posle TABLE
        int krajIndex = upit.indexOf("("); // pozicija otvarajuće zagrade

        // Ime tabele je tekst između "TABLE" i "("
        String ime = upit.substring(startIndex, krajIndex).trim();   //vraca string reci izvucene iz reci od indeksa do indeksa

        // Kolone između zagrada
        String inside = upit.substring(krajIndex + 1, upit.indexOf(")"));
        List<String> kol = Arrays.asList(inside.split(",\\s*"));  //asList pretvara niz u listu

        db.createTable(ime, kol);
        return new Result("Kreirana je tabela: " + ime);
    }

    private Result handleInsert(Database db) {
        // INSERT INTO users VALUES (1, 'Ana', 25)
        String tableName =upit.split("\\s+")[2];
        String inside = upit.substring(upit.indexOf("(") + 1, upit.indexOf(")"));
        List<String> values = Arrays.asList(inside.split(",\\s*"));
        db.getTable(tableName).insert(new Record(values));
        return new Result("Ubacen red u tabelu: " + tableName);
    }

    private Result handleSelect(Database db) {
        // SELECT * FROM users WHERE age = 25
        String[] delovi = upit.split("\\s+");
        String imeTab = delovi[3];
        Table t = db.getTable(imeTab);

        List<Record> redoviZaVracanje = t.getRecords(); // default svi redovi
        List<String> kolZV = new ArrayList<>(t.getKolone());

        // Provera SELECT kolona (ne samo *)
        String odredjeneKol = upit.substring(6, upit.toUpperCase().indexOf("FROM")).trim();
        if (!odredjeneKol.equals("*")) {
            kolZV = Arrays.asList(odredjeneKol.split(",\\s*"));
        }

        // Provera WHERE
        if (upit.toUpperCase().contains("WHERE")) {             //contains() vraca true/false i proverava da li string ima podstring
            String whereDeo = upit.substring(upit.toUpperCase().indexOf("WHERE") + 5).trim();
            String[] jednako = whereDeo.split("=");
            String kolonaa = jednako[0].trim();
            String vred = jednako[1].trim().replaceAll("'", ""); // ukloni ' ako postoje
            redoviZaVracanje = t.filter(kolonaa, vred);
        }

        // Ako selektujemo samo određene kolone, mapiramo vrednosti
        if (!odredjeneKol.equals("*")) {
            List<Record> mappedRows = new ArrayList<>();
            for (Record r : redoviZaVracanje) {
                List<String> noveVr = new ArrayList<>();
                for (String c : kolZV) {
                    int idx = t.getKolone().indexOf(c);
                    noveVr.add(r.getValues().get(idx));
                }
                mappedRows.add(new Record(noveVr));
            }
            redoviZaVracanje = mappedRows;
        }

        return new Data(kolZV, redoviZaVracanje);
    }

    private Result handleDrop(Database db) {
        // DROP TABLE users
        String tableName = upit.split("\\s+")[2];
        db.dropTable(tableName);
        return new Result("Obrisana tabela: " + tableName);
    }
    
    private Result handleUpdate(Database db) {
        // UPDATE users SET age = 26, name = 'Ana' WHERE id = 1
        String[] delovi = upit.split("\\s+");
        String imeT = delovi[1];
        Table t = db.getTable(imeT);

        // Izdvoji deo između SET i WHERE
        int setIndex = upit.toUpperCase().indexOf("SET") + 3;
        int whereIndex = upit.toUpperCase().indexOf("WHERE");
        String setDeo = upit.substring(setIndex, whereIndex).trim();

        // Više kolona: razdvajanje po zarezima
        String[] potrebe = setDeo.split(",\\s*");

        // WHERE uslov
        String whereDeo = upit.substring(whereIndex + 5).trim();
        String[] whereniz = whereDeo.split("=");
        String wherekol = whereniz[0].trim();
        String whereV = whereniz[1].trim().replaceAll("'", "");

        int br = 0;
        for (Record r : t.getRecords()) {
            int whereKIdx = t.getKolone().indexOf(wherekol);
            if (whereKIdx == -1) throw new RuntimeException("Kolona ne postoji: " + wherekol);

            if (r.getValues().get(whereKIdx).equals(whereV)) {
                // Za svaku kolonu iz SET dela uradi update
                for (String potreba : potrebe) {
                    String[] kv = potreba.split("=");
                    if (kv.length != 2) throw new RuntimeException("Neispravan SET format: " + potreba);
                    String col = kv[0].trim();
                    String val = kv[1].trim().replaceAll("'", "");
                    int colIdx = t.getKolone().indexOf(col);
                    if (colIdx == -1) throw new RuntimeException("Kolona ne postoji: " + col);
                    r.getValues().set(colIdx, val);
                }
                br++;
            }
        }

        return new Result("Ažurirani redovi: " + br);
    }

    
    private Result handleDelete(Database db) {
        // DELETE FROM users WHERE age = 25
        String[] delovi = upit.split("\\s+");
        String imeTa = delovi[2];
        Table t = db.getTable(imeTa);

        String whereDeo = upit.substring(upit.toUpperCase().indexOf("WHERE") + 5).trim();
        String[] whereNiz = whereDeo.split("=");
        String whereC = whereNiz[0].trim();
        String whereVred = whereNiz[1].trim().replaceAll("'", "");

        int obrisanih = t.delete(whereC, whereVred);
        return new Result("Obrisani redovi: " + obrisanih);
    }
}
