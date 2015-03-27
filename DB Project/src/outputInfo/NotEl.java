/**
 * 
 */
package outputInfo;

import java.util.ArrayList;

import objDB.Column;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * @author ������
 *
 */
public class NotEl {
	private ArrayList<Scheme> schemes = new ArrayList<Scheme>();
	private ArrayList<Table> tables = new ArrayList<Table>();
	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<Keys> keys = new ArrayList<Keys>();
	
	public void addScheme(Scheme s) {
		schemes.add(s);
	}
	
	public void addTable(Table t) {
		tables.add(t);
	}
	
	public void addColumn(Column c) {
		columns.add(c);
	}
	
	public void addKeys(Keys k) {
		keys.add(k);
	}
}