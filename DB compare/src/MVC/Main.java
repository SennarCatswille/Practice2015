package MVC;
/**
 * 
 */

/**
 * @author Sennar
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBCompareView theView = new DBCompareView();
		DBCompareModel theModel = new DBCompareModel();
		
		DBCompareController theController = new DBCompareController(theView, theModel);
		
		theView.setVisible(true);
	}

}
