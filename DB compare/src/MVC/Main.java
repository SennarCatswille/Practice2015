package MVC;
/**
 * 
 */

/**
 * @author Sennar
 *
 */
public class Main {
	private static int compareFlag = 2; 
	/*
	 * ���������� �������������� ���������: 
	 * 		1 - ��������� ����
	 * 		2 - �������� �����
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBCompareView theView = new DBCompareView(compareFlag);
		DBCompareModel theModel = new DBCompareModel();
		
		DBCompareController theController = new DBCompareController(theView, theModel);
		
		theView.setVisible(true);
	}

}
