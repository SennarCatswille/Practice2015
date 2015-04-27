import java.awt.*;

import javax.swing.*;

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
	private String[] menuParts = {
			new String("������� ��������"),
			new String("��������� ��"),
			new String("��� �����"),
			new String("������ �����"),
			new String("�����")
	};
	JMenuBar mainMenuBar = new JMenuBar();
	
	public DBCompareView() {
		super("������� ��������");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setIconImage(createIcon("image/miniLogo.png").getImage());
		this.setMinimumSize(new Dimension(400, 300));
		this.setSize(500, 350);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {}		
		
		this.setJMenuBar(CreateMainMenu());
		
		this.add(CreateFirstPanel());
		
		this.setVisible(true);		
	}
	/*
	 * ����� �������� ��������� �����
	 */
	private JPanel CreateFirstPanel() {
		BlockMenuItem(0);
		
		JPanel p = new JPanel();
		JLabel centerIcon = new JLabel();
		JLabel nameLabel = new JLabel("��������� ��� ������ v0.1");
		
		centerIcon.setIcon(createIcon("image/ProgramLogo.png"));
		Font font = new Font("Colibri", 1, 16);
		nameLabel.setFont(font);
		
		p.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(15, 1, 1, 1);
		
		p.add(centerIcon, gbc);
		p.add(nameLabel, gbc);		
		
		return p;
	}
	/*
	 * ����� �������� ����� ��������� ��� ������
	 */
	private JPanel CreateComparePanel() {
		BlockMenuItem(1);
		JPanel p = new JPanel();
			
		
		
		return p;
	}
	/*
	 * ����� ���������� ������ ���� ��� ������� �������� �� ������� �����
	 */
	private void BlockMenuItem(int index) {
		mainMenu.getMenu(0).getItem(index).setEnabled(false);
		if (this.index != 0) mainMenu.getMenu(0).getItem(this.index).setEnabled(true);
		mainMenu.updateUI();
		this.index = 0;
	}
	/*
	 * ����� �������� �������� ����
	 */
	private JMenuBar CreateMainMenu() {
		JMenu firstMenu = new JMenu("�������� ������");
		firstMenu.setFont(programFont);
		
		for (int i = 0; i < menuParts.length; i++) {
			if (i == menuParts.length - 1) {
				firstMenu.addSeparator();
			}
			firstMenu.add(new JMenuItem(menuParts[i]));
		}
		
		mainMenu.add(firstMenu);
		
		JMenu settings = new JMenu("���������");	
		settings.setFont(programFont);
		mainMenu.add(settings);
		
		mainMenu.setFont(programFont);
		
		return mainMenu;
	}
	/*
	 * ����� �������� ������ �� �������� �� ����������� ����
	 */
	private ImageIcon createIcon(String path) {
        java.net.URL imgURL = Main.class.getResource(path);    
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("���� �� ������ " + path);
            return null;
        }
    }
}
