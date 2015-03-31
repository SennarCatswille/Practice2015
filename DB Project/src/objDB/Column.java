/**
 * Класс описания поля таблицы
 */
package objDB;

/**
 * @author Кирилл
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
	
	public boolean equals(Column obj) {
		if (this.colName.equals(obj.getName())) {
			if (this.typeName.equals(obj.getType())) {
				if (this.size == obj.getSize()) {
					return true;
				}
			}
		}
		return false;
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
