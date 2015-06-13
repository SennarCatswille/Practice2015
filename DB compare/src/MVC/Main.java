package MVC;

import myutils.ReadConfigFile;

/**
 * @author Sennar
 *
 */
public class Main {
	private static int compareFlag; 
	/*
	 * Определяет направленность программы: 
	 * 		1 - сравнение базы
	 * 		2 - создание файла
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadConfigFile.Initialize();
		compareFlag = ReadConfigFile.getProgramMode();
		
		DBCompareView theView = new DBCompareView(compareFlag);
		DBCompareModel theModel = new DBCompareModel();
		
		@SuppressWarnings("unused")
		DBCompareController theController = new DBCompareController(theView, theModel);
		
		theView.setVisible(true);
	}

}
