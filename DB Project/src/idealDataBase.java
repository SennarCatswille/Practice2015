import java.sql.*;
import java.util.ArrayList;
import java.io.*;
/**
 * @author Sennar
 *
 */
public class idealDataBase {
	private Connection con = null;
	private DatabaseMetaData dbmeta = null;
	private String text = "";
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
	
	idealDataBase (String name, String user, String pass) {
		boolean isDriverRegistred = false;
		try {
			Driver driver = new COM.ibm.db2.jdbc.app.DB2Driver();
	        DriverManager.registerDriver(driver);
	        System.out.println("Драйвер DB2 загружен успешно!");
	        isDriverRegistred = true;
		} catch (SQLException e) {
			System.out.println("Ошибка загрузки драйвера DB2!");
			e.printStackTrace();
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2:" + name;
				con = DriverManager.getConnection(strcon, user, pass);					
				System.out.println("Подключение к БД установлено успешно!");
				dbmeta = con.getMetaData();
			} catch (SQLException e) {
				System.out.println("Не удалось подключиться к БД!");
				e.printStackTrace();
			}
		}
	}
	
	//Получить мета-инфу
	public void getMeta (String fname) {		
		try {	
			dbmeta = con.getMetaData();
			//Достаем названия таблиц
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				String sname = ss.get(i);
				ResultSet rs = dbmeta.getTables(null, sname, "%", null);
				if (i > 0) text += "| " + sname; else text += sname;
				while (rs.next()) {
					String tname = rs.getString(3);
					try {						
						text += " T:" + tname;
						Indexes(sname, tname);
						Tables(sname, tname);
						Keys(sname, tname);	
					} catch (IOException e) {
						System.out.println("Ошибка записи в файл!");
						e.printStackTrace();
					}
				}
			}	
			text += "|";
			Filework.write(fname, text);
		} catch (SQLException e) {
			System.out.println("Ошибка выполнения запросов к БД!");
			e.printStackTrace();
		}		
	}
	
	// Инфа об индексах
	private void Indexes (String sname, String tname) throws SQLException, IOException {
		ResultSet dbindex = dbmeta.getIndexInfo(null, sname, tname, false, false);
		while (dbindex.next()) {			
			text += " I:" + dbindex.getString("INDEX_NAME");
			text += " " + dbindex.getString("COLUMN_NAME");
			text += " " + dbindex.getString("ORDINAL_POSITION");
			text += " " + dbindex.getString("TYPE");
		}	
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
	private void Tables (String sname, String tname) throws SQLException, IOException {
		ResultSet col = dbmeta.getColumns(null, sname, tname, null);				
		while (col.next()) {
			text += " C:" + col.getString("COLUMN_NAME");
			text += " " + col.getString("TYPE_NAME");
			text += " " + col.getString("COLUMN_SIZE");
			text += " " + col.getString("NULLABLE");
		}	
	}
	
	//Инфа о первичных и внешних ключах
	private void Keys (String sname, String tname) throws SQLException, IOException {
		ResultSet keys = dbmeta.getPrimaryKeys(null, sname, tname);
		while (keys.next()) {
			text += " P:" + keys.getString("COLUMN_NAME");
		}
		//Инфа о внешних ключах
		ResultSet fk = dbmeta.getCrossReference(null, null, null, null, sname, tname);
		while (fk.next()) {
			text += " F:" + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
		}
	}
	
	
	public void close () {
		if (con!= null)	{
			try	{
				con.commit();
				con.close();
			} catch (Exception e) {
				System.out.println("Problem in closing DB2 connection: " + e.getMessage());
			}
			con = null;
		}
	}
}
