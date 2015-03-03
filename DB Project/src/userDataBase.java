import java.sql.*;
import java.util.ArrayList;
import java.io.*;

/**
 * @author Sennar
 *
 */
public class userDataBase {
	private Connection con = null;
	private DatabaseMetaData dbmeta = null;
	private StringBuilder text = new StringBuilder();
	private boolean flag = false;
	Logs l = null;
/*
 * Подключение к PostgreSQL
 * 
	DataBase (String host, String port, String name, String user, String pass) {
		try {
			Class.forName("org.postgresql.Driver");
			String strcon = "jdbc:postgresql://" + host + ":" + port + "/" + name;
			con = DriverManager.getConnection(strcon, user, pass);
		} catch (ClassNotFoundException e) {
		    System.out.println("Не найден драйвер СУБД!");
		    e.printStackTrace();
		    return;
		} catch (SQLException e) {
			System.out.println("Не удалось подключиться к БД!");
			e.printStackTrace();
		}
	}
*/
	
	userDataBase (Logs g, String host, String name, String user, String pass, String newtext) {
		boolean isDriverRegistred = false;
		l = g;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
	        l.addMsg("Драйвер DB2 загружен успешно!");
	        isDriverRegistred = true;
		} catch (ClassNotFoundException e) {
			l.addMsg("Ошибка загрузки драйвера DB2!");
			l.addMsg(e.getMessage());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + host + "/" + name;
				//String strcon = "jdbc:db2:" + name;
				con = DriverManager.getConnection(strcon, user, pass);
				l.addMsg("Подключение к БД установлено успешно!");
				dbmeta = con.getMetaData();
				flag = true;
			} catch (SQLException e) {
				l.addMsg("Не удалось подключиться к БД!");
				l.addMsg(e.getMessage());
			}
		}	
		text.append(newtext);
	}
	
	public boolean GetState() {
		return flag;
	}
	
	public int analysis (String foname) {
		StringBuilder workstr = new StringBuilder();
		StringBuilder tablestr = new StringBuilder();
		// Для отсутствующих элементов
		ArrayList<String> notElSchemes = new ArrayList<String>();
		ArrayList<String> notElTables = new ArrayList<String>();
		ArrayList<String> notElColumns = new ArrayList<String>();
		ArrayList<String> notElFKeys = new ArrayList<String>();
		ArrayList<String> notElPKeys = new ArrayList<String>();
		// Для лишних элементов
		ArrayList<String> elSchemes = new ArrayList<String>();
		ArrayList<String> elTables = new ArrayList<String>();
		ArrayList<String> elColumns = new ArrayList<String>();
		ArrayList<String> elFKeys = new ArrayList<String>();
		ArrayList<String> elPKeys = new ArrayList<String>();
		int pos = 0;
		l.addMsg("Начинаю анализ базы данных...");
		try {
			dbmeta = con.getMetaData();
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				pos = 0;
				String sname = ss.get(i);
			// Ищем схему
				pos = text.indexOf(sname);
				if (pos == -1) {
					elSchemes.add(sname + System.getProperty("line.separator"));
					continue;
				}					
				l.addMsg("Анализирую схему " + Integer.toString(i+1) + "/" + Integer.toString(ss.size()) + " [" + sname + "]");
				//temp = text.substring(0, pos);
				//pos += sname.length();
			// Ищем конец схемы
				
				int palka = text.indexOf("|", pos+1);
				workstr.delete(0, workstr.length());
				workstr.append(text.substring(pos + sname.length(), palka));
				TrimStringBuilder(workstr);
				//workstr.trim();
				//int ololo = workstr.length()+1;
				//text.append(text.substring(0, pos) + text.substring(pos + 2 + workstr.length() + sname.length()));		
				text.delete(pos, palka + 1);
				TrimStringBuilder(text);
				// Получаем таблицы
				ResultSet rs = dbmeta.getTables(null, sname, "%", null);
				while (rs.next()) {
					String tname = rs.getString(3);
					pos = workstr.indexOf("T:" + tname);
					if (pos == -1) {
						notElTables.add(TableToString(tname, sname, false));
						continue;
					}
					int wnext = workstr.indexOf("T:", pos+2);
					tablestr.delete(0, tablestr.length());
					if (wnext == -1) {
						tablestr.append(workstr.substring(pos + tname.length() + 2));
						workstr.delete(pos, workstr.length());
					} else {
						tablestr.append(workstr.substring(pos + tname.length() + 2, wnext));
						workstr.delete(pos, wnext);
					}
					TrimStringBuilder(tablestr);
					//workstr = workstr.substring(tname.length()+2).trim();
					// Проверка индексов
					/*
					ArrayList<String> ind = Indexes(sname, tname);
					for (int j = 0; j < ind.size(); j++) {
						//tempstr = text.substring(0, text.indexOf(":", 3)-2);
						String tind = ind.get(j);
						pos = tablestr.indexOf(tind);
						if (pos == -1) {
							notEl.add(IndexToString(ind.get(j), tname, false));
							continue;
						}
						if (ind.get(j).length() != tablestr.length()) {
							tablestr.delete(pos, pos + tind.length());
							TrimStringBuilder(tablestr);
						} else tablestr.delete(0, tablestr.length());
					}
					ind = null;*/
					// Проверка полей таблиц
					ArrayList<String> col = Tables(sname, tname);
					for (int j = 0; j < col.size(); j++) {
						String tcol = col.get(j);
						pos = tablestr.indexOf(tcol);
						if (pos == -1) {
							notElColumns.add(ColumnToString(col.get(j), tname, false, sname));
							continue;
						}
						if (col.get(j).length() != tablestr.length()) {
							tablestr.delete(pos, tcol.length() + pos + 1);		
							TrimStringBuilder(tablestr);
						} else tablestr.delete(0, tablestr.length());
					}
					col = null;
					// Проверка ключей
					ArrayList<String> key = Keys(sname, tname);
					for (int j = 0; j < key.size(); j++) {
						String tkey = key.get(j);
						pos = tablestr.indexOf(tkey);
						if (pos == -1) {
							if (tkey.substring(0, 2).equals("P:")) {
								notElPKeys.add(PKeyToString(key.get(j), false, sname));
							} else {
								notElFKeys.add(FKeyToString(key.get(j), false, sname));
							}
							continue;
						}
						if (key.get(j).length() != tablestr.length()) {
							tablestr.delete(pos, tkey.length() + pos + 1);
							TrimStringBuilder(tablestr);
						} else tablestr.delete(0, tablestr.length());
					}
					key = null;
					// остаток файла
					//if (!tablestr.isEmpty()) {						
						while (tablestr.length() != 0) {
							String c = tablestr.substring(0, 2);
							//workstr = workstr.substring(2);
							int next = tablestr.indexOf(":", 3);
							String t = null;
							if (next != -1) {
								t = tablestr.substring(0, next-1).trim();
							} else {
								t = tablestr.toString();
							}
							//if (c.equals("I:"))	notEl.add(IndexToString(t, tname, true));
							if (c.equals("C:")) elColumns.add(ColumnToString(t, tname, true, sname));
							if (c.equals("P:")) elPKeys.add(PKeyToString(t, true, sname));
							if (c.equals("F:")) elFKeys.add(FKeyToString(t, true, sname));
							tablestr.delete(0, t.length());
							TrimStringBuilder(tablestr);
							//c = tablestr.substring(0, 2);
						}
					//}					
				}				
			}
			l.addMsg("Анализ окончен!");
			if (!(notElSchemes.isEmpty() && notElTables.isEmpty() && notElColumns.isEmpty() && notElFKeys.isEmpty() && notElPKeys.isEmpty())){
				l.addMsg("Формирую выходной файл...");
				char tab = '\u0009';
				String ls = System.getProperty("line.separator");
				BufferedWriter  out = new BufferedWriter(new FileWriter(foname));
				if (!notElSchemes.isEmpty() || !elSchemes.isEmpty()) {
					if (notElSchemes.size() == 1) out.write("Отсутствует схема:" + ls);
						else if (notElSchemes.size() > 1) out.write("Отсутствуют схемы:" + ls);
					for (String item : notElSchemes) {
						out.write(tab + item);
					}
					if (elSchemes.size() == 1) out.write("Лишняя схема:" + ls);
						else if (elSchemes.size() > 1) out.write("Лишние схемы:" + ls);
					for (String item : elSchemes) {
						out.write(tab + item);
					}
				}
				if (!notElTables.isEmpty() || !elTables.isEmpty()) {
					if (notElTables.size() == 1) out.write("Отсутствует таблица:" + ls);
						else if (notElTables.size() > 1) out.write("Отсутствуют таблицы:" + ls);
					for (String item : notElTables) {
						out.write(tab + item);
					}
					if (elTables.size() == 1) out.write("Лишняя таблица:" + ls);
						else if (elTables.size() > 1) out.write("Лишние таблицы:" + ls);
					for (String item : elTables) {
						out.write(tab + item);
					}
				}
				if (!notElColumns.isEmpty() || !elColumns.isEmpty()) {
					if (notElColumns.size() == 1) out.write("Отсутствует поле:" + ls);
						else if (notElColumns.size() > 1) out.write("Отсутсвуют поля:" + ls);
					for (String item : notElColumns) {
						out.write(tab + item);
					}
					if (elColumns.size() == 1) out.write("Лишнее поле:" + ls);
						else if (elColumns.size() > 1) out.write("Лишние поля:" + ls);
					for (String item : elColumns) {
						out.write(tab + item);
					}
				}
				if (!notElPKeys.isEmpty() || !elPKeys.isEmpty()) {
					if (notElPKeys.size() == 1) out.write("Отсутствует первичный ключ:" + ls);
						else if (notElPKeys.size() > 1) out.write("Отсутствуют первичные ключи:" + ls);
					for (String item : notElPKeys) {
						out.write(tab + item);
					}
					if (elPKeys.size() == 1) out.write("Лишний первичный ключ:" + ls);
						else if (elPKeys.size() > 1) out.write("Лишние первичные ключи:" + ls);
					for (String item : elPKeys) {
						out.write(tab + item);
					}
				}
				if (!notElFKeys.isEmpty() || !elFKeys.isEmpty()) {
					if (notElFKeys.size() == 1) out.write("Отсутствует внешний ключ:" + ls);
						else if (notElFKeys.size() > 1) out.write("Отсутствуют внешние ключи:" + ls);
					for (String item : notElFKeys) {
						out.write(tab + item);
					}
					if (elFKeys.size() == 1) out.write("Лишний внешний ключ:" + ls);
						else if (elFKeys.size() > 1) out.write("Лишние внешние ключи:" + ls);
					for (String item : elFKeys) {
						out.write(tab + item);
					}
				}
				out.flush();
				out.close();
				l.addMsg("Программа завершена! Созданный файл находится по адресу " + foname);
				return 1;
			} else {
				l.addMsg("Пользовательская база данных идентична эталонной!");
				return 0;
			}	
		} catch (SQLException e) {
			l.addMsg("Ошибка выпонения запросов к базы данных!");
			l.addMsg(e.getMessage());
		} catch (IOException e) {
			l.addMsg("Ошибка создания или записи файла!");
			l.addMsg(e.getMessage());
		} catch (StringIndexOutOfBoundsException e) {
			l.addMsg("Неизвестная ошибка!");
			l.addMsg(e.getMessage());
		}
		return -1;
	}
	
	private void TrimStringBuilder(StringBuilder str) {
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
	
	// Преобразование для вывода в файл индекса
	/*
	private String IndexToString (String ind, String tn, boolean mark) {
		ind = ind.substring(2);
		String[] a = ind.split(" ");
		String str = null;
		if (mark) {
			str = "Отсутствует индекс " + a[0];
		} else {
			str = "Найден лишний индекс " + a[0];
		}
		str += " для поля " + a[1];
		str += " в позиции " + a[2];
		Integer type = new Integer(a[3]);
		switch (type) {
			case 0: str += " с типом tableIndexStatistic ";	break;
			case 1: str += " с типом tableIndexClustered ";	break;
			case 2: str += " с типом tableIndexHashed "; break;
			case 3: str += " с типом tableIndexOther ";	break;
		}
		str += "в таблице " + tn + "." + System.getProperty("line.separator");
		return str;
	}
	*/
	// Преобразование для вывода в файл поля
	private String ColumnToString (String col, String tn, boolean mark, String sn) {
		col = col.substring(2);
		String a[] = col.split(" ");
		String str = sn + "." + tn + " " + a[0] + " " + a[1].toString() + "(" + a[2] + ")";
		/*if (mark) {
			str += " отсутствует поле " + a[0];
		} else {
			str += " найдено лишнее поле " + a[0];
		}
		str += " с типом " + a[1];
		str += " размером " + a[2] + " байт, ";
		*/
		str += System.getProperty("line.separator");
		return str;
	}
	// Преобразование информации о ключе для вывода в файл
	private String PKeyToString(String key, boolean mark, String sn) {
		String a[] = key.split(" ");
		String str = sn + "." + a[0] + a[1] + System.getProperty("line.separator");
		/*if (mark) {
			str += " отсутствует ";
		} else {
			str += " найден лишний ";
		}
		if (fp.equals("P:")) {
			str += "первичный ключ " + a[1] + ".";
		} else if (fp.equals("F:")) {
			str += "внешний ключ " + a[1];
			str += " к таблице " + a[2] + " (" + a[3] + ").";
		}*/
		return str;
	}
	
	private String FKeyToString(String key, boolean mark, String sn) {
		String a[] = key.split(" ");
		String str = sn + "." + a[0] + " " + a[1] + " - " + a[2] + "(" + a[3] + ")" + System.getProperty("line.separator");
		return str;
	}
	
	// Преобразование информации о таблице для вывода в файл
	private String TableToString(String tn, String sn, boolean mark) {
		String str = null;
		str = sn + "." + tn + System.getProperty("line.separator");
		/*if (mark) {
			str = "Отсутствует таблица " + tn;
		} else {
			str = "Найдена лишняя таблица " + tn;
		}*/
		return str;
	}
	
	// Инфа об индексах
	/*
	private ArrayList<String> Indexes (String sname, String tname) throws SQLException {
		String str = null;
		ArrayList<String> al = new ArrayList<String>();
		ResultSet dbindex = dbmeta.getIndexInfo(null, sname, tname, false, false);
		while (dbindex.next()) {			
			str = " I:" + dbindex.getString("INDEX_NAME");
			str += " " + dbindex.getString("COLUMN_NAME");
			str += " " + dbindex.getString("ORDINAL_POSITION");
			str += " " + dbindex.getString("TYPE");
			al.add(str.trim());
		}	
		return al;		
	}*/
	// Схемы
	private ArrayList<String> Schemes () throws SQLException {
		ArrayList<String> al = new ArrayList<String>();
		ResultSet schem = dbmeta.getSchemas();
		while (schem.next()) {
			al.add(schem.getString("TABLE_SCHEM"));
		}		
		return al;
	}
	
	//Инфа о таблицах
	private ArrayList<String> Tables (String sname, String tname) throws SQLException {
		String str = null;
		ArrayList<String> al = new ArrayList<String>();
		ResultSet col = dbmeta.getColumns(null, sname, tname, null);				
		while (col.next()) {
			str = " C:" + col.getString("COLUMN_NAME");
			str += " " + col.getString("TYPE_NAME");
			str += " " + col.getString("COLUMN_SIZE");
			str += " " + col.getString("NULLABLE");
			al.add(str.trim());
		}	
		return(al);
	}
	
	//Инфа о первичных и внешних ключах
	private ArrayList<String> Keys (String sname, String tname) throws SQLException {
		String str = null;
		ArrayList<String> al = new ArrayList<String>();
		ResultSet keys = dbmeta.getPrimaryKeys(null, sname, tname);
		while (keys.next()) {
			str = " P:" + keys.getString("TABLE_NAME") + " " + keys.getString("COLUMN_NAME");
			al.add(str.trim());
		}
		//Инфа о внешних ключах
		ResultSet fk = dbmeta.getImportedKeys(null, sname, tname);
		while (fk.next()) {
			str = " F:" + fk.getString("FKTABLE_NAME") + " " + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
			//text.append(" F:").append(fk.getString("FKTABLE_NAME") + " ").append(fk.getString("FKCOLUMN_NAME") + " ").append(fk.getString("PKTABLE_NAME") + " ").append(fk.getString("PKCOLUMN_NAME"));
			al.add(str.trim());
		}
		/*//Инфа о внешних ключах
		ResultSet fk = dbmeta.getCrossReference(null, null, null, null, sname, tname);
		while (fk.next()) {
			str = " F:" + fk.getString("FKTABLE_NAME") + " " + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
			al.add(str.trim());
		}*/
		return al;
	}
	
	
	public void close () {
		if (con!= null)	{
			try	{
				con.commit();
				con.close();
			} catch (Exception e) {
				l.addMsg("Проблема при закрытии подключения: ");
				l.addMsg(e.getMessage());
			}
			con = null;
		}
	}
}
