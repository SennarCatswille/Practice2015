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
		 * Консольный вариант программы:
		 * 
		Scanner in = new Scanner(System.in);
		System.out.print("Создать файл по базе данных или проверить? (1 / 2) ");
		int a = in.nextInt();
		System.out.print("Введите название БД: ");
		String dbname = in.next();
		System.out.print("Введите пользователя БД: ");
		String dbuser = in.next();
		System.out.print("Введите пароль пользователя: ");
		String dbpass = in.next();		
		
		if (a == 1) {
			System.out.print("Введите путь создаваемого файла: ");
			String fname = in.next();
			System.out.println();
			idealDataBase db = new idealDataBase(dbname, dbuser, dbpass);
			db.getMeta(fname);
			db.close();
			System.out.println("Файл успешно создан!");
		} else if (a == 2) {
			System.out.print("Введите путь файла эталонной БД: ");
			String fname = in.next();
			System.out.print("Введите путь создаваемого файла: ");
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
