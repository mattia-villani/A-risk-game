package gui;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import core.WorldBuilder;
import gui.map.MapRenderer;

public class GUI implements ActionListener {
	private JFrame uiFrame;
	public static JTextArea textLog;
	public static JTextField textInput;
	public static JButton submitButton;
	private JLayeredPane layeredPane;
	private JComponent worldMap;
	private JLabel Map;
	

		public void createGUI() throws IOException {	
		uiFrame=new JFrame();
		uiFrame.setBounds(0, 0, 1000,728);
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.getContentPane().setLayout(null);
		uiFrame.setResizable(false);
		
		// Background Map
		final BufferedImage bkimage =ImageIO.read(new File("images/PlaceholderMap.jpg"));
		Map = new JLabel(){
			@Override
			public void paint(Graphics g){
				super.paint(g);
				g.drawImage(bkimage, 0,0,900,600,null);
			}
		};
		Map.setBounds(0, 0, 900, 600);
	    
		// Node Map
		worldMap = new MapRenderer(WorldBuilder.Build());
		worldMap.setBounds(0, 0, 1000, 600);
		worldMap.setOpaque(true);
		
		// Combination of Background and Node maps, alignments still off
		layeredPane = new JLayeredPane();
        layeredPane.setBounds(50,0, 1000, 600);
        layeredPane.add(Map, 1);
        layeredPane.add(worldMap, 0);
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
		textInput.setColumns(10);
		uiFrame.getContentPane().add(textInput);
		
		
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
		String cmd = textInput.getText();
		
		textInput.setText("");	
    }	
}

