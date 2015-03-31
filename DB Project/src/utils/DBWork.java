/**
 * ����� �������� ������� ���� ������
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
	private DataBase db = null; //- ������ ���� ������
	private Connection con = null; //- ����������� � ���� ������
	private DatabaseMetaData dbmeta = null; //- ����-������ � ��
	private Logs l; //- ������ ����� ���������
	private boolean state = false; //- ���������� ��������� ����������� � ���� ������
	private String[] conDB = new String[4]; //- ������ ������ � ����������� � ��
	
	/*
	 * ����������� ������, �������� � ���� ������ ����� ���������
	 */
	public DBWork(Logs temp) {
		l = temp;
	}
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
		if (state) {
			//- ���� ����������� ����������� �������, �������� �������������� ���� ������
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
	 * �������� ������ ������ ����� ���� ������
	 * ���������, �� ��������� �� ��� ������� �� �������� "SYS"
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
	 * ����� ��������� ���� ���� ������
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
		boolean isDriverRegistred = false;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
	        l.addMsg("������� DB2 �������� �������!");
	        isDriverRegistred = true;
		} catch (ClassNotFoundException e) {
			l.addMsg("������ �������� �������� DB2!");
			l.addMsg(e.getMessage().toString());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + conDB[0] + "/" + conDB[1];
				con = DriverManager.getConnection(strcon, conDB[2], conDB[3]);				
				l.addMsg("����������� � �� ����������� �������!");
				dbmeta = con.getMetaData();
				state = true;
			} catch (SQLException e) {
				l.addMsg("�� ������� ������������ � ��!");
				l.addMsg(e.getMessage().toString());
			}
		}		
	}
	/*
	 * ����� ������������ ����������� � ���� ������.
	 * ��������� ������ ����� �� ����������:
	 * [0] - ����
	 * [1] - ��� ���� ������
	 * [2] - ������������
	 * [3] - ������
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
				l.addMsg("�������� � ��������� ����������� � DB2:");
				l.addMsg(e.getStackTrace().toString());
			}
			con = null;
		}
	}
}
