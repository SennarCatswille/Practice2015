/**
 * Класс описания таблицы
 */
package objDB;

import java.util.ArrayList;

import outputInfo.ExtraEl;
import outputInfo.NotEl;

/**
 * @author Кирилл
 *
 */
public class Table {
	private String tName;
	private String sName;
	ArrayList<Column> columns = null;
	ArrayList<Keys> keys = null;
	/*
	 * Основной конструктор класса
	 */
	public Table(String n, String s, ArrayList<Column> c, ArrayList<Keys> k) {
		tName = new String(n);
		sName = new String(s);
		columns = new ArrayList<>(c);
		keys = new ArrayList<>(k);
	}
	/*
	 * Конструктор копирования класса
	 */
	public Table(Table t) {
		tName = t.getTableName();
		sName = t.getSchemeName();
		columns = t.getColumns();
		keys = t.getAllKeys();
	}
	
	/**
	 * Проверка столбцов и ключей, если сходится - удаляем,
	 * если нет - заносим в лог и удаляем
	 */
	
	public boolean equalsTable(Table t, ExtraEl ee, NotEl ne) {
		boolean flag = false;
		if (this.tName.equals(t.getTableName())) {
			ArrayList<Column> c = t.getColumns(); 
			for (int i = 0; i < columns.size(); i++) {
				for (int j = 0; j < c.size(); j++) {
					if (columns.get(i).equals(c.get(j))) {
						c.remove(j);
						flag = true;
						break;
					}
				}
				if (flag) {
					ne.addColumn(columns.get(i));
					flag = false;
				}
			}
			if (c.size() != 0) {
				for (Column col : c) {
					ee.addColumn(col);
				}
			}
			ArrayList<Keys> k = t.getAllKeys();
			for (int i = 0; i < keys.size(); i++) {
				for (int j = 0; j < k.size(); j++) {
					if(keys.get(i).equals(k.get(j))) {
						k.remove(j);
						flag = true;
						break;
					}
				}
				if (flag) {
					ne.addKeys(keys.get(i));
					flag = false;
				}
			}
			if (k.size() != 0) {
				for (Keys key : k) {
					ee.addKeys(key);
				}
			}
		}
		return false;
	}
	
	public String getTableName() {
		return tName;
	}
	
	public String getSchemeName() {
		return sName;
	}
	
	public ArrayList<Keys> getPKeys() {
		ArrayList<Keys> k = new ArrayList<Keys>();
		for (Keys key : keys) {
			if (key.getKeyType() == 1) {
				k.add(key);
			}
		}
		return k;
	}
	
	public ArrayList<Keys> getFKeys() {
		ArrayList<Keys> k = new ArrayList<Keys>();
		for (Keys key : keys) {
			if (key.getKeyType() == 2) {
				k.add(key);
			}
		}
		return k;
	}
	
	public ArrayList<Keys> getAllKeys() {
		return keys;
	}
	
	public ArrayList<Column> getColumns() {
		return columns;
	}
}
