package outputInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import objDB.Column;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

public class ExtraEl {
	private static String nl = System.getProperty("line.separator");
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
	
	private ArrayList<String> getSchemes() {
		ArrayList<String> str = new ArrayList<>();
		for (Scheme s : schemes) {
			str.add(s.getName());
		}
		return str;
	}
	
	private ArrayList<String> getTables() {
		ArrayList<String> str = new ArrayList<>();
		for (Table t : tables) {
			str.add(t.getSchemeName() + "." + t.getTableName() + nl);
		}
		return str;
	}
	
	private ArrayList<String> getColumns() {
		ArrayList<String> str = new ArrayList<>();
		for (Column c : columns) {
			str.add(c.getSchemTable() + " " + c.getName() + " " + c.getType() + "(" + c.getSize() + ")" + nl);
		}
		return str;
	}
	
	private ArrayList<String> getFKeys() {
		ArrayList<String> str = new ArrayList<>();
		for (Keys k : keys) {
			if (k.getKeyType() == 2) {
				str.add(k.getScheme() + "." + k.getPTable() + " " + k.getKeyName() + " - " + k.getFTable() + " (" + k.getFKeyName() + ")");
			}
		}
		return str;
	}
	
	private ArrayList<String> getPKeys() {
		ArrayList<String> str = new ArrayList<>();
		for (Keys k : keys) {
			if (k.getKeyType() == 1) {
				str.add(k.getScheme() + "." + k.getPTable() + " " + k.getKeyName() + nl);
			}
		}
		return str;
	}
	
	public void OutInfo(BufferedWriter out) throws IOException {
		ArrayList<String> outStr = null;
		if (!schemes.isEmpty()) {
			outStr = getSchemes();
			if (schemes.size() == 1) {
				out.write("Отсутствует схема:" + nl);
			} else {
				out.write("Отсутствуют схемы:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		if (!tables.isEmpty()) {
			outStr = getTables();
			if (tables.size() == 1) {
				out.write("Отсутсвует таблица:" + nl);
			} else {
				out.write("Отсутствуют таблицы:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		if (!columns.isEmpty()) {
			outStr = getColumns();
			if (columns.size() == 1) {
				out.write("Отсутствует поле:" + nl);
			} else {
				out.write("Отсутствуют поля:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		if (!keys.isEmpty()) {
			outStr = getPKeys();
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("Отсутсвует первичный ключ:" + nl);
				} else {
					out.write("Отсутствуют первичные ключи: " + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
			outStr = getFKeys();
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("Отсутствует внешний ключ:" + nl);
				} else {
					out.write("Отсутствуют внешние ключи:" + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
		}
	}
}
