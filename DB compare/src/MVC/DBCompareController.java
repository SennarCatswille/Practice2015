package MVC;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import myutils.ReadConfigFile;

/**
 * ���������� ����������
 * @author Sennar
 *
 */
public class DBCompareController {
	private static DBCompareView theView;
	private DBCompareModel theModel;
	private boolean dirPathFlag = false;
	private boolean filePathFlag = false;
	
	//- ����������� ������
	@SuppressWarnings("static-access")
	public DBCompareController(DBCompareView theView, DBCompareModel theModel) {
		this.theView = theView;
		this.theModel = theModel;
		
		this.theView.AddExitMenuItemActionListener(new ExitMenuItemActionListener());
		this.theView.AddMainPageMenuItemActionListener(new SwitchToMainPageActionListener());
		this.theView.AddCompareDBMenuItemActionListener(new SwitchToCompareDBPageActionListener());
		this.theView.AddCheckDBButtonActionListener(new CheckDBButtonActionListener());
		this.theView.AddDirpathButtonActionListener(new ChooseDirActionListener());
		this.theView.AddFilepathButtonActionListener(new ChooseFileActionListener());
		this.theView.AddSettingsMenuItemActionListener(new SwitchToSettingsMenuActionListener());
		this.theView.AddRestartButtonActionListener(new RestartProgramActionListener());
		
		if (this.theView.getCompareFlag() == 1) {
			this.theView.AddConfirmButtonActionListener(new CompareDBActionListener());
		} else if (this.theView.getCompareFlag() == 2) {
			this.theView.AddConfirmButtonActionListener(new CreateFileActionListener());
		}
		
		//this.theView.SetLogsDirPath(path);
	}
	//- ����������� ������� ��� ���� ����������
	class ExitMenuItemActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			theView.dispose();			
		}
	}	
	
	class RestartProgramActionListener implements ActionListener {
		@SuppressWarnings("static-access")
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				ReadConfigFile.changeProgramMode();
				theModel.restartApplication(null);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	class SwitchToMainPageActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			theView.ChangeForm(0);
		}
	}
	
	class SwitchToCompareDBPageActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			theView.ChangeForm(1);
		}		
	}
	
	class SwitchToSettingsMenuActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			theView.ChangeForm(2);
		}
	}
	
	class CheckDBButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AddLogMessage("������� �������� �����������...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			if (theModel.CheckDBConnection(dbHost, dbName, dbUser, dbPass)) {
				theView.SetGoodConnection();
				AddLogMessage("�������� ����������� �������� �������!");
			} else {
				theView.SetBadConnection();
			}
		}
	}
	
	class ChooseFileActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String filepath;
			JFileChooser fileOpen = new JFileChooser();     
            fileOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int ret = fileOpen.showDialog(null, "������� ����");                
            if (ret == JFileChooser.APPROVE_OPTION) {
            	File file = fileOpen.getSelectedFile();
            	filepath = file.getPath();
            	theModel.setFilePath(filepath);
                Object button = e.getSource();
                theView.SetDirPathOnButton(button, filepath);
                AddLogMessage("������ ������� ����:");
                AddLogMessage("  " + filepath);
                filePathFlag = true;
            }
		}
	}
	
	class ChooseDirActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String dirpath;
			JFileChooser diropen = new JFileChooser();         
            diropen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = diropen.showDialog(null, "������� ����������");                
            if (ret == JFileChooser.APPROVE_OPTION) {
            	File file = diropen.getSelectedFile();
                dirpath = file.getPath();
                theModel.setDirPath(dirpath);
                Object button = e.getSource();
                theView.SetDirPathOnButton(button, dirpath);
                AddLogMessage("������� ���������� ��� ������: ");
                AddLogMessage("  " + dirpath);
                dirPathFlag = true;
            }			
		}
	}
	
	class CreateFileActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AddLogMessage("������� �������� ����� ����-�������� ���� ������...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			
			String[] dbInfo = theModel.checkDB(dbHost, dbName, dbUser, dbPass);
			if (dbInfo != null) {			
				theModel.CreateDBMetaFile(dbInfo);
			} else {
				AddLogMessage("��������� ��������� � �������!");
				AddLogMessage("�������� ������ ����������� � ���� ������");
			}
			theView.SetDefaultNameButton();
			dirPathFlag = false;
			filePathFlag = false;
		}
	}
	
	class CompareDBActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AddLogMessage("������� ��������� ���� ������...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			
			String[] dbInfo = theModel.checkDB(dbHost, dbName, dbUser, dbPass);
			if (dbInfo != null) {
				if (!filePathFlag) {
					JOptionPane.showMessageDialog(null, "�������� ���� ��������� ���� ������!", "������ ������", JOptionPane.ERROR_MESSAGE);
					AddLogMessage("��������� ��������� � �������!");
					AddLogMessage("���������� ���� � ����� ��������� ���� ������");
					return;
				}
				if (!dirPathFlag) {
					JOptionPane.showMessageDialog(null, "�������� ����� ���������� �����!", "������ ������", JOptionPane.ERROR_MESSAGE);
					AddLogMessage("��������� ��������� � �������!");
					AddLogMessage("���������� ���� � ����� ���������� ��������� �����");
					return;
				}
			
				theModel.CompareDB(dbInfo);
				theView.SetDefaultNameButton();
				dirPathFlag = false;
				filePathFlag = false;
			} else {
				AddLogMessage("��������� ��������� � �������!");
				AddLogMessage("�������� ������ ����������� � ���� ������");
			}
			theView.SetDefaultNameButton();
			dirPathFlag = false;
			filePathFlag = false;
		}		
	}
	
	class CreateZipArchive implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		
		}
	}
	
	public static void AddLogMessage(String str) {
		theView.AddToLog(str);
	}
}
