/**
 * Общий класс для базы данных
 */
package objDB;

import java.util.ArrayList;

import outputInfo.ExtraEl;
import outputInfo.NotEl;

/**
 * @author Кирилл
 *
 */
public class DataBase {
	private ExtraEl el;
	private NotEl ne;
	
	private ArrayList<Scheme> schemes = null;
	
	public DataBase(ArrayList<Scheme> s) {
		schemes = s;
	}
	
	public boolean equals(DataBase db) {
		// идельаная.equals(пользовательская)
		//- Сравнение баз данных
		return true;
	}
	
	public ArrayList<Scheme> getSchemes() {
		return schemes;
	}
}
