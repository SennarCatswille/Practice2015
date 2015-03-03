import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 * @author Sennar
 *
 */
public class gui {
	public Logs logs;
	private Cursor hand = null;
	private JFrame frame = null;
	private GridBagLayout gbl = null;
	private GridBagConstraints gbc = null;
	private JPanel aPanel = null;
	private JPanel bPanel = null;
	private JPanel cPanel = null;
	private JRadioButton Radio1 = null;
	private JRadioButton Radio2 = null;
	private JTextField dbHost = null;
	private JTextField dbName = null;
	private JTextField dbUser = null;
	private JPasswordField dbPass = null;	
	private String filepath = null, dirpath = null;
	private JLabel dbAnswer = new JLabel();
	private boolean radioFlag = false;
	
	public void createGUI(Logs l) {
		logs = l;
		hand = new Cursor(Cursor.HAND_CURSOR);
		// ������� �����	
		frame = new JFrame("DB Project");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(createIcon("image/db26.png").getImage());
		gbl = new GridBagLayout();
		frame.setLayout(gbl);
		frame.setResizable(false);
		gbc = new GridBagConstraints(); 
		
		frame.add(createEmptyPanel(5));
		// �������� � ���������
		frame.add(createTopLabel());
		frame.add(createEmptyPanel(1));
		//-------------------------------------------------------------
		// ����� ����������� � ��
		frame.add(createAuthPanel());		
		frame.add(createEmptyPanel(1));
		//-------------------------------------------------------------
		// ����������� ������������ ������� ���������
		frame.add(createRadioGroup());
		//-------------------------------------------------------------
		// ������ � �������� ������ ������ � ����������
		frame.add(createMainPanel());
		//-------------------------------------------------------------
		// ������ ������� ���������
		frame.add(createConButton());
		frame.add(createEmptyPanel(1));
		//-------------------------------------------------------------
		// ������� ����� � ���������� ��
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		
	}
	
	private JLabel createTopLabel() {
		JLabel titleLabel = new JLabel();
		titleLabel.setIcon(createIcon("image/db26.png"));
		titleLabel.setText("<html>���������<br>��� ������</html>");
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.ipady = 20;
		gbl.setConstraints(titleLabel, gbc);
		return titleLabel;
	}
	
	private JPanel createEmptyPanel(int height) {
		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize(new Dimension(200, height));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(emptyPanel, gbc);
		return emptyPanel;
	}
	
	private JTabbedPane createAuthPanel() {
		JTabbedPane dbAuth = new JTabbedPane();
		JPanel dbAuthPanel = new JPanel();
		dbAuthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		dbAuth.addTab("������� ��",	dbAuthPanel);
		
		JLabel dbHostLabel = new JLabel();
		dbHostLabel.setText("������������ ��: ");
		dbHostLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		dbHost = new JTextField(11);
		dbHost.setText("localhost:50000");
		
		JLabel dbNameLabel = new JLabel();
		dbNameLabel.setText("��� ���� ������:  ");
		dbNameLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		dbName = new JTextField(11);
		
		JLabel dbUserLabel = new JLabel();
		dbUserLabel.setText("������������:        ");
		dbUserLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		dbUser = new JTextField(11);
		
		JLabel dbPassLabel = new JLabel();
		dbPassLabel.setText("������:                     ");
		dbPass = new JPasswordField(11);
		
		JPanel dbTest = createDBAuthPanel();
		
		
		dbAuthPanel.add(dbHostLabel);
		dbAuthPanel.add(dbHost);
		dbAuthPanel.add(dbNameLabel);
		dbAuthPanel.add(dbName);
		dbAuthPanel.add(dbUserLabel);
		dbAuthPanel.add(dbUser);
		dbAuthPanel.add(dbPassLabel);
		dbAuthPanel.add(dbPass);
		dbAuthPanel.add(dbTest);
		
		dbAuth.setPreferredSize(new Dimension(266, 150));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(dbAuth, gbc);
		return dbAuth;
	}
	
