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
	
	public void addReferenceField(Column c) {
		refCol.add(c);
	}
	
	public void addPartialField(Column c) {
		partCol.add(c);
	}
	
	public ArrayList<Column> getReferenceFields() {
		return refCol;
	}
	
	public ArrayList<Column> getPartialFields() {
		return partCol;
	}
}
