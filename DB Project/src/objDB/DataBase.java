/**
 * ����� ����� ��� ���� ������
 */
package objDB;

import java.util.ArrayList;

import outputInfo.ExtraEl;
import outputInfo.NotEl;

/**
 * @author ������
 *
 */
public class DataBase {
	private ExtraEl el;
	private NotEl ne;
	
	private ArrayList<Scheme> schemes = null;
	
	public DataBase(ArrayList<Scheme> s) {
		schemes = new ArrayList<Scheme>(s);
	}
	
	public DataBase(DataBase d) {
		schemes = d.getSchemes();
	}
	
	public boolean equals(DataBase db) {
		// ���������.equals(����������������)
		//- ��������� ��� ������
		return true;
	}
	
	public ArrayList<Scheme> getSchemes() {
		return schemes;
	}
}
