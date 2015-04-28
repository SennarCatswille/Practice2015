import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * @author Sennar
 *
 */
public class DBCompareController {
	private DBCompareView theView;
	private DBCompareModel theModel;
	
	public DBCompareController(DBCompareView theView, DBCompareModel theModel) {
		this.theView = theView;
		this.theModel = theModel;
		
		this.theView.AddExitMenuItemActionListener(new ExitMenuItemActionListener());
	}
	
	class ExitMenuItemActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			theView.dispose();			
		}
	}
}