	private JPanel createDBAuthPanel() {
		JPanel dbTest = new JPanel();
		dbTest.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		dbTest.setPreferredSize(new Dimension(250, 33));
		
		JButton dbTestButton = new JButton("���������");
		dbTestButton.setCursor(hand);
		dbTestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] dbInfo = checkDB();
                if (dbInfo != null) {
					try {
						Class.forName("com.ibm.db2.jcc.DB2Driver");						
						String strcon = "jdbc:db2://" + dbInfo[0] + "/" + dbInfo[1];
						Connection con = DriverManager.getConnection(strcon, dbInfo[2], dbInfo[3]);					
						if (con != null) {
							dbAnswer.setIcon(createIcon("image/good.png"));
							dbAnswer.setText(" �������!");
						}
						con = null;
					} catch (SQLException e1) {
						dbAnswer.setIcon(createIcon("image/bad.png"));
						dbAnswer.setText(" �������!");
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}        	        
        	        
                }
            }
        });		
		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize(new Dimension(20, 5));

		dbTest.add(dbTestButton);
		dbTest.add(emptyPanel);
		dbTest.add(dbAnswer);
		return dbTest;
	}
	
	private JPanel createRadioGroup() {
		ActionListener radioListener = new RadioAction();
		ButtonGroup bg = new ButtonGroup();
		JPanel radioPanel = new JPanel();
		Radio1 = new JRadioButton("������ ��������� ��");
		Radio1.setSelected(true);
		Radio1.addActionListener(radioListener);
		Radio2 = new JRadioButton("��������� �������");		
		Radio2.addActionListener(radioListener);
		radioPanel.add(Radio1);
		radioPanel.add(Radio2);
		radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		radioPanel.setPreferredSize(new Dimension(150, 50));
		bg.add(Radio1);
		bg.add(Radio2);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.ipadx = 150;
		gbc.ipady = 20;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(radioPanel, gbc);
		return radioPanel;
	}
	
	private JPanel createMainPanel() {
		cPanel = new JPanel();
		cPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(cPanel, gbc);
		cPanel.add(createPanel1());
		cPanel.setPreferredSize(new Dimension(200, 120));
		return cPanel;
	}
	
	private JPanel createPanel1() {
		ActionListener act1 = new ChooseDir();
		dirpath = "";
		aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel labelChoose = new JLabel();
		labelChoose.setText("�������� ����� ����������:");
		labelChoose.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		
		JButton fChoose = new JButton("������� ����������...");
		fChoose.setCursor(hand);
		fChoose.addActionListener(act1);			
		aPanel.add(labelChoose);
		aPanel.add(fChoose);
		aPanel.setPreferredSize(new Dimension(200, 75));
		aPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		return aPanel;
	}	
	
	private JPanel createPanel2() {
		ActionListener act1 = new ChooseDir();
		ActionListener act2 = new ChooseFile();
		dirpath = "";
		filepath = "";
		bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel blabelChoose = new JLabel();
		blabelChoose.setText("�������� ���� ��������� ��:");
		JButton bfChoose = new JButton("������� ����...");
		bfChoose.setCursor(hand);
		
		bfChoose.addActionListener(act2);		
		JLabel �labelChoose = new JLabel();
		�labelChoose.setText("�������� ����� ����������:");
		�labelChoose.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		
		JButton �fChoose = new JButton("������� ����������...");
		�fChoose.setCursor(hand);
		�fChoose.addActionListener(act1);		
		bPanel.add(blabelChoose);
		bPanel.add(bfChoose);
		bPanel.add(�labelChoose);
		bPanel.add(�fChoose);
		bPanel.setPreferredSize(new Dimension(200, 120));
		bPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		return bPanel;
	}
	
	private JButton createConButton() {
		ActionListener confirm = new ConfirmAction();
		JButton confirmButton = new JButton(" ���������! ");
		confirmButton.setPreferredSize(new Dimension(100, 15));
		confirmButton.setCursor(hand);
		confirmButton.addActionListener(confirm);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(confirmButton, gbc);
		return confirmButton;
	}
	
	private class ChooseDir implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser diropen = new JFileChooser();         
            diropen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = diropen.showDialog(null, "������� �����");                
            if (ret == JFileChooser.APPROVE_OPTION) {
            	File file = diropen.getSelectedFile();
                dirpath = file.getPath();
                Object button = e.getSource();
                if(button instanceof JButton){
                    if (!dirpath.isEmpty()) {
                    	int t = 0;
            			int k = 0;
            			while (t != -1) {
            				k = t;
            				t = dirpath.indexOf("\\", t+1);
            			}
            			Dimension dim = ((JButton)button).getPreferredSize();
            			((JButton)button).setText(dirpath.substring(k));
            			((JButton)button).setPreferredSize(dim);
            			((JButton)button).updateUI();
                    }
                 }
            }			
		}
	}
	
	private class ChooseFile implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileOpen = new JFileChooser();     
            fileOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int ret = fileOpen.showDialog(null, "������� ����");                
            if (ret == JFileChooser.APPROVE_OPTION) {
            	File file = fileOpen.getSelectedFile();
                filepath = file.getPath(); 
                Object button = e.getSource();
                if(button instanceof JButton){
                    if (!filepath.isEmpty()) {
                    	int t = 0;
            			int k = 0;
            			while (t != -1) {
            				k = t;
            				t = filepath.indexOf("\\", t+1);
            			}
            			Dimension dim = ((JButton)button).getPreferredSize();
            			((JButton)button).setText(filepath.substring(k+1));
            			((JButton)button).setPreferredSize(dim);
            			((JButton)button).updateUI();
                    }
                 }
            }
		}
	}
	
	private class RadioAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Radio1.isSelected() && radioFlag) {
				cPanel.remove(bPanel);
				cPanel.add(createPanel1());
				radioFlag = false;
			} else if (Radio2.isSelected() && !radioFlag) {
				cPanel.remove(aPanel);
				cPanel.add(createPanel2());
				radioFlag = true;
			}
			cPanel.updateUI();
		}		
	}
	
	private void UpdateButtons() {
		if (Radio1.isSelected()) {
			cPanel.remove(aPanel);
			cPanel.add(createPanel1());
		} else if (Radio2.isSelected()) {
			cPanel.remove(bPanel);
			cPanel.add(createPanel2());
		}
		cPanel.updateUI();
	}
	
	private String[] checkDB() {
		String[] dbauth = new String[4];
		dbauth[0] = new String(dbHost.getText());
		dbauth[1] = new String(dbName.getText());
		dbauth[2] = new String(dbUser.getText());
		dbauth[3] = new String(dbPass.getPassword());
		if (dbauth[0].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� ������������ ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[1].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� �������� ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[2].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� ������������ ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (dbauth[3].isEmpty()) {
			JOptionPane.showMessageDialog(null, "��������� ���� ������ ������������ ��!", "������ �����", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return dbauth;
	}
	
	private class ConfirmAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String[] dbInfo = checkDB();
			if (dbInfo != null) {
				if (Radio1.isSelected()) {
					if (dirpath.isEmpty()) {
						JOptionPane.showMessageDialog(null, "�������� ����� ���������� �����!", "������ ������", JOptionPane.ERROR_MESSAGE);
						return;
					}
					logs.createLogFrame();
					logs.addMsg("��������� � ������...");
					idealDataBase db = new idealDataBase(logs, dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);
					if (db.GetState()) {
						dirpath += "\\out.dat";
						if (db.getMeta(dirpath)) {
							db.close();
							String str = "��������� ������� ���������! ��������� ���� ��������� � " + dirpath;
							logs.addMsg(str);
						}
					}
				} else if (Radio2.isSelected()) {
					if (filepath.isEmpty()) {
						JOptionPane.showMessageDialog(null, "�������� ���� ��������� ���� ������!", "������ ������", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (dirpath.isEmpty()) {
						JOptionPane.showMessageDialog(null, "�������� ����� ���������� �����!", "������ ������", JOptionPane.ERROR_MESSAGE);
						return;
					}
					logs.createLogFrame();
					logs.addMsg("��������� � ������...");
					userDataBase db = new userDataBase(logs, dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3], Filework.read(filepath));
					if (db.GetState()) {
						dirpath += "\\out.txt";
						int flag = db.analysis(dirpath);
						db.close();		
						String str = "��������� ������� ���������!";
						if (flag != -1) {
							if (flag == 1) {
								//str += "��������� ���������! ��������� ���� ��������� � " + dirpath;
							} else {
								//str += "������������ ���� ������ ���������!";
							}
							
						} else {
							str = "��������� ��������� � �������! ���������� ��� ���.";
						}			
						logs.addMsg(str);
					}					
				}
			dirpath = "";
			filepath = "";
			UpdateButtons();
			}
		}
	}
	
	private ImageIcon createIcon(String path) {
        java.net.URL imgURL = Work.class.getResource(path);    
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("���� �� ������ " + path);
            return null;
        }
    }
}
