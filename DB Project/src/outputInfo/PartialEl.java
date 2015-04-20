/**
 * 
 */
package outputInfo;

import java.util.ArrayList;

import objDB.Column;

/**
 * @author Sennar
 * 
 * Класс для описания частично совпадающих полей базы данных 
 */

public class PartialEl {
	private ArrayList<Column> refCol = new ArrayList<Column>();
	private ArrayList<Column> partCol = new ArrayList<Column>();
	
	public boolean isEmpty() {
		if ((refCol.size() != 0) && (partCol.size() != 0)) {
			return false;
		}
		return true;
	}
	
	public void addFields(Column c1, Column c2) {
		refCol.add(c1);
		partCol.add(c2);
	}
	
	public ArrayList<Column> getReferenceFields() {
		return refCol;
	}
	
	public ArrayList<Column> getPartialFields() {
		return partCol;
	}
}
