/**
 * ����� �������� �������
 */
package objDB;

import java.util.ArrayList;

/**
 * @author ������
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
	 * �������� �������� � ������, ���� �������� - �������,
	 * ���� ��� - ������� � ��� � �������
	 */
	
	public String getTableName() {
		return tName;
	}
}
