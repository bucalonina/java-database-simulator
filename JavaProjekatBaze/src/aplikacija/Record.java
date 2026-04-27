package aplikacija;

import java.util.*;

public class Record {
	private List<String> kljucevi;

    public Record(List<String> values) {
        this.kljucevi = values;
    }

    public List<String> getValues() {
        return kljucevi;
    }

    @Override
    public String toString() {
        return String.join(" | ", kljucevi);
    }
}
