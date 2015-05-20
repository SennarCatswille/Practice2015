package MVC;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import outputInfo.OutInfo;
import myutils.DBWork;
import myutils.FileWorkReadDB;
import myutils.FileWorkWriteDB;
import myutils.Filework;
import myutils.ReadConfigFile;
import objDB.DataBase;


/**
 * @author Sennar
 *
 */
public class DBCompareModel {
	private String dirPath;
	private String filePath;
	private OutInfo oi = null;
	private ReadConfigFile readConfigFile = new ReadConfigFile();
	
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
	
	public void CompareDB(String[] dbInfo) {
		DBWork db = new DBWork();
		DBCompareController.AddLogMessage("Создаю объект базы данных...");
		DataBase db2 = db.createObjDB(dbInfo);
		DBCompareController.AddLogMessage("Считываю информацию о базе данных из файла...");
		if (!(new File(filePath).exists())) {
			DBCompareController.AddLogMessage("Файл недоступен или не существует!");
			return;
		}
		DataBase db1 = FileWorkReadDB.readDB(filePath);
		DBCompareController.AddLogMessage("Успешно! Начинаю сравнение...");
		if (db.getState()) {
			dirPath += "\\out.txt";
			db1.equals(db2);
			db.close();	
			oi = new OutInfo(db1.getEE(), db1.getNE(), db1.getPE());
			int flag = oi.getFile(dirPath);
			String str = null;
			switch (flag) {
				case -1:
					str = new String("Сравнение завершено! Базы данных идентичны!"); break;
				case 0:
					str = new String("Сравнение успешно завершено! Обнаружены различия. Они находятся в файле " + dirPath); break;
				case 1:
					str = new String("Ошибка открытия файла для записи!"); break;
				case 2:
					str = new String("Ошибка записи данных в файл!"); break;
				case 3:
					str = new String("Ошибка сохранения файла на диске!"); break;
				case 4:
					str = new String("Ошибка закрытия потока вывода!"); break;
			}						
			DBCompareController.AddLogMessage(str);
		}	
	}
	
	public String getLogsDirPath() {
		return readConfigFile.getLogsDirPath().getPath();
	}
	
	public void directoryToZip(File zipFile) throws IOException {
        File directory = readConfigFile.getLogsDirPath();
		URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipFile);
        Closeable res = out;
      
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File child : directory.listFiles()) {
                    String name = base.relativize(child.toURI()).getPath();
                    if (child.isDirectory()) {
                        queue.push(child);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
 
 
                        InputStream in = new FileInputStream(child);
                        try {
                            byte[] buffer = new byte[1024];
                            while (true) {
                                int readCount = in.read(buffer);
                                if (readCount < 0) {
                                    break;
                                }
                                zout.write(buffer, 0, readCount);
                            }
                        } finally {
                            in.close();
                        }
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }
}

