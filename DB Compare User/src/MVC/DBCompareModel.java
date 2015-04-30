package MVC;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import myutils.DBWork;
import myutils.FileWorkWriteDB;
import myutils.Filework;
import objDB.DataBase;


/**
 * @author Sennar
 *
 */
public class DBCompareModel {
	private String dirPath;
	private String filePath;
	/*
	 * Метод тестирования подключения к базе данных.
	 */
	public boolean CheckDBConnection(String dbHost, String dbName, String dbUser, String dbPass) {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
			String strcon = "jdbc:db2://" + dbHost + "/" + dbName;
			Connection con = DriverManager.getConnection(strcon, dbUser, dbPass);
			con.close();
			return true;
		} catch (ClassNotFoundException e) {
			DBCompareController.AddLogMessage("Не найден класс-драйвер базы данных! Текст ошибки:");
			DBCompareController.AddLogMessage(e.getMessage());
			return false;
		} catch (SQLException e) {
			DBCompareController.AddLogMessage("Ошибка подключения к базе данных! Текст ошибки:");
			DBCompareController.AddLogMessage(e.getMessage());
			return false;
		}
	}
	
	public String[] checkDB(String dbHost, String dbName, String dbUser, String dbPass) {
		String[] dbauth = new String[4];
		dbauth[0] = new String(dbHost);
		dbauth[1] = new String(dbName);
		dbauth[2] = new String(dbUser);
		dbauth[3] = new String(dbPass);
		if (dbauth[0].isEmpty()) {
			JOptionPane.showMessageDialog(null, "Заполните поле расположения БД!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[1].isEmpty()) {
			JOptionPane.showMessageDialog(null, "Заполните поле названия БД!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[2].isEmpty()) {
			JOptionPane.showMessageDialog(null, "Заполните поле пользователя БД!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[3].isEmpty()) {
			JOptionPane.showMessageDialog(null, "Заполните поле пароля пользователя БД!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return dbauth;
	}
	
	public void setDirPath(String dp) {
		dirPath = dp;
	}
	
	public void setFilePath(String fp) {
		filePath = fp;
	}
	
	public void CreateDBMetaFile(String[] dbInfo) {
		DBWork db = new DBWork();
		DBCompareController.AddLogMessage("Анализирую базу данных...");
		DataBase db1 = db.createObjDB(dbInfo);
		if (db.getState()) {
			dirPath += "\\out.dat";
			DBCompareController.AddLogMessage("Сохраняю файл на диск");
			FileWorkWriteDB fileDB = new FileWorkWriteDB(db1);
			StringBuilder DBString = fileDB.CreateFileDB();
			if (Filework.write(dirPath, DBString)) {
				db.close();
				String str = "Программа успешно завершена! Созданный файл находится в " + dirPath;
				DBCompareController.AddLogMessage(str);
			}
		}
	}
}

