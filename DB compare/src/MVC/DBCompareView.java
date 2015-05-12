//- ����� �� ��������� �����
package MVC;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * 
 */

/**
 * @author Sennar
 *
 */
public class DBCompareView extends JFrame {
	private int index = -1;
	private JMenuBar mainMenu = new JMenuBar();
	private int compareFlag;
	private String[] versions = {
		new String("0.1"),
		new String("0.5")
	};
	
	private JMenu firstMenu = new JMenu("�������� ������");
	private JMenuItem mainPageMenuItem = new JMenuItem("������� ��������");
	private JMenuItem compareDBMenuItem = new JMenuItem("�������� ����� ����-������");
	private JMenuItem testMenuItem = new JMenuItem("�������� ����� (�� ��������)");
	private JMenuItem exitNemuItem = new JMenuItem("�����");
	private JMenu settingsMenu = new JMenu("���������");	
	
	private JPanel firstPanel = new JPanel();
	private JLabel centerIcon = new JLabel();
	private JLabel nameLabel = new JLabel("��������� ��� ������ v0.1");
	
	private JPanel mainPanel = new JPanel(); // ����� ������
	private JPanel leftPanel = new JPanel(); // ����� ����� � ������������ � ��������
	private JPanel rightPanel = new JPanel(); // ������ ����� � ����� ��������	
	private Font font = new Font("Colibri", 0, 12);
	private JLabel logsLabel = new JLabel("�������� ���������:");
	private JTextArea logs = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(logs);
	private JLabel dbHostLabel = new JLabel("������������ ��:");
	private JLabel dbNameLabel = new JLabel("��� ���� ������:");
	private JLabel dbUserLabel = new JLabel("������������ ��:");
	private JLabel dbPassLabel = new JLabel("������:");
	private JTextField dbHost = new JTextField(15);
	private JTextField dbName = new JTextField(15);
	private JTextField dbUser = new JTextField(15);
	private JPasswordField dbPass = new JPasswordField(15);
	private JButton checkDBButton = new JButton("���������");
	private JLabel responseLabel = new JLabel();
//-- ��� ���������������� ����� ------
	private JLabel versionsLabel = new JLabel("�������� ������:");
	private JComboBox<String> versionsComboBox = new JComboBox<>(versions);
	private JLabel filePathLabel = new JLabel("���� �����:");
	private JButton filePathButton = new JButton("�������� ����...");
//------------------------------------
	private JLabel dirPathLabel = new JLabel("���� ����������:");
	private JButton dirPathButton = new JButton("�������� ����������...");
	private JButton confirmButton = new JButton("������� ����-��������");
	
	private JPanel[] programForms = {
			firstPanel,
			mainPanel
	};
	
	public DBCompareView(int compareFlag) {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setIconImage(createIcon("../image/miniLogo.png").getImage());
		this.setMinimumSize(new Dimension(700, 400));
		this.setSize(500, 350);
		this.compareFlag = compareFlag;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {}		
		
		this.setJMenuBar(CreateMainMenu());
		
		CreateFirstPanel();
		CreateComparePanel();
		
		//this.add(firstPanel);
		//this.add(mainPanel);
		
		ChangeForm(0);
	}
	
