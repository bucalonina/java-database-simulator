package aplikacija;

import java.util.*;

public class Data extends Result {
    private List<Record> redovi;
    private List<String> kolone;

    public Data(List<String> col, List<Record> re) {
        super("Rezultat: " + re.size() + " redova");
        this.kolone = col;
        this.redovi = re;
    }


    public List<Record> getRedovi() {
		return redovi;
	}


	public List<String> getKolone() {
		return kolone;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" | ", kolone)).append("\n");
        for (Record r : redovi) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }
}
