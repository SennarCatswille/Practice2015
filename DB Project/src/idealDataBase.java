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
	private StringBuilder text = new StringBuilder();
	private boolean flag = false;
	private ArrayList<String> tables = new ArrayList<>();
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
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
	        l.addMsg("������� DB2 �������� �������!");
	        isDriverRegistred = true;
		} catch (ClassNotFoundException e) {
			l.addMsg("������ �������� �������� DB2!");
			l.addMsg(e.getMessage().toString());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + host + "/" + name;
				con = DriverManager.getConnection(strcon, user, pass);				
				l.addMsg("����������� � �� ����������� �������!");
				dbmeta = con.getMetaData();
				flag = true;
			} catch (SQLException e) {
				l.addMsg("�� ������� ������������ � ��!");
				l.addMsg(e.getMessage().toString());
			}
		}		
	}
	
	public boolean GetState() {
		return flag;
	}
	
	//�������� ����-����
	public boolean getMeta (String fname) {		
		try {	
			dbmeta = con.getMetaData();
			//������� �������� ������
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				String sname = ss.get(i);
				l.addMsg("���������� ����� " + Integer.toString(i+1) + "/" + Integer.toString(ss.size()) + " [" + sname + "]");
				ResultSet rs = dbmeta.getTables(null, sname, "%", null);
				while (rs.next()) {
					tables.add(rs.getString(3));
				}
				if (i > 0) text.append("| ").append(sname); else text.append(sname);
				for (String tname : tables) {
					//String tname = rs.getString(3);
					try {						
						text.append(" T:").append(tname);
						//Indexes(sname, tname);
						Tables(sname, tname);
						Keys(sname, tname);	
					} catch (IOException e) {
						l.addMsg("������ ������ � ����!");
						l.addMsg(e.getMessage().toString());
					}
				}
			}	
			text.append("|");
			Filework.write(fname, text);
			return true;
		} catch (SQLException e) {
			l.addMsg("������ ���������� �������� � ��!");
			l.addMsg(e.getMessage().toString());
		}		
		return false;
	}
	/*
	// ���� �� ��������
	private void Indexes (String sname, String tname) throws SQLException, IOException {
		ResultSet dbindex = dbmeta.getIndexInfo(null, sname, tname, false, false);
		while (dbindex.next()) {			
			text.append(" I:").append(dbindex.getString("INDEX_NAME"));
			text.append(" ").append(dbindex.getString("COLUMN_NAME"));
			text.append(" ").append(dbindex.getString("ORDINAL_POSITION"));
			text.append(" ").append(dbindex.getString("TYPE"));
		}	
	}*/
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
			text.append(" C:").append(col.getString("COLUMN_NAME"));
			text.append(" ").append(col.getString("TYPE_NAME"));
			text.append(" ").append(col.getString("COLUMN_SIZE"));
			text.append(" ").append(col.getString("NULLABLE"));
		}	
	}
	
	//���� � ��������� � ������� ������
	private void Keys (String sname, String tname) throws SQLException, IOException {
		ResultSet keys = dbmeta.getPrimaryKeys(null, sname, tname);
		while (keys.next()) {
			text.append(" P:").append(keys.getString("TABLE_NAME")).append(" ").append(keys.getString("COLUMN_NAME"));
		}
		//���� � ������� ������
		ResultSet fk = dbmeta.getImportedKeys(null, sname, tname);
		while (fk.next()) {
			text.append(" F:").append(fk.getString("FKTABLE_NAME") + " ").append(fk.getString("FKCOLUMN_NAME") + " ").append(fk.getString("PKTABLE_NAME") + " ").append(fk.getString("PKCOLUMN_NAME"));
		}
		/*//���� � ������� ������
		for (String tfname : tables) {
			ResultSet fk = dbmeta.getCrossReference(null, null, tfname, null, sname, tname);
			while (fk.next()) {
				text.append(" F:").append(fk.getString("FKTABLE_NAME") + " ").append(fk.getString("FKCOLUMN_NAME") + " ").append(fk.getString("PKTABLE_NAME") + " ").append(fk.getString("PKCOLUMN_NAME"));
			}
		}*/
	}
	
	
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