	public void ChangeForm(int index) {
		if (this.index != -1) this.remove(programForms[this.index]);
		this.add(programForms[index]);
		this.invalidate();
		this.validate();
		programForms[index].updateUI();
		BlockMenuItem(index); 
	}	
	/*
	 * ����� �������� ��������� �����
	 */
	private void CreateFirstPanel() {
		BlockMenuItem(0);
		
		centerIcon.setIcon(createIcon("../image/ProgramLogo.png"));
		Font font = new Font("Colibri", 1, 16);
		nameLabel.setFont(font);
		
		firstPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(15, 1, 1, 1);
		
		firstPanel.add(centerIcon, gbc);
		firstPanel.add(nameLabel, gbc);		
		
	}
	/*
	 * ����� �������� ����� ��������� ��� ������
	 */
	private void CreateComparePanel() {
		BlockMenuItem(1);
		GridBagConstraints gbc = new GridBagConstraints();
		
		leftPanel.setLayout(new GridBagLayout());
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.gridx = 0;
		gbc.gridy = 0;
		dbHostLabel.setFont(font);
		leftPanel.add(dbHostLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		dbHost.setText("localhost:50000");
		leftPanel.add(dbHost, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		dbNameLabel.setFont(font);
		leftPanel.add(dbNameLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		leftPanel.add(dbName, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		dbUserLabel.setFont(font);
		leftPanel.add(dbUserLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		leftPanel.add(dbUser, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		dbPassLabel.setFont(font);
		leftPanel.add(dbPassLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		leftPanel.add(dbPass, gbc);	
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		checkDBButton.setFont(font);
		leftPanel.add(checkDBButton, gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		responseLabel.setFont(font);
		responseLabel.setVisible(false);
		leftPanel.add(responseLabel, gbc);
		if (compareFlag == 1) {
			gbc.gridx = 0;
			gbc.gridy = 5;
			versionsLabel.setFont(font);
			leftPanel.add(versionsLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 5;
			versionsComboBox.setFont(font);
			leftPanel.add(versionsComboBox, gbc);
			gbc.gridx = 0;
			gbc.gridy = 6;
			filePathLabel.setFont(font);
			leftPanel.add(filePathLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 6;
			filePathButton.setFont(font);
			leftPanel.add(filePathButton, gbc);
		}
		gbc.gridx = 0;
		gbc.gridy = 7;
		dirPathLabel.setFont(font);
		leftPanel.add(dirPathLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 7;
		dirPathButton.setFont(font);
		leftPanel.add(dirPathButton, gbc);
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.insets = new Insets(10, 10, 3, 3);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		if (compareFlag == 1) {
			confirmButton.setText("�������� ���� ������");
		}
		confirmButton.setFont(font);
		leftPanel.add(confirmButton, gbc);
		
		rightPanel.setLayout(new GridBagLayout());
		logsLabel.setFont(font);
		logs.setEditable(false);
        logs.setLineWrap(true);
        logs.setWrapStyleWord(true);
        logs.setPreferredSize(new Dimension(330, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        //gbc.anchor = GridBagConstraints.NORTHWEST;
        rightPanel.add(logsLabel, gbc);
        gbc.gridy = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(scrollPane, gbc);
		
        leftPanel.setMaximumSize(new Dimension(300, 500));
        
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
	}	
	/*
	 * ����� ���������� ������ ���� ��� ������� �������� �� ������� �����
	 */
	private void BlockMenuItem(int index) {
		mainMenu.getMenu(0).getItem(index).setEnabled(false);
		if (this.index != -1) mainMenu.getMenu(0).getItem(this.index).setEnabled(true);
		mainMenu.updateUI();
		this.setTitle(mainMenu.getMenu(0).getItem(index).getText());
		this.index = index;
	}
	/*
	 * ����� �������� �������� ����
	 */
	private JMenuBar CreateMainMenu() {
		Font programFont = new Font("Colibri", 0, 12);
		firstMenu.setFont(programFont);
		
		mainPageMenuItem.setFont(programFont);
		firstMenu.add(mainPageMenuItem);
		compareDBMenuItem.setFont(programFont);
		if (compareFlag == 1) {
			compareDBMenuItem.setText("��������� ���� ������");
		}
		firstMenu.add(compareDBMenuItem);
		testMenuItem.setFont(programFont);
		firstMenu.add(testMenuItem);
		firstMenu.addSeparator();
		exitNemuItem.setFont(programFont);
		firstMenu.add(exitNemuItem);
		
		mainMenu.add(firstMenu);
		
		settingsMenu.setFont(programFont);
		mainMenu.add(settingsMenu);
		
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
	/*
	 * ������ ���������� ���������� ������� � �������
	 */
	public void AddConfirmButtonActionListener (ActionListener al) {
		confirmButton.addActionListener(al);
	}
	
	public void AddFilepathButtonActionListener (ActionListener al) {
		filePathButton.addActionListener(al);
	}
	
	public void AddDirpathButtonActionListener (ActionListener al) {
		dirPathButton.addActionListener(al);
	}
	
	public void AddCheckDBButtonActionListener (ActionListener al) {
		checkDBButton.addActionListener(al);
	}
	
	public void AddMainPageMenuItemActionListener (ActionListener al) {
		mainPageMenuItem.addActionListener(al);
	}
	
	public void AddCompareDBMenuItemActionListener (ActionListener al) {
		compareDBMenuItem.addActionListener(al);
	}
	
	public void AddExitMenuItemActionListener (ActionListener al) {
		exitNemuItem.addActionListener(al);
	}
	
	public void SettingsMenuActionListener (ActionListener al) {
		settingsMenu.addActionListener(al);
	}
	/*
	 * ������ ��������� ���������� �� ����� �����
	 */
	public String getDBHost () {
		return dbHost.getText();
	}
	
	public String getDBName () {
		return dbName.getText();
	}
	
	public String getDBUser () {
		return dbUser.getText();
	}
	
	public String getDBPass () {
		String pass = new String(dbPass.getPassword()); 
		return pass;
	}
	
	public int getCompareFlag() {
		return this.compareFlag;
	}
	/*
	 * ������ ��������� �������� ���������� �����������
	 */
	public void SetDirPathOnButton (Object button, String dp) {
		if(button instanceof JButton){
            if (!dp.isEmpty()) {
            	int t = 0;
    			int k = 0;
    			while (t != -1) {
    				k = t;
    				t = dp.indexOf("\\", t+1);
    			}
    			Dimension dim = ((JButton)button).getPreferredSize();
    			((JButton)button).setText(dp.substring(k));
    			((JButton)button).setPreferredSize(dim);
    			((JButton)button).updateUI();
            }
         }
	}
	
	public void SetGoodConnection() {
		responseLabel.setText(" �������!");
		responseLabel.setIcon(createIcon("../image/good.png"));
		responseLabel.setVisible(true);
	}
	
	public void SetBadConnection() {
		responseLabel.setText(" �������!");
		responseLabel.setIcon(createIcon("../image/bad.png"));
		responseLabel.setVisible(true);
	}
	
	public void SetDefaultNameButton() {
		filePathButton.setText("�������� ����...");
		dirPathButton.setText("�������� ����������...");
	}
	
	public void AddToLog(String str) {
		str += System.getProperty("line.separator");
		logs.append(str);
		logs.update(logs.getGraphics());
	}
}
