/**
 * Описание схемы
 */
package objDB;

import java.util.ArrayList;

/**
 * @author Кирилл
 *
 */
public class Scheme {
	private String sName;
	private ArrayList<Table> tables = null;
	
	public Scheme(String n, ArrayList<Table> s) {
		sName = new String(n);
		tables = s;
	}
	
	public String getName() {
		return sName;
	}
	
	public ArrayList<Table> getTables() {
		return tables;
	}
}
