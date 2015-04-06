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
	/*
	 * Пустой конструктор класса
	 */
	public Table() {
		tName = null;
		sName = null;
		columns = null;
		keys = null;
	}
	/**
	 * Проверка столбцов и ключей, если сходится - удаляем,
	 * если нет - заносим в лог и удаляем
	 */
	
	public boolean equalsTable(Table t, ExtraEl ee, NotEl ne) {
		int flag = 0;
		if (this.tName.equals(t.getTableName())) {
			ArrayList<Column> c = t.getColumns(); 
			for (int i = 0; i < columns.size(); i++) {
				for (int j = 0; j < c.size(); j++) {
					if (columns.get(i).equals(c.get(j))) {
						c.remove(j);
						flag++;
						break;
					}
				}
				if (flag == 0) {
					ne.addColumn(columns.get(i));
				}
				flag = 0;
			}
			if (c.size() != 0) {
				for (Column col : c) {
					ee.addColumn(col);
				}
			}
			ArrayList<Keys> k = t.getAllKeys();
			flag = 0;
			for (int i = 0; i < keys.size(); i++) {
				for (int j = 0; j < k.size(); j++) {
					if(keys.get(i).equals(k.get(j))) {
						k.remove(j);
						flag++;
						break;
					}
				}
				if (flag == 0) {
					ne.addKeys(keys.get(i));
				}
				flag = 0;
			}
			if (k.size() != 0) {
				for (Keys key : k) {
					ee.addKeys(key);
				}
			}
		} else return false;
		return true;
	}
	/*
	 * Геттеры
	 */
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
	/*
	 * Сеттеры
	 */
	public void setTName(String t) {
		tName = new String(t);
	}
	
	public void setSName(String s) {
		sName = new String(s);
	}
	
	public void setColumns(ArrayList<Column> c) {
		columns = new ArrayList<>(c);
	}
	
	public void setKeys(ArrayList<Keys> k) {
		keys = new ArrayList<>(k);
	}
}
