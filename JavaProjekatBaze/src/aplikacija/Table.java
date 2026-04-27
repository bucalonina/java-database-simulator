package aplikacija;

import java.util.*;

public class Table {
	 private String imeTabele;
	 private List<String> kolone;
	 private List<Record> redovi = new ArrayList<>();
	 
	 public Table(String name, List<String> columns) {
	        this.imeTabele = name;
	        this.kolone = columns;
	 }

	 public void insert(Record red) {
		 if (red.getValues().size() != kolone.size()) {
			 throw new RuntimeException("Nije dobar broj kljuceva u redu.");
	     }
	     redovi.add(red);
	 }
	 
	 public List<Record> getRecords() { return redovi; }

	public String getImeTabele() {
		return imeTabele;
	}

	public List<String> getKolone() {
		return kolone;
	}
	
	public List<Record> filter(String column, String value) {
	    int colIndex = kolone.indexOf(column);
	    if (colIndex == -1) throw new RuntimeException("Kolona ne postoji: " + column);

	    List<Record> result = new ArrayList<>();
	    for (Record r : redovi) {
	        if (r.getValues().get(colIndex).equals(value)) {
	            result.add(r);
	        }
	    }
	    return result;
	}
	
	// UPDATE po koloni
	public int update(String kol, String vr, String whereKol, String wherev) {
	    int colIndex = kolone.indexOf(kol);
	    int whereIndex = kolone.indexOf(whereKol);
	    if (colIndex == -1) throw new RuntimeException("Kolona ne postoji: " + kol);
	    if (whereIndex == -1) throw new RuntimeException("Kolona ne postoji: " + whereKol);

	    int br = 0;
	    
	    //iterator
	    for (Record r : redovi) {
	        if (r.getValues().get(whereIndex).equals(wherev)) {
	            r.getValues().set(colIndex, vr);
	            br++;
	        }
	    }
	    return br;
	}

	// DELETE po koloni
	public int delete(String wherek, String wherevr) {
	    int whereIndex = kolone.indexOf(wherek);
	    if (whereIndex == -1) throw new RuntimeException("Kolona ne postoji: " + wherek);

	    int originalSize = redovi.size();
	    redovi.removeIf(r -> r.getValues().get(whereIndex).equals(wherevr));
	    return originalSize - redovi.size();
	}

}
