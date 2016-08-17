package lasad.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import lasad.Server;

/**
 * User interface to simplify the use of the server.
 * 
 * @author Frank Loll
 *
 */

// serial UID wasn't written, so I'll suppress warning
@SuppressWarnings("serial")
public class ServerGUI extends JFrame {

	private Server correspondingServerInstance;
	
	private JTextPane logText;
	private JScrollPane myScrollArea;
	
	public ServerGUI(Server ref) {
		this.correspondingServerInstance = ref;
		
		this.setTitle("LASAD - Learning to Argue: Generalized Support Across Domains - Server");
		
		Dimension frameSize = new Dimension(800, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setSize(frameSize);
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent arg0) { 				
				correspondingServerInstance.closeServer();
				dispose();
				System.exit(0);
			}
			
		});
		
		logText = new JTextPane();
		logText.setForeground(Color.BLACK);
		logText.setEditable(false);
		
		myScrollArea = new JScrollPane(logText);
		myScrollArea.setPreferredSize(frameSize);

		this.getContentPane().add(myScrollArea);
		this.setVisible(true);
	}
	
	public synchronized void logToScreen(String text) {
		if(logText.getText().length() > 15000) {
			logText.setText("");
		}
		logText.setText(logText.getText() + text);
//		scrollToNewMessage();
	}
	
//	public void scrollToNewMessage() {
//			Point point = new Point(0, (int)(logText.getSize().getHeight()));
//			myScrollArea.getViewport().setViewPosition(point);
//	}
}
