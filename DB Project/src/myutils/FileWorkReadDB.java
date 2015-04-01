/**
 * Класс чтения объекта базы данных из файла
 */
package myutils;

import java.util.ArrayList;

import objDB.Column;
import objDB.DataBase;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * @author Кирилл
 *
 */
public class FileWorkReadDB {	
	public static DataBase readDB (String path) {
		DataBase db;
		ArrayList<Scheme> scheme = new ArrayList<>();
		ArrayList<Table> table = new ArrayList<>();
		ArrayList<Column> column = new ArrayList<>();
		ArrayList<Keys> keys = new ArrayList<>();
		
		StringBuilder mainStr = new StringBuilder(Filework.read(path));
		StringBuilder workStr = null;
		
		int palka = mainStr.indexOf("| ");
		do {
			palka = mainStr.indexOf("| ", palka + 1);
			workStr = new StringBuilder(mainStr.substring(0, palka));
			mainStr.delete(0, palka + 1);
			TrimStringBuilder(mainStr);
			TrimStringBuilder(workStr);
			
			
			
		} while (palka == -1);
			
		
		return db;
	}
	
	private static void TrimStringBuilder(StringBuilder str) {
		if (str.length() != 0) {
			char fchar = str.charAt(0);
			while (fchar == ' ') {
				str.deleteCharAt(0);
				fchar = str.charAt(0);
			}
			char lchar = str.charAt(str.length()-1);
			while (lchar == ' ') {
				str.deleteCharAt(str.length()-1);
				lchar = str.charAt(str.length()-1);
			}
		}
	}
}
