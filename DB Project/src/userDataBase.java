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
	private StringBuilder text = new StringBuilder();
	private boolean flag = false;
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
	
	userDataBase (Logs g, String host, String name, String user, String pass, String newtext) {
		boolean isDriverRegistred = false;
		l = g;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
	        l.addMsg("������� DB2 �������� �������!");
	        isDriverRegistred = true;
		} catch (ClassNotFoundException e) {
			l.addMsg("������ �������� �������� DB2!");
			l.addMsg(e.getMessage());
		}
		if (isDriverRegistred) {
			try {
				String strcon = "jdbc:db2://" + host + "/" + name;
				//String strcon = "jdbc:db2:" + name;
				con = DriverManager.getConnection(strcon, user, pass);
				l.addMsg("����������� � �� ����������� �������!");
				dbmeta = con.getMetaData();
				flag = true;
			} catch (SQLException e) {
				l.addMsg("�� ������� ������������ � ��!");
				l.addMsg(e.getMessage());
			}
		}	
		text.append(newtext);
	}
	
	public boolean GetState() {
		return flag;
	}
	
	public int analysis (String foname) {
		StringBuilder workstr = new StringBuilder();
		StringBuilder tablestr = new StringBuilder();
		ArrayList<String> notEl= new ArrayList<String>();
		int pos = 0;
		l.addMsg("������� ������ ���� ������...");
		try {
			//�������� ��� StringBuilder
			//�������� ������������ - ����
			//
			dbmeta = con.getMetaData();
			ArrayList<String> ss = Schemes();
			for (int i = 0; i < ss.size(); i++) {
				pos = 0;
				String sname = ss.get(i);
			// ���� �����
				pos = text.indexOf(sname);
				if (pos == -1) {
					notEl.add("����������� ����� " + sname + System.getProperty("line.separator"));
					continue;
				}					
				l.addMsg("���������� ����� " + Integer.toString(i+1) + "/" + Integer.toString(ss.size()) + " [" + sname + "]");
				//temp = text.substring(0, pos);
				//pos += sname.length();
			// ���� ����� �����
				
				int palka = text.indexOf("|", pos+1);
				workstr.delete(0, workstr.length());
				workstr.append(text.substring(pos + sname.length(), palka));
				TrimStringBuilder(workstr);
				//workstr.trim();
				//int ololo = workstr.length()+1;
				//text.append(text.substring(0, pos) + text.substring(pos + 2 + workstr.length() + sname.length()));		
				text.delete(pos, palka + 1);
				TrimStringBuilder(text);
				// �������� �������
				ResultSet rs = dbmeta.getTables(null, sname, "%", null);
				while (rs.next()) {
					String tname = rs.getString(3);
					pos = workstr.indexOf("T:" + tname);
					if (pos == -1) {
						notEl.add(TableToString(tname, sname, false));
						continue;
					}
					int wnext = workstr.indexOf("T:", pos+2);
					tablestr.delete(0, tablestr.length());
					if (wnext == -1) {
						tablestr.append(workstr.substring(pos + tname.length() + 2));
						workstr.delete(pos, workstr.length());
					} else {
						tablestr.append(workstr.substring(pos + tname.length() + 2, wnext));
						TrimStringBuilder(tablestr);
						workstr.delete(pos, wnext);
					}
					//workstr = workstr.substring(tname.length()+2).trim();
					// �������� ��������
					ArrayList<String> ind = Indexes(sname, tname);
					for (int j = 0; j < ind.size(); j++) {
						//tempstr = text.substring(0, text.indexOf(":", 3)-2);
						String tind = ind.get(j);
						pos = tablestr.indexOf(tind);
						if (pos == -1) {
							notEl.add(IndexToString(ind.get(j), tname, false));
							continue;
						}
						if (ind.get(j).length() != tablestr.length()) {
							tablestr.delete(pos, pos + tind.length());
							TrimStringBuilder(tablestr);
						} else tablestr.delete(0, tablestr.length());
					}
					ind = null;
					// �������� ����� ������
					ArrayList<String> col = Tables(sname, tname);
					for (int j = 0; j < col.size(); j++) {
						String tcol = col.get(j);
						pos = tablestr.indexOf(tcol);
						if (pos == -1) {
							notEl.add(ColumnToString(col.get(j), tname, false));
							continue;
						}
						if (col.get(j).length() != tablestr.length()) {
							tablestr.delete(pos, tcol.length());		
							TrimStringBuilder(tablestr);
						} else tablestr.delete(0, tablestr.length());
					}
					col = null;
					// �������� ������
					ArrayList<String> key = Keys(sname, tname);
					for (int j = 0; j < key.size(); j++) {
						String tkey = key.get(j);
						pos = tablestr.indexOf(tkey);
						if (pos == -1) {
							notEl.add(KeyToString(key.get(j), false));
							continue;
						}
						if (key.get(j).length() != tablestr.length()) {
							tablestr.delete(pos, tkey.length());
							TrimStringBuilder(tablestr);
						} else tablestr.delete(0, tablestr.length());
					}
					key = null;
					// ������� �����
					//if (!tablestr.isEmpty()) {						
						while (tablestr.length() != 0) {
							String c = tablestr.substring(0, 2);
							//workstr = workstr.substring(2);
							int next = tablestr.indexOf(":", 3);
							String t = null;
							if (next != -1) {
								t = tablestr.substring(0, next-1).trim();
							} else {
								t = tablestr.toString();
							}
							if (c.equals("I:"))	notEl.add(IndexToString(t, tname, true));
							if (c.equals("C:")) notEl.add(ColumnToString(t, tname, true));
							if (c.equals("P:")) notEl.add(KeyToString(t, true));
							if (c.equals("F:")) notEl.add(KeyToString(t, true));
							tablestr.delete(0, t.length());
							TrimStringBuilder(tablestr);
							//c = tablestr.substring(0, 2);
						}
					//}					
				}				
			}
			if (!notEl.isEmpty()){
				BufferedWriter  out = new BufferedWriter(new FileWriter(foname));
				for (int i = 0; i < notEl.size(); i++) {
					out.write(notEl.get(i));
				}
				out.flush();
				out.close();
				//l.addMsg("��������� ���������! ��������� ���� ��������� �� ������ " + foname);
				return 1;
			} else {
				//l.addMsg("���������������� ���� ������ ��������� ���������!");
				return 0;
			}	
		} catch (SQLException e) {
			l.addMsg("������ ��������� �������� � ���� ������!");
			l.addMsg(e.getMessage());
		} catch (IOException e) {
			l.addMsg("������ �������� ��� ������ �����!");
			l.addMsg(e.getMessage());
		} catch (StringIndexOutOfBoundsException e) {
			l.addMsg("����������� ������!");
			l.addMsg(e.getMessage());
		}
		return -1;
	}
	
	private void TrimStringBuilder(StringBuilder str) {
		if (str.length() != 0) {
			char fchar = str.charAt(0);
			while (fchar == ' ') {
				str.deleteCharAt(0);
				fchar = str.charAt(0);
			}
			char lchar = str.charAt(str.length()-1);
			while (lchar == ' ') {
				str.deleteCharAt(str.length()-1);
				lchar = str.charAt(str.length()-1);
			}
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
	private String KeyToString(String key, boolean mark) {
		String fp = key.substring(0, 2);
		key = key.substring(2);
		String a[] = key.split(" ");
		String str = "� ������� " + a[0];
		if (mark) {
			str += " ����������� ";
		} else {
			str += " ������ ������ ";
		}
		if (fp.equals("P:")) {
			str += "��������� ���� " + a[1] + ".";
		} else if (fp.equals("F:")) {
			str += "������� ���� " + a[1];
			str += " � ������� " + a[2] + " (" + a[3] + ").";
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
			str = " P:" + keys.getString("TABLE_NAME") + " " + keys.getString("COLUMN_NAME");
			al.add(str.trim());
		}
		//���� � ������� ������
		ResultSet fk = dbmeta.getImportedKeys(null, sname, tname);
		while (fk.next()) {
			str = " F:" + fk.getString("FKTABLE_NAME") + " " + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
			//text.append(" F:").append(fk.getString("FKTABLE_NAME") + " ").append(fk.getString("FKCOLUMN_NAME") + " ").append(fk.getString("PKTABLE_NAME") + " ").append(fk.getString("PKCOLUMN_NAME"));
			al.add(str.trim());
		}
		/*//���� � ������� ������
		ResultSet fk = dbmeta.getCrossReference(null, null, null, null, sname, tname);
		while (fk.next()) {
			str = " F:" + fk.getString("FKTABLE_NAME") + " " + fk.getString("FKCOLUMN_NAME") + " " + fk.getString("PKTABLE_NAME") + " " + fk.getString("PKCOLUMN_NAME");
			al.add(str.trim());
		}*/
		return al;
	}
	
	
	public void close () {
		if (con!= null)	{
			try	{
				con.commit();
				con.close();
			} catch (Exception e) {
				l.addMsg("�������� ��� �������� �����������: ");
				l.addMsg(e.getMessage());
			}
			con = null;
		}
	}
}
