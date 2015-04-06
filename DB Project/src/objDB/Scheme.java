/**
 * Описание схемы
 */
package objDB;

import java.util.ArrayList;

import outputInfo.ExtraEl;
import outputInfo.NotEl;

/**
 * @author Кирилл
 *
 */
public class Scheme {
	private String sName;
	private ArrayList<Table> tables = new ArrayList<>();
	
	public Scheme() {
		sName = null;
		tables = null;
	}
	
	public Scheme(String n, ArrayList<Table> s) {
		sName = new String(n);
		tables = new ArrayList<>(s);
	}
	
	public Scheme(Scheme s) {
		sName = s.getName();
		tables = s.getTables();
	}
	
	public boolean equals(Scheme s, ExtraEl ee, NotEl ne) {
		int flag = 0;
		ArrayList<Table> tables2 = s.getTables();
		if (this.sName.equals(s.getName())) {
			for (int i = 0; i < tables.size(); i++) {
				Table t1 = tables.get(i);
				for (int j = 0; j < tables2.size(); j++) {
					Table t2 = tables2.get(j);
					if (t1.equalsTable(t2, ee, ne)) {
						tables2.remove(j);
						flag++;
						break;
					}
				}
				if (flag == 0) {
					ne.addTable(t1);
				}
				flag = 0;
			}
			if (tables2.size() != 0) {
				for (Table t : tables2) {
					ee.addTable(t);
				}
			}
		} else return false;
		return true;		
	}
	
	public void setName(String n) {
		sName = new String(n);
	}
	
	public String getName() {
		return sName;
	}
	
	public void setTables(ArrayList<Table> t) {
		tables = new ArrayList<Table>(t);
	}
	
	public ArrayList<Table> getTables() {
		return tables;
	}
}
