package myutils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Sennar
 *
 */
public class Filework {
	public static void write(String fileName, StringBuilder text) {
	    //Определяем файл
	    File file = new File(fileName);
	 
	    try {
	        //проверяем, что если файл не существует то создаем его
	        if(!file.exists()){
	            file.createNewFile();
	        }
	        //PrintWriter обеспечит возможности записи в файл
	        PrintWriter out = new PrintWriter(file.getAbsoluteFile());	 
	        try {
	            //Записываем текст у файл
	            out.print(text.toString());
	        } finally {
	            //После чего мы должны закрыть файл
	            //Иначе файл не запишется
	            out.close();
	        }
	    } catch(IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static String read(String fileName) {
		byte[] str = null;
		String text = null;		
		try {
			FileInputStream inFile = new FileInputStream(fileName);
			str = new byte[inFile.available()];
			inFile.read(str);
			text = new String(str);
			inFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("Файл не найден!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Не удается прочитать файл");
			e.printStackTrace();
		}
		return text;
	}
}
