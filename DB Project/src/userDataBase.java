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
	private String text = "";
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
			Driver driver = new COM.ibm.db2.jdbc.app.DB2Driver();
	        DriverManager.registerDriver(driver);
	        l.addMsg("Драйвер DB2 загружен успешно!");
	        isDriverRegistred = true;
		} catch (SQLException e) {
			l.addMsg("Ошибка загрузки драйвера DB2!");
			l.addMsg(e.getMessage());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + host + "/" + name;
				con = DriverManager.getConnection(strcon, user, pass);
				l.addMsg("Подключение к БД установлено успешно!");
				dbmeta = con.getMetaData();
			} catch (SQLException e) {
				l.addMsg("Не удалось подключиться к БД!");
				l.addMsg(e.getMessage());
			}
		}	
		text = newtext;
	}
	
	public int analysis (String foname) {
		String workstr = null;
		String tablestr = null;
		ArrayList<String> notEl= new ArrayList<String>();
		int pos = 0;
		l.addMsg("Начинаю анализ базы данных...");
		try {
			//почитать про StringBuilder
			//основной стрингбилдер - файл
			//
			dbmeta = con.getMetaData();
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				pos = 0;
				String sname = ss.get(i);
				pos = text.indexOf(sname);
				if (pos == -1) {
					notEl.add("Отсутствует схема " + sname + System.getProperty("line.separator"));
					continue;
				}					
				l.addMsg("Анализирую схему " + Integer.toString(i+1) + "/" + Integer.toString(ss.size()) + " [" + sname + "]");
				//temp = text.substring(0, pos);
				//pos += sname.length();
				int palka = text.indexOf("|", pos+1);
				workstr = text.substring(pos + sname.length(), palka).trim();
				//workstr.trim();
				//int ololo = workstr.length()+1;
				text = (text.substring(0, pos) + text.substring(pos + 2 + workstr.length() + sname.length())).trim();		
				// Получаем таблицы
				ResultSet rs = dbmeta.getTables(null, sname, "%", null);
				while (rs.next()) {
					String tname = rs.getString(3);
					pos = workstr.indexOf("T:" + tname);
					if (pos == -1) {
						notEl.add(TableToString(tname, sname, false));
						continue;
					}
					int wnext = workstr.indexOf("T:", pos+2);
					if (wnext == -1) {
						tablestr = workstr.substring(pos + tname.length() + 2).trim();
						workstr = workstr.substring(0, pos).trim();
					} else {
						tablestr = workstr.substring(pos + tname.length() + 2, wnext).trim();
						workstr = (workstr.substring(0, pos) + workstr.substring(wnext)).trim();
					}
					//workstr = workstr.substring(tname.length()+2).trim();
					// Проверка индексов
					ArrayList<String> ind = Indexes(sname, tname);
					for (int j = 0; j < ind.size(); j++) {
						//tempstr = text.substring(0, text.indexOf(":", 3)-2);
						pos = tablestr.indexOf(ind.get(j));
						if (pos == -1) {
							notEl.add(IndexToString(ind.get(j), tname, false));
							continue;
						}
						if (ind.get(j).length() != tablestr.length()) {
							tablestr = (tablestr.substring(0, pos) + tablestr.substring(pos + ind.get(j).length())).trim();
						} else tablestr = "";
					}
					ind = null;
					// Проверка полей таблиц
					ArrayList<String> col = Tables(sname, tname);
					for (int j = 0; j < col.size(); j++) {
						pos = tablestr.indexOf(col.get(j));
						if (pos == -1) {
							notEl.add(ColumnToString(col.get(j), tname, false));
							continue;
						}
						if (col.get(j).length() != tablestr.length()) {
							tablestr = (tablestr.substring(0, pos) + tablestr.substring(pos + col.get(j).length())).trim();		
						} else tablestr = "";
					}
					col = null;
					// Проверка ключей
					ArrayList<String> key = Keys(sname, tname);
					for (int j = 0; j < key.size(); j++) {
						pos = tablestr.indexOf(key.get(j));
						if (pos == -1) {
							notEl.add(KeyToString(key.get(j), false));
							continue;
						}
						if (key.get(j).length() != tablestr.length()) {
							tablestr = (tablestr.substring(0, pos) + tablestr.substring(pos + key.get(j).length())).trim();
						} else tablestr = "";
					}
					key = null;
					// остаток файла
					//if (!tablestr.isEmpty()) {
						
						while (!tablestr.isEmpty()) {
							String c = tablestr.substring(0, 2);
							//workstr = workstr.substring(2);
							int next = tablestr.indexOf(":", 3);
							String t = null;
							if (next != -1) {
								t = tablestr.substring(0, next-1).trim();
							} else {
								t = tablestr;
							}
							if (c.equals("I:"))	notEl.add(IndexToString(t, tname, true));
							if (c.equals("C:")) notEl.add(ColumnToString(t, tname, true));
							if (c.equals("P:")) notEl.add(KeyToString(t, true));
							if (c.equals("F:")) notEl.add(KeyToString(t, true));
							tablestr = tablestr.substring(t.length()).trim();
							//c = tablestr.substring(0, 2);
						}
					//}					
				}				
			}
			if (!notEl.isEmpty()){
				BufferedWriter  out = new BufferedWriter(new FileWriter(foname));
				for (int i = 0; i < notEl.size(); i++) {
					out.write(notEl.get(i));
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
	
	// Преобразование для вывода в файл индекса
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
	
	// Преобразование для вывода в файл поля
	private String ColumnToString (String col, String tn, boolean mark) {
		col = col.substring(2);
		String a[] = col.split(" ");
		String str = "В таблице " + tn;
		if (mark) {
			str += " отсутствует поле " + a[0];
		} else {
			str += " найдено лишнее поле " + a[0];
		}
		str += " с типом " + a[1];
		str += " размером " + a[2] + " байт, ";
		if (a[3].equals("0")) str += "не допустимы пустые значения.";
		if (a[3].equals("1")) str += "пустые значения допустимы.";
		
		str += System.getProperty("line.separator");
		return str;
	}
	// Преобразование информации о ключе для вывода в файл
	private String KeyToString(String key, boolean mark) {
		String fp = key.substring(0, 2);
		key = key.substring(2);
		String a[] = key.split(" ");
		String str = "В таблице " + a[0];
		if (mark) {
			str += " отсутствует ";
		} else {
			str += " найден лишний ";
		}
		if (fp.equals("P:")) {
			str += "первичный ключ " + a[1] + ".";
		} else if (fp.equals("F:")) {
			str += "внешний ключ " + a[1];
			str += " к таблице " + a[2] + " (" + a[3] + ").";
		}
		str += System.getProperty("line.separator");
		return str;
	}
	
	// Преобразование информации о таблице для вывода в файл
	private String TableToString(String tn, String sn, boolean mark) {
		String str = null;
		if (mark) {
			str = "Отсутствует таблица " + tn;
		} else {
			str = "Найдена лишняя таблица " + tn;
		}
		str += " в схеме " + sn + "." + System.getProperty("line.separator");
		return str;
	}
		
	// Инфа об индексах
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
	}
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
		ResultSet fk = dbmeta.getCrossReference(null, null, null, null, sname, tname);
		while (fk.next()) {
			str = " F:" + fk.getString("FKTABLE_NAME") + " " + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
			al.add(str.trim());
		}
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
