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
	Logs l = null;
/*
 * ����������� � PostgreSQL
 * 
	DataBase (String host, String port, String name, String user, String pass) {
		try {
			Class.forName("org.postgresql.Driver");
			String strcon = "jdbc:postgresql://" + host + ":" + port + "/" + name;
			con = DriverManager.getConnection(strcon, user, pass);
		} catch (ClassNotFoundException e) {
		    System.out.println("�� ������ ������� ����!");
		    e.printStackTrace();
		    return;
		} catch (SQLException e) {
			System.out.println("�� ������� ������������ � ��!");
			e.printStackTrace();
		}
	}
*/
	
	idealDataBase (Logs g, String host, String name, String user, String pass) {
		boolean isDriverRegistred = false;
		l = g;
		try {
			Driver driver = new COM.ibm.db2.jdbc.app.DB2Driver();
	        DriverManager.registerDriver(driver);
	        
	        l.addMsg("������� DB2 �������� �������!");
	        isDriverRegistred = true;
		} catch (SQLException e) {
			l.addMsg("������ �������� �������� DB2!");
			l.addMsg(e.getMessage());
		}
		if (isDriverRegistred) {
			try {
				//String strcon = "jdbc:db2://" + host + "/" + name;
				String strcon = "jdbc:db2:" + name;
				con = DriverManager.getConnection(strcon, user, pass);				
				l.addMsg("����������� � �� ����������� �������!");
				dbmeta = con.getMetaData();
			} catch (SQLException e) {
				l.addMsg("�� ������� ������������ � ��!");
				l.addMsg(e.getMessage());
			}
		}
	}
	
	//�������� ����-����
	public void getMeta (String fname) {		
		try {	
			dbmeta = con.getMetaData();
			//������� �������� ������
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				String sname = ss.get(i);
				l.addMsg("���������� ����� " + Integer.toString(i+1) + "/" + Integer.toString(ss.size()) + " [" + sname + "]");
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
						l.addMsg("������ ������ � ����!");
						l.addMsg(e.getMessage());
					}
				}
			}	
			text += "|";
			Filework.write(fname, text);
		} catch (SQLException e) {
			l.addMsg("������ ���������� �������� � ��!");
			l.addMsg(e.getMessage());
		}		
	}
	
	// ���� �� ��������
	private void Indexes (String sname, String tname) throws SQLException, IOException {
		ResultSet dbindex = dbmeta.getIndexInfo(null, sname, tname, false, false);
		while (dbindex.next()) {			
			text += " I:" + dbindex.getString("INDEX_NAME");
			text += " " + dbindex.getString("COLUMN_NAME");
			text += " " + dbindex.getString("ORDINAL_POSITION");
			text += " " + dbindex.getString("TYPE");
		}	
	}
	// �����
	private ArrayList<String> Schemes () throws SQLException {
		ArrayList<String> al = new ArrayList<String>();
		ResultSet schem = dbmeta.getSchemas();
		while (schem.next()) {
			al.add(schem.getString("TABLE_SCHEM"));
		}		
		return al;
	}
	
	//���� � ��������
	private void Tables (String sname, String tname) throws SQLException, IOException {
		ResultSet col = dbmeta.getColumns(null, sname, tname, null);				
		while (col.next()) {
			text += " C:" + col.getString("COLUMN_NAME");
			text += " " + col.getString("TYPE_NAME");
			text += " " + col.getString("COLUMN_SIZE");
			text += " " + col.getString("NULLABLE");
		}	
	}
	
	//���� � ��������� � ������� ������
	private void Keys (String sname, String tname) throws SQLException, IOException {
		ResultSet keys = dbmeta.getPrimaryKeys(null, sname, tname);
		while (keys.next()) {
			text += " P:" + keys.getString("TABLE_NAME") + " " + keys.getString("COLUMN_NAME");
		}
		//���� � ������� ������
		ResultSet fk = dbmeta.getCrossReference(null, null, null, null, sname, tname);
		while (fk.next()) {
			text += " F:" + fk.getString("FKTABLE_NAME") + " " + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
		}
	}
	
	
	public void close () {
		if (con!= null)	{
			try	{
				con.commit();
				con.close();
			} catch (Exception e) {
				l.addMsg("�������� � ��������� ����������� � DB2:");
				l.addMsg(e.getMessage());
			}
			con = null;
		}
	}
}
