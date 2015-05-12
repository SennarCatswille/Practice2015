package MVC;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import outputInfo.OutInfo;
import myutils.DBWork;
import myutils.FileWorkReadDB;
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
	private OutInfo oi = null;
	/*
	 * ����� ������������ ����������� � ���� ������.
	 */
	public boolean CheckDBConnection(String dbHost, String dbName, String dbUser, String dbPass) {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
			String strcon = "jdbc:db2://" + dbHost + "/" + dbName;
			Connection con = DriverManager.getConnection(strcon, dbUser, dbPass);
			con.close();
			return true;
		} catch (ClassNotFoundException e) {
			DBCompareController.AddLogMessage("�� ������ �����-������� ���� ������! ����� ������:");
			DBCompareController.AddLogMessage(e.getMessage());
			return false;
		} catch (SQLException e) {
			DBCompareController.AddLogMessage("������ ����������� � ���� ������! ����� ������:");
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
			JOptionPane.showMessageDialog(null, "��������� ���� ������������ ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[1].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� �������� ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[2].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� ������������ ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[3].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� ������ ������������ ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
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
			DBCompareController.AddLogMessage("�������� ���� �� ����");
			FileWorkWriteDB fileDB = new FileWorkWriteDB(db1);
			StringBuilder DBString = fileDB.CreateFileDB();
			if (Filework.write(dirPath, DBString)) {
				db.close();
				String str = "��������� ������� ���������! ��������� ���� ��������� � " + dirPath;
				DBCompareController.AddLogMessage(str);
			}
		}
	}
	
	public void CompareDB(String[] dbInfo) {
		DBWork db = new DBWork();
		DBCompareController.AddLogMessage("������ ������ ���� ������...");
		DataBase db2 = db.createObjDB(dbInfo);
		DBCompareController.AddLogMessage("�������� ���������� � ���� ������ �� �����...");
		if (!(new File(filePath).exists())) {
			DBCompareController.AddLogMessage("���� ���������� ��� �� ����������!");
			return;
		}
		DataBase db1 = FileWorkReadDB.readDB(filePath);
		DBCompareController.AddLogMessage("�������! ������� ���������...");
		if (db.getState()) {
			dirPath += "\\out.txt";
			//int flag = db.analysis(dirpath);
			db1.equals(db2);
			db.close();	
			oi = new OutInfo(db1.getEE(), db1.getNE(), db1.getPE());
			int flag = oi.getFile(dirPath);
			String str = null;
			switch (flag) {
				case -1:
					str = new String("��������� ���������! ���� ������ ���������!"); break;
				case 0:
					str = new String("��������� ������� ���������! ���������� ��������. ��� ��������� � ����� " + dirPath); break;
				case 1:
					str = new String("������ �������� ����� ��� ������!"); break;
				case 2:
					str = new String("������ ������ ������ � ����!"); break;
				case 3:
					str = new String("������ ���������� ����� �� �����!"); break;
				case 4:
					str = new String("������ �������� ������ ������!"); break;
			}						
			DBCompareController.AddLogMessage(str);
		}	
	}
}

