package gui;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import core.WorldBuilder;
import gui.map.MapRenderer;

public class GUI implements ActionListener {
	private JFrame uiFrame;
	public static JTextArea textLog;
	public static JTextField textInput;
	public static JButton submitButton;


	public void createGUI() {	
		uiFrame=new JFrame();
		uiFrame.setBounds(0, 0, 1000,728);
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.getContentPane().setLayout(null);
		uiFrame.setResizable(false);

		// Background Map
		JLabel Map = new JLabel();
		Map.setBounds(0, 0, 1000, 600);
		Map.setIcon(new ImageIcon("images/PlaceholderMap.jpg"));

		// Node Map
		JComponent worldMap = new MapRenderer( WorldBuilder.Build() );
		worldMap.setBounds(0, 0, 1000, 600);
		worldMap.setOpaque(false);

		// Combination of Background and Node maps, alignments still off
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0,0, 1000, 600);
		layeredPane.add(worldMap, 0);
		layeredPane.add(Map, 1);
		uiFrame.getContentPane().add(layeredPane);

		// Text log area
		textLog = new JTextArea(50, 100);
		textLog.setBounds(0, 600, 1000, 65);
		textLog.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textLog);	
		uiFrame.getContentPane().add(textLog);

		// Input area
		textInput = new JTextField();
		textInput.setBounds(0, 665, 800, 35);
		uiFrame.getContentPane().add(textInput);
		textInput.setColumns(10);

		// Submit button, non focusable, default action for enter key
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
		System.out.println("hr");
	};	
}
