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
	
	userDataBase (String name, String user, String pass, String newtext) {
		boolean isDriverRegistred = false;
		try {
			Driver driver = new COM.ibm.db2.jdbc.app.DB2Driver();
	        DriverManager.registerDriver(driver);
	        System.out.println("������� DB2 �������� �������!");
	        isDriverRegistred = true;
		} catch (SQLException e) {
			System.out.println("������ �������� �������� DB2!");
			e.printStackTrace();
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2:" + name;
				con = DriverManager.getConnection(strcon, user, pass);					
				System.out.println("����������� � �� ����������� �������!");
				dbmeta = con.getMetaData();
			} catch (SQLException e) {
				System.out.println("�� ������� ������������ � ��!");
				e.printStackTrace();
			}
		}	
		text = newtext;
	}
	
	public void analysis (String foname) {
		String workstr = null;
		ArrayList<String> notEl= new ArrayList<String>();
		int pos = 0;
		System.out.println("������� ������ ���� ������...");
		try {
			dbmeta = con.getMetaData();
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				pos = 0;
				String sname = ss.get(i);
				pos = text.indexOf(sname);
				if (pos == -1) {
					notEl.add("����������� ����� " + sname + System.getProperty("line.separator"));
					continue;
				}							
				//temp = text.substring(0, pos);
				//pos += sname.length();
				int palka = text.indexOf("|");
				workstr = text.substring(pos + sname.length(), palka).trim();
				//workstr.trim();
				//int ololo = workstr.length()+1;
				text = (text.substring(0, pos) + text.substring(pos + 2 + workstr.length() + sname.length())).trim();		
				// �������� �������
				ResultSet rs = dbmeta.getTables(null, sname, "%", null);
				while (rs.next()) {
					String tname = rs.getString(3);
					pos = workstr.indexOf("T:" + tname);
					if (pos == -1) {
						notEl.add(TableToString(tname, sname, false));
						continue;
					}
					workstr = workstr.substring(tname.length()+2).trim();
					// �������� ��������
					ArrayList<String> ind = Indexes(sname, tname);
					for (int j = 0; j < ind.size(); j++) {
						//tempstr = text.substring(0, text.indexOf(":", 3)-2);
						pos = workstr.indexOf(ind.get(j));
						if (pos == -1) {
							notEl.add(IndexToString(ind.get(j), tname, false));
							continue;
						}
						if (ind.get(j).length() != workstr.length()) {
							workstr = (workstr.substring(0, pos) + workstr.substring(pos + 1 + ind.get(j).length())).trim();
						} else workstr = "";
					}
					ind = null;
					// �������� ����� ������
					ArrayList<String> col = Tables(sname, tname);
					for (int j = 0; j < col.size(); j++) {
						pos = workstr.indexOf(col.get(j));
						if (pos == -1) {
							notEl.add(ColumnToString(col.get(j), tname, false));
							continue;
						}
						if (col.get(j).length() != workstr.length()) {
							workstr = (workstr.substring(0, pos) + workstr.substring(pos + 1 + col.get(j).length())).trim();		
						} else workstr = "";
					}
					col = null;
					// �������� ������
					ArrayList<String> key = Keys(sname, tname);
					for (int j = 0; j < key.size(); j++) {
						pos = workstr.indexOf(key.get(j));
						if (pos == -1) {
							notEl.add(KeyToString(key.get(j), tname, false));
							continue;
						}
						if (key.get(j).length() != workstr.length()) {
							workstr = (workstr.substring(0, pos) + workstr.substring(pos + 1 + key.get(j).length())).trim();
						} else workstr = "";
					}
					key = null;
					// ������� �����
					if (!workstr.isEmpty()) {
						String c = workstr.substring(0, 2);
						while (!c.equals("T:") && !workstr.isEmpty()) {
							//workstr = workstr.substring(2);
							String t = workstr.substring(0, workstr.indexOf(":", 3)-1).trim();
							if (c.equals("I:"))	notEl.add(IndexToString(t, tname, true));
							if (c.equals("C:")) notEl.add(ColumnToString(t, tname, true));
							if (c.equals("P:")) notEl.add(KeyToString(t, tname, true));
							if (c.equals("F:")) notEl.add(KeyToString(t, tname, true));
							workstr = workstr.substring(t.length()).trim();
							c = workstr.substring(0, 2);
						}
					}					
				}				
			}
			if (!notEl.isEmpty()){
				BufferedWriter  out = new BufferedWriter(new FileWriter(foname));
				for (int i = 0; i < notEl.size(); i++) {
					out.write(notEl.get(i));
				}
				out.flush();
				out.close();
				System.out.println("��������� ���������! ��������� ���� ��������� �� ������ " + foname);
			} else {
				System.out.println("���������������� ���� ������ ��������� ���������!");
			}
		} catch (SQLException e) {
			System.out.println("������ ��������� �������� � ���� ������!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("������ �������� ��� ������ �����!");
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println(pos);
			System.out.println(workstr);
		}
	}
	
	// �������������� ��� ������ � ���� �������
	private String IndexToString (String ind, String tn, boolean mark) {
		ind = ind.substring(2);
		String[] a = ind.split(" ");
		String str = null;
		if (mark) {
			str = "����������� ������ " + a[0];
		} else {
			str = "������ ������ ������ " + a[0];
		}
		str += " ��� ���� " + a[1];
		str += " � ������� " + a[2];
		Integer type = new Integer(a[3]);
		switch (type) {
			case 0: str += " � ����� tableIndexStatistic ";	break;
			case 1: str += " � ����� tableIndexClustered ";	break;
			case 2: str += " � ����� tableIndexHashed "; break;
			case 3: str += " � ����� tableIndexOther ";	break;
		}
		str += "� ������� " + tn + "." + System.getProperty("line.separator");
		return str;
	}
	
	// �������������� ��� ������ � ���� ����
	private String ColumnToString (String col, String tn, boolean mark) {
		col = col.substring(2);
		String a[] = col.split(" ");
		String str = "� ������� " + tn;
		if (mark) {
			str += " ����������� ���� " + a[0];
		} else {
			str += " ������� ������ ���� " + a[0];
		}
		str += " � ����� " + a[1];
		str += " �������� " + a[2] + " ����, ";
		if (a[3].equals("0")) str += "�� ��������� ������ ��������.";
		if (a[3].equals("1")) str += "������ �������� ���������.";
		
		str += System.getProperty("line.separator");
		return str;
	}
	// �������������� ���������� � ����� ��� ������ � ����
	private String KeyToString(String key, String tn, boolean mark) {
		String fp = key.substring(0, 2);
		key = key.substring(2);
		String a[] = key.split(" ");
		String str = "� ������� " + tn;
		if (mark) {
			str += " ����������� ";
		} else {
			str += " ������ ������ ";
		}
		if (fp.equals("P:")) {
			str += "��������� ���� " + a[0] + ".";
		} else if (fp.equals("F:")) {
			str += "������� ���� " + a[0];
			str += " � ������� " + a[1] + " (" + a[2] + ").";
		}
		str += System.getProperty("line.separator");
		return str;
	}
	
	// �������������� ���������� � ������� ��� ������ � ����
	private String TableToString(String tn, String sn, boolean mark) {
		String str = null;
		if (mark) {
			str = "����������� ������� " + tn;
		} else {
			str = "������� ������ ������� " + tn;
		}
		str += " � ����� " + sn + "." + System.getProperty("line.separator");
		return str;
	}
		
	// ���� �� ��������
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
	
	//���� � ��������� � ������� ������
	private ArrayList<String> Keys (String sname, String tname) throws SQLException {
		String str = null;
		ArrayList<String> al = new ArrayList<String>();
		ResultSet keys = dbmeta.getPrimaryKeys(null, sname, tname);
		while (keys.next()) {
			str = " P:" + keys.getString("COLUMN_NAME");
			al.add(str.trim());
		}
		//���� � ������� ������
		ResultSet fk = dbmeta.getCrossReference(null, null, null, null, sname, tname);
		while (fk.next()) {
			str = " F:" + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
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
				System.out.println("�������� ��� �������� �����������: " + e.getMessage());
			}
			con = null;
		}
	}
}
