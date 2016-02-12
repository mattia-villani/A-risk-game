package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiActionListener implements ActionListener{

	final static String newline = "\n";
	
	public GuiActionListener(){
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = GUI.textInput.getText();

		GUI.textLog.setText(cmd + newline + cmd+ newline + cmd+ newline + cmd);
		
		GUI.textInput.setText("");
	}
	
	

}
