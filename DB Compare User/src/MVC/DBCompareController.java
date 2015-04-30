package MVC;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

/**
 * @author Sennar
 *
 */
public class DBCompareController {
	private static DBCompareView theView;
	private DBCompareModel theModel;
	
	public DBCompareController(DBCompareView theView, DBCompareModel theModel) {
		this.theView = theView;
		this.theModel = theModel;
		
		this.theView.AddExitMenuItemActionListener(new ExitMenuItemActionListener());
		this.theView.AddMainPageMenuItemActionListener(new SwitchToMainPageActionListener());
		this.theView.AddCompareDBMenuItemActionListener(new SwitchToCompareDBPageActionListener());
		this.theView.AddCheckDBButtonActionListener(new CheckDBButtonActionListener());
		this.theView.AddDirpathButtonActionListener(new ChooseDirActionListener());
		this.theView.AddFilepathButtonActionListener(new ChooseFileActionListener());
		this.theView.AddConfirmButtonActionListener(new ConfirmButtonActionListener());
	}
	
	class ExitMenuItemActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			theView.dispose();			
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
                theView.SetPathOnButton(button, dirpath);
                AddLogMessage("������� ���������� ��� ������:");
                AddLogMessage("  " + dirpath);
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
                theView.SetPathOnButton(button, filepath);
                AddLogMessage("������ ������� ����:");
                AddLogMessage("  " + filepath);
            }
		}
	}
	
	class ConfirmButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AddLogMessage("������� �������� ����� ����-�������� ���� ������...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			
			String[] dbInfo = theModel.checkDB(dbHost, dbName, dbUser, dbPass);
			theModel.CreateDBMetaFile(dbInfo);
		}
	}
	
	public static void AddLogMessage(String str) {
		theView.AddToLog(str);
	}
}
