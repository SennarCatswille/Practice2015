import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

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
			new JMenuItem("Главная страница"),
			new JMenuItem("Сравнение БД"),
			new JMenuItem("Еще пункт"),
			new JMenuItem("Третий пункт"),
			new JMenuItem("Выход")
	};
	private JPanel dbAuthPanel = new JPanel();
	private JLabel logo = new JLabel();
	private JLabel softwareName = new JLabel();
	
	
	public DBCompareView() {
		super("Главная страница");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon("image/logo.gif");
		this.setIconImage(icon.getImage());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {}

		this.setSize(500, 300);
		
		this.setJMenuBar(CreateMainMenu(0));
		
		this.add(CreateFirstPanel());
		
		this.setVisible(true);		
	}
	
	private JPanel CreateFirstPanel() {
		JPanel p = new JPanel();
		JLabel centerIcon = new JLabel();
		JLabel nameLabel = new JLabel("Сравнение баз данных v0.1");
		
		centerIcon.setIcon(new ImageIcon("image/logo.png"));
		nameLabel.setFont(programFont);
		
		p.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		
		p.add(centerIcon, gbc);
		p.add(nameLabel, gbc);
		
		return p;
	}
	
	private JMenuBar CreateMainMenu(int index) {
		JMenu firstMenu = new JMenu("Выберите раздел");
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
		
		JMenu settings = new JMenu("Настройки");	
		settings.setFont(programFont);
		mainMenu.add(settings);
		
		mainMenu.setFont(programFont);
		
		return mainMenu;
	}
	
}
