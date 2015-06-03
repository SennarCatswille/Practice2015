//- Чтение конфигурационного файла
package myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * @author Sennar
 *
 */
public class ReadConfigFile {
	//private String appDataPath = System.getProperty("user.home")+ "/AppData/Roaming/DB Compare";
	private static File LOGS_DIR_PATH;
	private static int programMode;
	private static String baseDir = getBaseDir();
	
	public static void Initialize() {
		File filePath = new File("config.properties");
		if (filePath.exists()) {
			try {
				Properties properties = new Properties();
				properties.load(new FileInputStream(filePath));
				programMode = new Integer(properties.getProperty("PROGRAM_MODE"));
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
				properties.setProperty("PROGRAM_MODE", "2");
				properties.setProperty("LOGS_DIR_PATH", baseDir);
				properties.store(new FileOutputStream(filePath), null);
				programMode = 2;
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
	
	public static File getLogsDirPath() {
		return LOGS_DIR_PATH;
	}
	
	public static int getProgramMode() {
		return programMode;
	}
	
	public static String getFilePath(String className) {
	      if (!className.startsWith("/")) {
	         className = "/" + className;
	      }

	      className = className.replace('.', '/');
	      className = className + ".class";

	      URL classUrl = new ReadConfigFile().getClass().getResource(className);
	      if (classUrl != null) {
	         String temp = classUrl.getFile();
	         if (temp.startsWith("file:")) {
	            return temp.substring(5);
	         }

	         return temp;
	      } else {
	         return "\nClass '" + className + 
	            "' not found in \n'" +
	            System.getProperty("java.class.path") + "'";
	      }
	   }
}
