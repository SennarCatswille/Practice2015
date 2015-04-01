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
	    //���������� ����
	    File file = new File(fileName);
	 
	    try {
	        //���������, ��� ���� ���� �� ���������� �� ������� ���
	        if(!file.exists()){
	            file.createNewFile();
	        }
	        //PrintWriter ��������� ����������� ������ � ����
	        PrintWriter out = new PrintWriter(file.getAbsoluteFile());	 
	        try {
	            //���������� ����� � ����
	            out.print(text.toString());
	        } finally {
	            //����� ���� �� ������ ������� ����
	            //����� ���� �� ���������
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
			System.out.println("���� �� ������!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("�� ������� ��������� ����");
			e.printStackTrace();
		}
		return text;
	}
}
