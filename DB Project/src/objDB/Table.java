/**
 * Класс описания таблицы
 */
package objDB;

import java.util.ArrayList;

/**
 * @author Кирилл
 *
 */
public class Table {
	private String tName;
	ArrayList<Column> columns = null;
	ArrayList<Keys> keys = null;
	
	public Table(String n, ArrayList<Column> c, ArrayList<Keys> k) {
		tName = n;
		columns = c;
		keys = k;
	}
	/**
	 * Проверка столбцов и ключей, если сходится - удаляем,
	 * если нет - заносим в лог и удаляем
	 */
	
	public String getTableName() {
		return tName;
	}
}
