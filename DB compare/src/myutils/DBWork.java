/**
 * ����� �������� ������� ���� ������
 */
package myutils;

import MVC.DBCompareController;

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
	private DataBase db = null; //- ������ ���� ������
	private Connection con = null; //- ����������� � ���� ������
	private DatabaseMetaData dbmeta = null; //- ����-������ � ��
	//private Logs l; //- ������ ����� ���������
	private boolean state = false; //- ���������� ��������� ����������� � ���� ������
	private String[] conDB = new String[4]; //- ������ ������ � ����������� � ��
	
	/*
	 * ����������� ������, �������� � ���� ������ ����� ���������
	 
	public DBWork(Logs log) {
		l = log;
	}*/
	/*
	 * �������� ����� - �������� ������� ���� ������
	 * ��������� ������ �� ��������� ����������:
	 * [0] - ����
	 * [1] - ��� ���� ������
	 * [2] - ������������
	 * [3] - ������
	 */
	public DataBase createObjDB(String[] dbInfo) {		
		ArrayList<Scheme> scheme = new ArrayList<>();
		ArrayList<Table> table = new ArrayList<>();
		ArrayList<Column> column = new ArrayList<>();
		ArrayList<Keys> keys = new ArrayList<>();	
		
		conDB = dbInfo;
		
		ConnectionToDB();
		DBCompareController.AddLogMessage("���������� ���� ������...");
		if (state) {
			//- ���� ����������� ����������� �������, �������� �������������� ���� ������
			try {
				dbmeta = con.getMetaData();
				ArrayList<String> schem = Schemes();
				int i = 1;
				for (String s : schem) {
					DBCompareController.AddLogMessage("����� " + i + "/" + schem.size() + " [" + s + "]");
					ArrayList<String> tab = Tables(s);
					table = new ArrayList<>();
					for (String t : tab) {
						column = Columns(s, t);
						keys = Keys(s, t);
						table.add(new Table(t, s, column, keys));
					}
					scheme.add(new Scheme(s, table));
					table = null;
					i++;
				}
				db = new DataBase(scheme);
				scheme.clear();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}		
		DBCompareController.AddLogMessage("������ �������!");
		return db;
	}
	/*
	 * �������� ������ ������ ����� ���� ������
	 * ���������, �� ��������� �� ��� ������� �� �������� "SYS"
	 */
	private ArrayList<String> Tables(String s) throws SQLException {
		ArrayList<String> str = new ArrayList<>();
		ResultSet rs = dbmeta.getTables(null, s, "%", null);
		while (rs.next()) {
			String tab = new String(rs.getString(3));
			if (!(tab.indexOf("SYS") == 0)) {
				str.add(tab);
			}
		}
		return str;
	}
	/*
	 * ����� ��������� ���� ���� ������
	 */
	private ArrayList<String> Schemes() throws SQLException {
		ArrayList<String> str = new ArrayList<>();
		ResultSet schem = dbmeta.getSchemas();
		while (schem.next()) {
			String s = new String(schem.getString("TABLE_SCHEM"));
			if (!(s.indexOf("SYS") == 0)) {
				str.add(s);			
			}
		}
		return str;
	}
	/*
	 * �������� ������ ������� �������
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
	 * �������� ������ �������� - ������ �������
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
	 * ����������� � ��
	 */
	private void ConnectionToDB() {
		DBCompareController.AddLogMessage("������������ ����� � ����� ������!");
		boolean isDriverRegistred = false;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
			DBCompareController.AddLogMessage("������� DB2 �������� �������!");
	        isDriverRegistred = true;
		} catch (ClassNotFoundException e) {
			DBCompareController.AddLogMessage("������ �������� �������� DB2!");
			DBCompareController.AddLogMessage(e.getMessage().toString());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + conDB[0] + "/" + conDB[1];
				con = DriverManager.getConnection(strcon, conDB[2], conDB[3]);				
				DBCompareController.AddLogMessage("����������� � �� ����������� �������!");
				dbmeta = con.getMetaData();
				state = true;
			} catch (SQLException e) {
				DBCompareController.AddLogMessage("�� ������� ������������ � ��!");
				DBCompareController.AddLogMessage(e.getMessage().toString());
			}
		}		
	}
	/*
	 * ����� ��������� ��������� ����������� � ���� ������
	 */
	public boolean getState() {
		return state;
	}
	/*
	 * �������� �����������
	 */
	public void close () {
		if (con!= null)	{
			try	{
				con.commit();
				con.close();
			} catch (Exception e) {
				DBCompareController.AddLogMessage("�������� � ��������� ����������� � DB2:");
				DBCompareController.AddLogMessage(e.getStackTrace().toString());
			}
			con = null;
		}
	}
}
