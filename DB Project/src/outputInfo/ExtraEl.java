package outputInfo;

import java.util.ArrayList;

import objDB.Column;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

public class ExtraEl {
	private ArrayList<Scheme> schemes = new ArrayList<Scheme>();
	private ArrayList<Table> tables = new ArrayList<Table>();
	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<Keys> keys = new ArrayList<Keys>();
	
	public boolean isEmpty() {
		if ((schemes.size()!=0) || (tables.size()!=0) || (columns.size()!=0) || (keys.size()!=0)) {
			return false;
		}
		return true;
	}
	
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
	
	public ArrayList<Scheme> getSchemes() {
		return schemes;
	}
	
	public ArrayList<Table> getTables() {
		return tables;
	}
	
	public ArrayList<Column> getColumns() {
		return columns;
	}
	
	public ArrayList<Keys> getKeys() {
		return keys;
	}
}
