import myutils.Logs;

/**
 * @author Sennar
 *
 */
public class Main {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {		
		gui g = new gui();
		Logs l = new Logs();
		g.createGUI(l);
		
		
		/*
		 * ���������� ������� ���������:
		 * 
		Scanner in = new Scanner(System.in);
		System.out.print("������� ���� �� ���� ������ ��� ���������? (1 / 2) ");
		int a = in.nextInt();
		System.out.print("������� �������� ��: ");
		String dbname = in.next();
		System.out.print("������� ������������ ��: ");
		String dbuser = in.next();
		System.out.print("������� ������ ������������: ");
		String dbpass = in.next();		
		
		if (a == 1) {
			System.out.print("������� ���� ������������ �����: ");
			String fname = in.next();
			System.out.println();
			idealDataBase db = new idealDataBase(dbname, dbuser, dbpass);
			db.getMeta(fname);
			db.close();
			System.out.println("���� ������� ������!");
		} else if (a == 2) {
			System.out.print("������� ���� ����� ��������� ��: ");
			String fname = in.next();
			System.out.print("������� ���� ������������ �����: ");
			String foutname = in.next();
			System.out.println();
			
			userDataBase db;
			db = new userDataBase(dbname, dbuser, dbpass, Filework.read(fname));
			db.analysis(foutname);
			db.close();
			
		}
		in.close();
		*/
	}
}
