/**
 * ����� �������� ���� �������
 */
package objDB;

/**
 * @author ������
 *
 */
public class Column {
	private String colName;
	private String typeName;
	private String schemeTable;
	private int size;
	
	public Column(String c, String t, int s, String st) {
		colName = new String(c);
		typeName = new String(t);
		schemeTable = st;
		size = s;		
	}
	
	public Column(Column col) {
		colName = col.getName();
		typeName = col.getType();
		schemeTable = col.getSchemTable();
		size = col.getSize();
	}
	/*
	 * ����� ��������� �������
	 * ������������ ��������:
	 * 		0 - ���� ��������� ��������
	 * 		1 - ���� ���������
	 * 		2 - ���� ����������� ���������
	 */
	public int equals(Column obj) {
		if (this.colName.equals(obj.getName())) {
			if (this.typeName.equals(obj.getType()) && (this.size == obj.getSize())) {
				return 1;
			}
			if (this.typeName.equals(obj.getType()) || (this.size == obj.getSize())) {
				return 2;
			}
		}
		return 0;
	}
	
	public String getName() {
		return colName;
	}
	
	public String getType() {
		return typeName;
	}
	
	public String getSchemTable() {
		return schemeTable;
	}
	
	public int getSize() {
		return size;
	}
}
