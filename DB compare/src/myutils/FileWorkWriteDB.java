/**
 * Класс записи объекта базы данных в файл
 */
package myutils;

import java.util.ArrayList;

import objDB.Column;
import objDB.DataBase;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * @author Sennar
 *
 */
public class FileWorkWriteDB {
	private DataBase db = null;
	//private Logs l;
	
	/*
	 * Конструктор класса
	 */
	public FileWorkWriteDB(DataBase temp) {
		db = temp;
		//l = log;
	}
	
	public StringBuilder CreateFileDB() {
		StringBuilder outStr = new StringBuilder();
		ArrayList<Scheme> scheme = db.getSchemes();
		ArrayList<Table> table = new ArrayList<>();
		//l.addMsg("Начинаю вывод в файл");
		for (Scheme s : scheme) {
			outStr.append(s.getName());
			table = s.getTables();
			for (Table t : table) {
				outStr.append(" T:" + t.getTableName() + " ");
				outStr.append(getStringColumn(t) + " ");
				outStr.append(getStringKeys(t));
			}
			outStr.append(" | ");
		}
		//l.addMsg("Вывод окончен");
		return outStr;
	}
	
	private StringBuilder getStringColumn(Table t) {
		ArrayList<Column> col = t.getColumns();
		StringBuilder str = new StringBuilder();
		
		for (Column c : col) {
			str.append(" C:").append(c.getName() + " ").append(c.getType() + " ").append(c.getSize());
		}
		
		return str;
	}
	
	private StringBuilder getStringKeys(Table t) {
		StringBuilder str = new StringBuilder();
		ArrayList<Keys> key1 = t.getPKeys();
		ArrayList<Keys> key2 = t.getFKeys();
		
		for (Keys k : key1) {
			str.append(" P:").append(k.getPTable() + " ").append(k.getKeyName());
		}
		
		for (Keys k : key2) {
			str.append(" F:").append(k.getFTable() + " ").append(k.getFKeyName() + " ").append(k.getPTable() + " ").append(k.getKeyName());
		}
		
		return str;
	}
	
}
