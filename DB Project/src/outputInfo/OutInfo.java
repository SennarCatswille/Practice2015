/**
 * �����, ������������ ExtraEl � NotEl � ������ �����
 */
package outputInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import objDB.Column;
import objDB.Keys;
import objDB.Scheme;
import objDB.Table;

/**
 * @author ������
 *
 */
public class OutInfo {
	private static String nl = System.getProperty("line.separator");
	private static String tab = "    ";
	private ArrayList<Scheme> schemes = new ArrayList<Scheme>();
	private ArrayList<Table> tables = new ArrayList<Table>();
	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<Keys> keys = new ArrayList<Keys>();	
	
	private ExtraEl ee = null;
	private NotEl ne = null;
	
	public OutInfo(ExtraEl e, NotEl n) {
		ee = e;
		ne = n;
	}
	/*
	 * ����� ������ ����� ����������
	 * 
	 * ������������ ��������:
	 * 0 - ������� ���������
	 * 1 - ������ �������� �����
	 * 2 - ������ ������ � ����
	 * 3 - ������ ���������� ���������� �� �����
	 * 4 - ������ �������� ������ ������
	 */
	public int getFile(String foname) {
		//l.addMsg("�������� �������� ����...");
		//char tab = '\u0009';
		if (!ee.isEmpty() || !ne.isEmpty()) {
			BufferedWriter out;
			try {
				out = new BufferedWriter(new FileWriter(foname));
			} catch (IOException e) {
				e.printStackTrace();
				return 1;
			}
			try {
				ExtraOutInfo(out);
				NotOutInfo(out);
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			}
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return 3;
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return 4;
			}
			return 0;
		}
		return -1;
	}
	
	private ArrayList<String> getSchemes(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			schemes = ((ExtraEl) obj).getSchemes();
		}
		if (obj instanceof NotEl) {
			schemes = ((NotEl) obj).getSchemes();
		}
		for (Scheme s : schemes) {
			str.add(tab + s.getName());
		}
		return str;
	}
	
	private ArrayList<String> getTables(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			tables = ((ExtraEl) obj).getTables();
		}
		if (obj instanceof NotEl) {
			tables = ((NotEl) obj).getTables();
		}
		for (Table t : tables) {
			str.add(tab + t.getSchemeName() + "." + t.getTableName() + nl);
		}
		return str;
	}
	
	private ArrayList<String> getColumns(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			columns = ((ExtraEl) obj).getColumns();
		}
		if (obj instanceof NotEl) {
			columns = ((NotEl) obj).getColumns();
		}
		for (Column c : columns) {
			str.add(tab + c.getSchemTable() + " " + c.getName() + " " + c.getType() + "(" + c.getSize() + ")" + nl);
		}
		return str;
	}
	
	private ArrayList<String> getFKeys(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			keys = ((ExtraEl) obj).getKeys();
		}
		if (obj instanceof NotEl) {
			keys = ((NotEl) obj).getKeys();
		}
		for (Keys k : keys) {
			if (k.getKeyType() == 2) {
				str.add(tab + k.getScheme() + "." + k.getPTable() + " " + k.getKeyName() + " - " + k.getFTable() + " (" + k.getFKeyName() + ")");
			}
		}
		return str;
	}
	
	private ArrayList<String> getPKeys(Object obj) {
		ArrayList<String> str = new ArrayList<>();
		if (obj instanceof ExtraEl) {
			keys = ((ExtraEl) obj).getKeys();
		}
		if (obj instanceof NotEl) {
			keys = ((NotEl) obj).getKeys();
		}
		for (Keys k : keys) {
			if (k.getKeyType() == 1) {
				str.add(tab + k.getScheme() + "." + k.getPTable() + " " + k.getKeyName() + nl);
			}
		}
		return str;
	}
	
	public void ExtraOutInfo(BufferedWriter out) throws IOException {
		ArrayList<String> outStr = null;
		outStr = getSchemes(ee);
		if (!schemes.isEmpty()) {
			if (schemes.size() == 1) {
				out.write("������ �����:" + nl);
			} else {
				out.write("������ �����:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getTables(ee);
		if (!tables.isEmpty()) {
			if (tables.size() == 1) {
				out.write("������ �������:" + nl);
			} else {
				out.write("������ �������:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getColumns(ee);
		if (!columns.isEmpty()) {
			if (columns.size() == 1) {
				out.write("������ ����:" + nl);
			} else {
				out.write("������ ����:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getPKeys(ee);
		if (!keys.isEmpty()) {
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("������ ��������� ����:" + nl);
				} else {
					out.write("������ ��������� �����: " + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
			outStr = getFKeys(ee);
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("������ ������� ����:" + nl);
				} else {
					out.write("������ ������� �����:" + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
		}
		clearAllArrayList();
	}
	
	public void NotOutInfo(BufferedWriter out) throws IOException {
		ArrayList<String> outStr = null;
		outStr = getSchemes(ne);
		if (!schemes.isEmpty()) {
			if (schemes.size() == 1) {
				out.write("����������� �����:" + nl);
			} else {
				out.write("����������� �����:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getTables(ne);
		if (!tables.isEmpty()) {
			if (tables.size() == 1) {
				out.write("���������� �������:" + nl);
			} else {
				out.write("����������� �������:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getColumns(ne);
		if (!columns.isEmpty()) {
			if (columns.size() == 1) {
				out.write("����������� ����:" + nl);
			} else {
				out.write("����������� ����:" + nl);
			}
			for (String str : outStr) {
				out.write(str);
			}
		}
		outStr = getPKeys(ne);
		if (!keys.isEmpty()) {
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("���������� ��������� ����:" + nl);
				} else {
					out.write("����������� ��������� �����: " + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
			outStr = getFKeys(ne);
			if (!outStr.isEmpty()) {
				if (outStr.size() == 1) {
					out.write("����������� ������� ����:" + nl);
				} else {
					out.write("����������� ������� �����:" + nl);
				}
				for (String str : outStr) {
					out.write(str);
				}
			}
		}
		clearAllArrayList();
	}
	
	private void clearAllArrayList() {
		schemes = null;
		tables = null;
		columns = null;
		keys = null;
	}
}
