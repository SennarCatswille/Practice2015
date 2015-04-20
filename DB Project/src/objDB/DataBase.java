/**
 * Общий класс для базы данных
 */
package objDB;

import java.util.ArrayList;

import outputInfo.ExtraEl;
import outputInfo.NotEl;
import outputInfo.PartialEl;

/**
 * @author Кирилл
 *
 */
public class DataBase {
	private ExtraEl ee;
	private NotEl ne;
	private PartialEl pe;
	
	private ArrayList<Scheme> schemes = null;
	/*
	 * Конструктор класса
	 */
	public DataBase(ArrayList<Scheme> s) {
		schemes = new ArrayList<Scheme>(s);
		ne = new NotEl();
		ee = new ExtraEl();
	}
	/*
	 * Конструктор для копирования класса
	 */
	public DataBase(DataBase d) {
		schemes = d.getSchemes();
	}
	/*
	 * Метод сравнения баз данных
	 * Использование:
	 * 		идельаная.equals(пользовательская)
	 */
	public boolean equals(DataBase db) {
		int flag = 0;
		ArrayList<Scheme> badSchemes = db.getSchemes();
		for (int i = 0; i < schemes.size(); i++) {
			Scheme s1 = schemes.get(i);
			for (int j = 0; j < badSchemes.size(); j++) {
				Scheme s2 = badSchemes.get(j);
				if (s1.equals(s2, ee, ne, pe)) {
					badSchemes.remove(j);
					flag++;
					break;
				}
			}
			if (flag == 0) {
				ne.addScheme(schemes.get(i));
			}
			flag = 0;
		}
		if (badSchemes.size() != 0) {
			for (Scheme s : badSchemes) {
				ee.addScheme(s);
			}
		}
		return true;
	}
	/*
	 * Геттер схем базы данных
	 */
	public ArrayList<Scheme> getSchemes() {
		return schemes;
	}
	
	public ExtraEl getEE() {
		return ee;
	}
	
	public NotEl getNE() {
		return ne;
	}
	
	public PartialEl getPE() {
		return pe;
	}
}
