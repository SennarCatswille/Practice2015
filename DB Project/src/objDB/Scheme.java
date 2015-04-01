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
	
	public Scheme(String n, ArrayList<Table> s) {
		sName = new String(n);
		tables = new ArrayList<>(s);
	}
	
	public Scheme(Scheme s) {
		sName = s.getName();
		tables = s.getTables();
	}
	
	public boolean equals(Scheme s, ExtraEl ee, NotEl ne) {
		boolean flag = false;;
		ArrayList<Table> tables2 = s.getTables();
		if (this.sName.equals(s.getName())) {
			for (int i = 0; i < tables.size(); i++) {
				for (int j = 0; j < tables2.size(); j++) {
					Table t1 = tables.get(i);
					Table t2 = tables2.get(j);
					if (t1.equalsTable(t2, ee, ne)) {
						tables2.remove(j);
						flag = true;
						break;
					}
				}
				if (flag) {
					ne.addTable(tables.get(i));
					flag = false;
				}
			}
			if (tables2.size() != 0) {
				for (Table t : tables2) {
					ee.addTable(t);
				}
			}
		} 
		return true;		
	}
	
	public String getName() {
		return sName;
	}
	
	public ArrayList<Table> getTables() {
		return tables;
	}
}
