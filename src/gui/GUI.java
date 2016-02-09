package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GUI implements ActionListener {
	private JFrame uiFrame;
	private JTextArea textLog;
	private JTextField textInput;
	private JButton submitButton;
	

		public void createGUI() {	
		uiFrame=new JFrame();
		uiFrame.setBounds(0, 0, 1000,728);
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.getContentPane().setLayout(null);
		uiFrame.setResizable(false);
		
		// Placeholder for the map
		JLabel Map = new JLabel();
		Map.setBounds(0, 0, 1000, 600);
		Map.setIcon(new ImageIcon("images/PlaceholderMap.jpg"));
		uiFrame.getContentPane().add(Map);
	
		textLog = new JTextArea(50, 100);
		textLog.setBounds(0, 600, 1000, 65);
		textLog.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textLog);	
		uiFrame.getContentPane().add(textLog);
		
		textInput = new JTextField();
		textInput.setBounds(0, 665, 800, 35);
		uiFrame.getContentPane().add(textInput);
		textInput.setColumns(10);
		
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this); 
		submitButton.setBounds(798, 665, 200, 35);
		submitButton.setFocusable(false);
		uiFrame.getContentPane().add(submitButton);
		uiFrame.getRootPane().setDefaultButton(submitButton);
		
		uiFrame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
        // Button actions
    };
	
}
