/**
 * Класс, объединяющий ExtraEl, NotEl и PartialEl в выводе файла
 */
package outputInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import objDB.Column;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * @author Кирилл
 *
 */
public class OutInfo {
	private static String nl = System.getProperty("line.separator");
	private static String tab = "    ";
	private ArrayList<Scheme> schemes = new ArrayList<Scheme>();
	private ArrayList<Table> tables = new ArrayList<Table>();
	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<Keys> keys = new ArrayList<Keys>();	
	
	private ExtraEl ee = null;
	private NotEl ne = null;
	private PartialEl pe = null;
	
	public OutInfo(ExtraEl e, NotEl n, PartialEl p) {
		ee = e;
		ne = n;
		pe = p;
	}
	/*
	 * Метод вывода файла результата
	 * 
	 * Возвращаемые значения:
	 * 0 - успешно завершено
	 * 1 - ошибка открытия файла
	 * 2 - ошибка записи в файл
	 * 3 - ошибка сохранения информации на диске
	 * 4 - ошибка закрытия потока вывода
	 */
	public int getFile(String foname) {
		//l.addMsg("Формирую выходной файл...");
		//char tab = '\u0009';
		if (!ee.isEmpty() || !ne.isEmpty()) {
			BufferedWriter out;
			try {
				out = new BufferedWriter(new FileWriter(foname));
			} catch (IOException e) {
				e.printStackTrace();
				return 1;
			}
			try {
			//- Вывод в файл
				ExtraOutInfo(out);
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			}
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return 3;
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return 4;
			}
			return 0;
		}
		return -1;
	}
	
	private ArrayList<String> getSchemes(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			schemes = ((ExtraEl) obj).getSchemes();
		}
		if (obj instanceof NotEl) {
			schemes = ((NotEl) obj).getSchemes();
		}
		for (Scheme s : schemes) {
			str.add(tab + s.getName() + nl);
		}
		return str;
	}
	
	private ArrayList<String> getTables(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			tables = ((ExtraEl) obj).getTables();
		}
		if (obj instanceof NotEl) {
			tables = ((NotEl) obj).getTables();
		}
		for (Table t : tables) {
			str.add(tab + t.getSchemeName() + "." + t.getTableName() + nl);
		}
		return str;
	}
	
	private ArrayList<String> getColumns(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		ArrayList<Column> col2 = new ArrayList<>();
		int flag = 0;
		if (obj instanceof ExtraEl) {
			columns = ((ExtraEl) obj).getColumns();
		}
		if (obj instanceof NotEl) {
			columns = ((NotEl) obj).getColumns();
		}
		if (obj instanceof PartialEl) {
			columns = ((PartialEl) obj).getReferenceFields();
			col2 = ((PartialEl) obj).getPartialFields();
			flag = 1;
		}
		if (flag == 0) {
			for (Column c : columns) {
				str.add(tab + c.getSchemTable() + " " + c.getName() + " " + c.getType() + "(" + c.getSize() + ")" + nl);
			}
		} else {
			for (int i = 0; i < columns.size(); i++) {
				StringBuilder newStr = new StringBuilder();
				Column c1 = columns.get(i);
				Column c2 = col2.get(i);
				newStr.append(tab + c1.getSchemTable() + " " + c1.getName() + " " + c1.getType() + "(" + c1.getSize() + ") - ").
				append(c2.getSchemTable() + " " + c2.getName() + " " + c2.getType() + "(" + c2.getSize() + ")" + nl);
				str.add(newStr.toString());
			}
		}
		return str;
	}
	
	private ArrayList<String> getFKeys(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			keys = ((ExtraEl) obj).getKeys();
		}
		if (obj instanceof NotEl) {
			keys = ((NotEl) obj).getKeys();
		}
		for (Keys k : keys) {
			if (k.getKeyType() == 2) {
				str.add(tab + k.getScheme() + "." + k.getPTable() + " " + k.getKeyName() + " - " + k.getFTable() + " (" + k.getFKeyName() + ")" + nl);
			}
		}
		return str;
	}
	
	private ArrayList<String> getPKeys(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			keys = ((ExtraEl) obj).getKeys();
		}
		if (obj instanceof NotEl) {
			keys = ((NotEl) obj).getKeys();
		}
		for (Keys k : keys) {
			if (k.getKeyType() == 1) {
				str.add(tab + k.getScheme() + "." + k.getPTable() + " " + k.getKeyName() + nl);
			}
		}
		return str;
	}
	
	public void ExtraOutInfo(BufferedWriter out) throws IOException {
		ArrayList<String> outStr = null;
		outStr = getSchemes(ee);
		if (!schemes.isEmpty()) {
			if (schemes.size() == 1) {
				out.write("Лишняя схема:" + nl);
			} else {
				out.write("Лишние схемы:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getSchemes(ne);
		if (!schemes.isEmpty()) {
			if (schemes.size() == 1) {
				out.write("Отсутствует схема:" + nl);
			} else {
				out.write("Отсутствуют схемы:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getTables(ee);
		if (!tables.isEmpty()) {
			if (tables.size() == 1) {
				out.write("Лишняя таблица:" + nl);
			} else {
				out.write("Лишние таблицы:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getTables(ne);
		if (!tables.isEmpty()) {
			if (tables.size() == 1) {
				out.write("Отсутсвует таблица:" + nl);
			} else {
				out.write("Отсутствуют таблицы:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getColumns(ee);
		if (!columns.isEmpty()) {
			if (columns.size() == 1) {
				out.write("Лишнее поле:" + nl);
			} else {
				out.write("Лишние поля:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getColumns(ne);
		if (!columns.isEmpty()) {
			if (columns.size() == 1) {
				out.write("Отсутствует поле:" + nl);
			} else {
				out.write("Отсутствуют поля:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getColumns(pe);
		if (!columns.isEmpty()) {
			if (columns.size() == 1) {
				out.write("Несовпадающее поле:" + nl);
			} else {
				out.write("Несовпадающие поля:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getPKeys(ee);
		if (!keys.isEmpty()) {
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("Лишний первичный ключ:" + nl);
				} else {
					out.write("Лишние первичные ключи: " + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
			outStr = getFKeys(ee);
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("Лишний внешний ключ:" + nl);
				} else {
					out.write("Лишние внешние ключи:" + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
		}
		outStr = getPKeys(ne);
		if (!keys.isEmpty()) {
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("Отсутсвует первичный ключ:" + nl);
				} else {
					out.write("Отсутствуют первичные ключи: " + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
			outStr = getFKeys(ne);
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("Отсутствует внешний ключ:" + nl);
				} else {
					out.write("Отсутствуют внешние ключи:" + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
		}
		clearAllArrayList();
	}
	
	private void clearAllArrayList() {
		schemes = null;
		tables = null;
		columns = null;
		keys = null;
	}
}
