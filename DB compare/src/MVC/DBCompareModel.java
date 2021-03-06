package MVC;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import outputInfo.OutInfo;
import myutils.DBWork;
import myutils.FileWorkReadDB;
import myutils.FileWorkWriteDB;
import myutils.Filework;
import myutils.ReadConfigFile;
import objDB.DataBase;


/**
 * @author Sennar
 *
 */
public class DBCompareModel {
	private String dirPath;
	private String filePath;
	private OutInfo oi = null;
	public static final String SUN_JAVA_COMMAND = "sun.java.command";
	
	/*
	 * ����� ������������ ����������� � ���� ������.
	 */
	public boolean CheckDBConnection(String dbHost, String dbName, String dbUser, String dbPass) {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");	
			String strcon = "jdbc:db2://" + dbHost + "/" + dbName;
			Connection con = DriverManager.getConnection(strcon, dbUser, dbPass);
			con.close();
			return true;
		} catch (ClassNotFoundException e) {
			DBCompareController.AddLogMessage("�� ������ �����-������� ���� ������! ����� ������:");
			DBCompareController.AddLogMessage(e.getMessage());
			return false;
		} catch (SQLException e) {
			DBCompareController.AddLogMessage("������ ����������� � ���� ������! ����� ������:");
			DBCompareController.AddLogMessage(e.getMessage());
			return false;
		}
	}
	//- ����� �������� ����������� � ���� ������
	public String[] checkDB(String dbHost, String dbName, String dbUser, String dbPass) {
		String[] dbauth = new String[4];
		dbauth[0] = new String(dbHost);
		dbauth[1] = new String(dbName);
		dbauth[2] = new String(dbUser);
		dbauth[3] = new String(dbPass);
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
	
	public void setDirPath(String dp) {
		dirPath = dp;
	}
	
	public void setFilePath(String fp) {
		filePath = fp;
	}
	//- ����� �������� ����� � ���������������
	public void CreateDBMetaFile(String[] dbInfo) {
		DBWork db = new DBWork();
		DataBase db1 = db.createObjDB(dbInfo);
		if (db.getState()) {
			dirPath += "\\out.dat";
			DBCompareController.AddLogMessage("�������� ���� �� ����");
			FileWorkWriteDB fileDB = new FileWorkWriteDB(db1);
			StringBuilder DBString = fileDB.CreateFileDB();
			if (Filework.write(dirPath, DBString)) {
				db.close();
				String str = "��������� ������� ���������! ��������� ���� ��������� � " + dirPath;
				DBCompareController.AddLogMessage(str);
			}
		}
	}
	//- ����� ��������� ���� ������
	public void CompareDB(String[] dbInfo) {
		DBWork db = new DBWork();
		DBCompareController.AddLogMessage("������ ������ ���� ������...");
		DataBase db2 = db.createObjDB(dbInfo);
		DBCompareController.AddLogMessage("�������� ���������� � ���� ������ �� �����...");
		if (!(new File(filePath).exists())) {
			DBCompareController.AddLogMessage("���� ���������� ��� �� ����������!");
			return;
		}
		DataBase db1 = FileWorkReadDB.readDB(filePath);
		DBCompareController.AddLogMessage("�������! ������� ���������...");
		if (db.getState()) {
			dirPath += "\\out.txt";
			db1.equals(db2);
			db.close();	
			oi = new OutInfo(db1.getEE(), db1.getNE(), db1.getPE());
			int flag = oi.getFile(dirPath);
			String str = null;
			switch (flag) {
				case -1:
					str = new String("��������� ���������! ���� ������ ���������!"); break;
				case 0:
					str = new String("��������� ������� ���������! ���������� ��������. ��� ��������� � ����� " + dirPath); break;
				case 1:
					str = new String("������ �������� ����� ��� ������!"); break;
				case 2:
					str = new String("������ ������ ������ � ����!"); break;
				case 3:
					str = new String("������ ���������� ����� �� �����!"); break;
				case 4:
					str = new String("������ �������� ������ ������!"); break;
			}						
			DBCompareController.AddLogMessage(str);
		}	
	}
	
	public String getLogsDirPath() {
		return ReadConfigFile.getLogsDirPath().getPath();
	}
	
	@SuppressWarnings("resource")
	public void directoryToZip(File zipFile) throws IOException {
        File directory = ReadConfigFile.getLogsDirPath();
		URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipFile);
        Closeable res = out;
      
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File child : directory.listFiles()) {
                    String name = base.relativize(child.toURI()).getPath();
                    if (child.isDirectory()) {
                        queue.push(child);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
 
 
                        InputStream in = new FileInputStream(child);
                        try {
                            byte[] buffer = new byte[1024];
                            while (true) {
                                int readCount = in.read(buffer);
                                if (readCount < 0) {
                                    break;
                                }
                                zout.write(buffer, 0, readCount);
                            }
                        } finally {
                            in.close();
                        }
                        zout.closeEntry();                        
                    }
                }
            }
        } finally {
            res.close();
        }
    }
	
	public String getProgramPath() {
		String filePath = null;
		try {
			File file = new File(MVC.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			filePath = file.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return filePath;
	}
	//- ����� �������� ����������
	public static void restartApplication(Runnable runBeforeRestart) throws IOException {
		try {
			String java = System.getProperty("java.home") + "/bin/java";
			List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
			StringBuffer vmArgsOneLine = new StringBuffer();
			for (String arg : vmArguments) {
				if (!arg.contains("-agentlib")) {
					vmArgsOneLine.append(arg);
					vmArgsOneLine.append(" ");
				}
			}
			final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);
	
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
			if (mainCommand[0].endsWith(".jar")) {
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else {
				cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
			for (int i = 1; i < mainCommand.length; i++) {
				cmd.append(" ");
				cmd.append(mainCommand[i]);
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			if (runBeforeRestart!= null) {
				runBeforeRestart.run();
			}
			System.exit(0);
		} catch (Exception e) {
			throw new IOException("Error while trying to restart the application", e);
		}
	}
}

