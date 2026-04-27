package aplikacija;

import java.util.*;

public class Database {
	private Map<String,Table> tabele=new HashMap<>();
	
	public void createTable(String imeT, List<String> kol) {
        if (tabele.containsKey(imeT)) {
            throw new RuntimeException("Tabela sa zadatim imenom vec postoji");
        }
        tabele.put(imeT, new Table(imeT, kol));
    }

    public void dropTable(String imet) {
        if (!tabele.containsKey(imet)) {
            throw new RuntimeException("Tabela sa zadatim imenom vec ne postoji");
        }
        tabele.remove(imet);
    }
    
    public Table getTable(String name) {
        return tabele.get(name);
    }
    
    public void clear() {
        tabele.clear();
    }

    public Collection<Table> getTables() {
        return tabele.values();
    }
}
