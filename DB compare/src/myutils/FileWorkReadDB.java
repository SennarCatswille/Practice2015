package myutils;

import java.util.ArrayList;

import objDB.Column;
import objDB.DataBase;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * Класс чтения объекта базы данных из файла
 * @author Кирилл
 *
 */
public class FileWorkReadDB {	
	public static DataBase readDB (String path) {
		DataBase db = null;
		ArrayList<Scheme> scheme = new ArrayList<>();
		ArrayList<Table> table = null;
		
		//- Расшифровываем строку
		StringCrypter crypter = new StringCrypter(new byte[]{1,4,5,6,8,9,7,8});
		String tempStr = crypter.decrypt(Filework.read(path));
		crypter = null;
		
		StringBuilder mainStr = new StringBuilder(tempStr);
		StringBuilder workStr = null;
	
		do {
			TrimStringBuilder(mainStr);
			int palka = mainStr.indexOf("|");
			//- Извлекаем часть строки, описывающую схему
			workStr = new StringBuilder(mainStr.substring(0, palka));
			mainStr.delete(0, palka + 1);
			TrimStringBuilder(workStr);
			
			Scheme schem = new Scheme();
			table = new ArrayList<Table>();
			palka = workStr.indexOf(" ");
			if (palka != -1) {
				schem.setName(workStr.substring(0, palka));
				workStr.delete(0, palka);
				TrimStringBuilder(workStr);
				
				do {
					String para = workStr.substring(0, 2);
					Table tab = new Table();
					if (para.equals("T:")) {
						palka = workStr.indexOf(" ");
						tab.setTName(workStr.substring(2, palka));
						workStr.delete(0, palka);
						TrimStringBuilder(workStr);
					}
					tab.setSName(schem.getName());
					tab.setColumns(ColumnFromStr(workStr, schem.getName(), tab.getTableName()));
					tab.setKeys(KeysFromStr(workStr, schem.getName()));
					table.add(tab);
				} while (workStr.length() > 1);
				schem.setTables(table);
				scheme.add(schem);
				table = null;
				schem = null;
			} else {
				schem.setName(new String(workStr));
				schem.setTables(table);
				scheme.add(schem);
				schem = null;
				table = null;
			}
		} while (mainStr.length() > 1);
		db = new DataBase(scheme);
		return db;
	}
	//- Считывание ключей из строки
	private static ArrayList<Keys> KeysFromStr(StringBuilder wstr, String schem) {
		boolean flag = true;
		ArrayList<Keys> keys = new ArrayList<>();
		String para = null;
		if (wstr.length() > 1) {
			para = wstr.substring(0, 2);
		
			while (para.equals("P:") && flag) {
				int probel = wstr.indexOf(" ");
				String pTable = wstr.substring(2, probel);
				wstr.delete(0, probel);
				TrimStringBuilder(wstr);
				probel = wstr.indexOf(" ");
				String pKey = null;
				if (probel == -1) {
					pKey = new String(wstr);
					wstr.delete(0, wstr.length());
					TrimStringBuilder(wstr);
					flag = false;
				} else {
					pKey = wstr.substring(0, probel);
					wstr.delete(0, probel);
					TrimStringBuilder(wstr);
					para = wstr.substring(0, 2);
				}			
				keys.add(new Keys(1, pTable, pKey, schem));
			}
			while ((para.equals("F:")) && (flag)) {
				int probel = wstr.indexOf(" ");
				String FTable = wstr.substring(2, probel);
				wstr.delete(0, probel);
				TrimStringBuilder(wstr);
				probel = wstr.indexOf(" ");
				String FKey = wstr.substring(0, probel);
				wstr.delete(0, probel);
				TrimStringBuilder(wstr);
				probel = wstr.indexOf(" ");
				String PTable = wstr.substring(0, probel);
				wstr.delete(0, probel);
				TrimStringBuilder(wstr);
				probel = wstr.indexOf(" ");
				String PKey = null;
				if (probel == -1) {
					PKey = new String(wstr);
					wstr.delete(0, wstr.length());
					TrimStringBuilder(wstr);
					flag = false;
				} else {
					PKey = wstr.substring(0, probel);
					wstr.delete(0, probel);
					TrimStringBuilder(wstr);
					para = wstr.substring(0, 2);
				}
				keys.add(new Keys(2, PKey, PTable, FKey, FTable, schem));
			}	
		}
		return keys;
	}
	//- Считывание имен полей из строки
	private static ArrayList<Column> ColumnFromStr(StringBuilder wstr, String s, String t) {
		boolean flag = true;
		ArrayList<Column> column = new ArrayList<>();
		String para = null;
		if (wstr.length() > 1) {
			para = wstr.substring(0, 2);
		
			while ((para.equals("C:")) && (flag)) {
				int probel = wstr.indexOf(" ");
				String cName = wstr.substring(2, probel);
				wstr.delete(0, probel);
				TrimStringBuilder(wstr);
				probel = wstr.indexOf(" ");
				String tName = wstr.substring(0, probel);
				wstr.delete(0, probel);
				TrimStringBuilder(wstr);
				probel = wstr.indexOf(" ");
				String size = null;
				if (probel == -1) { 
					size = new String(wstr);
					wstr.delete(0, wstr.length());
					TrimStringBuilder(wstr);
					flag = false;
				} else { 
					size = wstr.substring(0, probel);
					wstr.delete(0, probel);
					TrimStringBuilder(wstr);
					para = wstr.substring(0, 2);
				}		
				
				column.add(new Column(cName, tName, new Integer(size), s + "." + t));
			}
		}
		return column;
	}
	//- Метод обрезания пробелов в StringBuilder
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
