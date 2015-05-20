//- Чтение конфигурационного файла
package myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Sennar
 *
 */
public class ReadConfigFile {
	//private String appDataPath = System.getProperty("user.home")+ "/AppData/Roaming/DB Compare";
	private File LOGS_DIR_PATH;
	private static String baseDir = getBaseDir();
	
	public ReadConfigFile() {
		File filePath = new File("config.properties");
		if (filePath.exists()) {
			try {
				Properties properties = new Properties();
				properties.load(new FileInputStream(filePath));
				String logsDirPath = properties.getProperty("LOGS_DIR_PATH");
				LOGS_DIR_PATH = new File(logsDirPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			//filePath.mkdirs();
			try {
				Properties properties = new Properties();
				properties.setProperty("LOGS_DIR_PATH", baseDir);
				properties.store(new FileOutputStream(filePath), null);
				LOGS_DIR_PATH = new File(baseDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static String getBaseDir() {
		String baseDir = null;
		File tempPath = new File(".");
		try {
			baseDir = tempPath.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baseDir;
	}
	
	public File getLogsDirPath() {
		return LOGS_DIR_PATH;
	}
}
