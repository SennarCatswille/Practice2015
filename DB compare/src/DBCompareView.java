import java.awt.Font;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

/**
 * 
 */

/**
 * @author Sennar
 *
 */
public class DBCompareView extends JFrame {
	private int index = 0;
	private Font programFont = new Font("Colibri", 0, 13);
	private JMenuBar mainMenu = new JMenuBar();
	private JMenuItem[] menuParts = {
			new JMenuItem("������� ��������"),
			new JMenuItem("��������� ��"),
			new JMenuItem("��� �����"),
			new JMenuItem("������ �����"),
			new JMenuItem("�����")
	};
	private JPanel dbAuthPanel = new JPanel();
	private JLabel logo = new JLabel();
	private JLabel softwareName = new JLabel();
	
	
	public DBCompareView() {
		super("������� ��������");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {}

		this.setSize(500, 300);
		
		this.setJMenuBar(CreateMainMenu(0));
		
		this.setVisible(true);		
	}
	
	private JMenuBar CreateMainMenu(int index) {
		JMenu firstMenu = new JMenu("�������� ������");
		firstMenu.setFont(programFont);
		
		this.index = index;
		for (int i = 0; i < menuParts.length; i++) {
			if (i == menuParts.length - 1) {
				firstMenu.addSeparator();
			}
			if (i != index) {
				menuParts[i].setFont(programFont);
				firstMenu.add(menuParts[i]);
			}
		}
		
		mainMenu.add(firstMenu);
		
		JMenu settings = new JMenu("���������");	
		settings.setFont(programFont);
		mainMenu.add(settings);
		
		mainMenu.setFont(programFont);
		
		return mainMenu;
	}

}
