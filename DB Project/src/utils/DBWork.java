/**
 * Класс создания объекта базы данных
 */
package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import objDB.Column;
import objDB.DataBase;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * @author Sennar
 *
 */
public class DBWork {
	private DataBase db = null; //- объект базы данных
	private Connection con = null; //- подключение к базе данных
	private DatabaseMetaData dbmeta = null; //- мета-данные о БД
	private Logs l; //- объект логов программы
	private boolean state = false; //- определяет состояние подключения к базе данных
	private String[] conDB = new String[4]; //- массив данных о подключении к бд
	
	/*
	 * Конструктор класса, передаем в него объект логов программы
	 */
	public DBWork(Logs temp) {
		l = temp;
	}
	/*
	 * Основной метод - создание объекта базы данных
	 * Принимает массив со следующей структурой:
	 * [0] - хост
	 * [1] - имя базы данных
	 * [2] - пользователь
	 * [3] - пароль
	 */
	public DataBase createObjDB(String[] dbInfo) {
		ArrayList<Scheme> scheme = new ArrayList<>();
		ArrayList<Table> table = new ArrayList<>();
		ArrayList<Column> column = new ArrayList<>();
		ArrayList<Keys> keys = new ArrayList<>();	
		
		conDB = dbInfo;
		
		ConnectionToDB();
		if (state) {
			//- Если подключение установлено успешно, начинаем преобразование базы данных
			try {
				dbmeta = con.getMetaData();
				ArrayList<String> schem = Schemes();
				for (String s : schem) {
					ArrayList<String> tab = Tables(s);
					for (String t : tab) {
						column = Columns(s, t);
						keys = Keys(s, t);
						table.add(new Table(t, s, column, keys));
						column = null;
						keys = null;
					}
					scheme.add(new Scheme(s, table));
					table = null;
				}
				db = new DataBase(scheme);
				scheme = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}		
		
		return db;
	}
	/*
	 * Получаем список таблиц схемы базы данных
	 * Проверяем, не системные ли это таблицы по префиксу "SYS"
	 */
	private ArrayList<String> Tables(String s) throws SQLException {
		ArrayList<String> str = new ArrayList<>();
		ResultSet rs = dbmeta.getTables(null, s, "%", null);
		while (rs.next()) {
			if (!(rs.getString(3).equals("SYS"))) {
				str.add(rs.getString(3));
			}
		}
		return str;
	}
	/*
	 * Метод получения схем базы данных
	 */
	private ArrayList<String> Schemes() throws SQLException {
		ArrayList<String> str = new ArrayList<>();
		ResultSet schem = dbmeta.getSchemas();
		while (schem.next()) {
			str.add(schem.getString("TABLE_SCHEM"));
		}		
		return str;
	}
	/*
	 * Получаем массив колонок таблицы
	 */
	private ArrayList<Column> Columns(String sname, String tname) throws SQLException {
		ArrayList<Column> cols = new ArrayList<>();
		ResultSet rs = dbmeta.getColumns(null, sname, tname, null);
		while (rs.next()) {
			cols.add(new Column(rs.getString("COLUMN_NAME"),
					rs.getString("TYPE_NAME"),
					new Integer(rs.getString("COLUMN_SIZE")),
					sname + "." + tname));
		}
		return cols;
	}
	/*
	 * Получаем массив объектов - ключей таблицы
	 */
	private ArrayList<Keys> Keys(String sname, String tname) throws SQLException {
		ArrayList<Keys> k = new ArrayList<>();
		ResultSet rs = dbmeta.getPrimaryKeys(null, sname, tname);
		while (rs.next()) {
			k.add(new Keys(1, 
					rs.getString("TABLE_NAME"),
					rs.getString("COLUMN_NAME"),
					sname));
		}
		rs = dbmeta.getImportedKeys(null, sname, tname);
		while (rs.next()) {
			k.add(new Keys(2,
					rs.getString("PKCOLUMN_NAME"),
					rs.getString("PKTABLE_NAME"),
					rs.getString("FKCOLUMN_NAME"),
					rs.getString("FKTABLE_NAME"),
					sname));
		}
		return k;
	}
	/*
	 * Подключение к БД
	 */
	private void ConnectionToDB() {
		boolean isDriverRegistred = false;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
	        l.addMsg("Драйвер DB2 загружен успешно!");
	        isDriverRegistred = true;
		} catch (ClassNotFoundException e) {
			l.addMsg("Ошибка загрузки драйвера DB2!");
			l.addMsg(e.getMessage().toString());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + conDB[0] + "/" + conDB[1];
				con = DriverManager.getConnection(strcon, conDB[2], conDB[3]);				
				l.addMsg("Подключение к БД установлено успешно!");
				dbmeta = con.getMetaData();
				state = true;
			} catch (SQLException e) {
				l.addMsg("Не удалось подключиться к БД!");
				l.addMsg(e.getMessage().toString());
			}
		}		
	}
	/*
	 * Метод тестирования подключения к базе данных.
	 * Принимает массив строк со структурой:
	 * [0] - хост
	 * [1] - имя базы данных
	 * [2] - пользователь
	 * [3] - пароль
	 */
	public static boolean TestDB(String[] dbInfo) {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
			String strcon = "jdbc:db2://" + dbInfo[0] + "/" + dbInfo[1];
			Connection con = DriverManager.getConnection(strcon, dbInfo[2], dbInfo[3]);
			con.close();
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (SQLException e) {
			return false;
		}
	}
	/*
	 * Метод получения состояния подключения к базе данных
	 */
	public boolean getState() {
		return state;
	}
	/*
	 * Закрытие подключения
	 */
	public void close () {
		if (con!= null)	{
			try	{
				con.commit();
				con.close();
			} catch (Exception e) {
				l.addMsg("Проблема с закрытием подключения к DB2:");
				l.addMsg(e.getStackTrace().toString());
			}
			con = null;
		}
	}
}
