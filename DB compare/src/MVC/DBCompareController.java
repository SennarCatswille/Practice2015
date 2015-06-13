package MVC;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import myutils.ReadConfigFile;

/**
 * Контроллер приложения
 * @author Sennar
 *
 */
public class DBCompareController {
	private static DBCompareView theView;
	private DBCompareModel theModel;
	private boolean dirPathFlag = false;
	private boolean filePathFlag = false;
	
	//- Конструктор класса
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
	//- Обработчики событий для вида приложения
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
			AddLogMessage("Начинаю проверку подключения...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			if (theModel.CheckDBConnection(dbHost, dbName, dbUser, dbPass)) {
				theView.SetGoodConnection();
				AddLogMessage("Проверка подключения пройдена успешно!");
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
            int ret = fileOpen.showDialog(null, "Открыть файл");                
            if (ret == JFileChooser.APPROVE_OPTION) {
            	File file = fileOpen.getSelectedFile();
            	filepath = file.getPath();
            	theModel.setFilePath(filepath);
                Object button = e.getSource();
                theView.SetDirPathOnButton(button, filepath);
                AddLogMessage("Выбран входной файл:");
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
            int ret = diropen.showDialog(null, "Выбрать директорию");                
            if (ret == JFileChooser.APPROVE_OPTION) {
            	File file = diropen.getSelectedFile();
                dirpath = file.getPath();
                theModel.setDirPath(dirpath);
                Object button = e.getSource();
                theView.SetDirPathOnButton(button, dirpath);
                AddLogMessage("Выбрана директория для вывода: ");
                AddLogMessage("  " + dirpath);
                dirPathFlag = true;
            }			
		}
	}
	
	class CreateFileActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AddLogMessage("Начинаю создание файла мета-описания базы данных...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			
			String[] dbInfo = theModel.checkDB(dbHost, dbName, dbUser, dbPass);
			if (dbInfo != null) {			
				theModel.CreateDBMetaFile(dbInfo);
			} else {
				AddLogMessage("Сравнение завершено с ошибкой!");
				AddLogMessage("Неверные данные подключения к базе данных");
			}
			theView.SetDefaultNameButton();
			dirPathFlag = false;
			filePathFlag = false;
		}
	}
	
	class CompareDBActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AddLogMessage("Начинаю сравнение базы данных...");
			String dbHost = theView.getDBHost();
			String dbName = theView.getDBName();
			String dbUser = theView.getDBUser();
			String dbPass = theView.getDBPass();
			
			String[] dbInfo = theModel.checkDB(dbHost, dbName, dbUser, dbPass);
			if (dbInfo != null) {
				if (!filePathFlag) {
					JOptionPane.showMessageDialog(null, "Выберите файл эталонной базы данных!", "Ошибка выбора", JOptionPane.ERROR_MESSAGE);
					AddLogMessage("Сравнение завершено с ошибкой!");
					AddLogMessage("Отсутсвует путь к файлу эталонной базы данных");
					return;
				}
				if (!dirPathFlag) {
					JOptionPane.showMessageDialog(null, "Выберите место сохранения файла!", "Ошибка выбора", JOptionPane.ERROR_MESSAGE);
					AddLogMessage("Сравнение завершено с ошибкой!");
					AddLogMessage("Отсутсвует путь к месту сохранения выходного файла");
					return;
				}
			
				theModel.CompareDB(dbInfo);
				theView.SetDefaultNameButton();
				dirPathFlag = false;
				filePathFlag = false;
			} else {
				AddLogMessage("Сравнение завершено с ошибкой!");
				AddLogMessage("Неверные данные подключения к базе данных");
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
