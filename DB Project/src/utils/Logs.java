package utils;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Sennar
 *
 */
public class Logs {
	private JTextArea logs = null;
	private JFrame form = null;	
	private final static String newLine = System.getProperty("line.separator");
	
	public void addMsg (String msg) {
		logs.append(msg + newLine);
		logs.update(logs.getGraphics());
	}
	
	public void createLogFrame() {
		form = new JFrame ("Лог действий");
		form.setSize(500, 300);
		form.setResizable(false);
		
		logs = new JTextArea(5, 20);
        logs.setEditable(false);
        logs.setLineWrap(true);
        logs.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(logs);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        form.add(scrollPane);
		form.setVisible(true);
	}
}
