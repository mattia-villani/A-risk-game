package gui;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import core.WorldBuilder;
import gui.map.MapRenderer;

public class GUI{
	private static JFrame uiFrame;
	private static JLayeredPane layeredPane;
	private static JComponent worldMap;
	private static JLabel map;
	private static JScrollPane scrollPane;
	private static JLabel leftLabel;
	private static JLabel rightLabel;
	public static JTextArea textLog;
	public static JTextArea playerList;
	public static JTextField textInput;
	public static JButton submitButton;
	public static GuiActionListener listener = new GuiActionListener();
	
	public GUI() throws IOException{	
		uiFrame=new JFrame();
		uiFrame.setBounds(0, 0, 1005,728);
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.getContentPane().setLayout(null);
		uiFrame.setResizable(false);
		
		final BufferedImage leftWood = ImageIO.read(new File("images/left.jpg"));
		leftLabel= new JLabel(){
			@Override
			public void paint(Graphics g){
				super.paint(g);
				g.drawImage(leftWood,0,0,50,700,null);
			}
		};
		leftLabel.setBounds(0, 0, 50, 700);
		uiFrame.getContentPane().add(leftLabel);

		final BufferedImage rightWood = ImageIO.read(new File("images/right.jpg"));
		rightLabel= new JLabel(){
			@Override
			public void paint(Graphics g){
				super.paint(g);
				g.drawImage(rightWood,0,0,50,700,null);
			}
		};
		rightLabel.setBounds(950, 0, 50, 700);
		uiFrame.getContentPane().add(rightLabel);
		
		// Background Map
		final BufferedImage bkimage = ImageIO.read(new File("images/map.jpg"));
		map = new JLabel(){
			@Override
			public void paint(Graphics g){
				super.paint(g);
				g.drawImage(bkimage,0,0,900,600,null);
			}
		};
		map.setBounds(50, 0, 900, 600);
	    
		// Node Map
		worldMap = new MapRenderer(WorldBuilder.Build());
		worldMap.setBounds(50, 0, 1000, 600);
		
		// Combination of Background and Node maps, alignments still off
		layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1000, 600);
        layeredPane.add(map, 1);
        layeredPane.add(worldMap, 0);
        uiFrame.getContentPane().add(layeredPane);

		// Text log area
		textLog = new JTextArea(1, 1);
		textLog.setEditable(false);
		textLog.setLineWrap(true);
		textLog.setWrapStyleWord(true);
		textLog.setBackground(new Color(255, 241, 187));
		textLog.setMargin(new Insets(5,5,5,5));
		
		scrollPane = new JScrollPane(textLog);
		scrollPane.setBounds(50, 601, 700, 64);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
		uiFrame.getContentPane().add(scrollPane);
			
		playerList = new JTextArea(1, 1);
		playerList.setBounds(748, 601, 201, 64);
		playerList.setEditable(false);
		playerList.setBackground(new Color(255, 241, 187));
		playerList.setMargin(new Insets(5,5,5,5));
		uiFrame.getContentPane().add(playerList);
		playerList.setText("Player List" +"\n"+ "*work in progress*");
		
		// Input area
		textInput = new JTextField() {
			public void addNotify() {
	            super.addNotify();
	            requestFocus();
	        }
		};
		textInput.setBounds(50, 665, 700, 35);
		textInput.setBackground(new Color(255, 241, 187));
		textInput.setMargin(new Insets(5,5,5,5));
		textInput.requestFocusInWindow();
		uiFrame.getContentPane().add(textInput);
		
		// Submit button, non focusable, default action for enter key
		submitButton = new JButton("Submit");
		submitButton.addActionListener(listener); 
		submitButton.setBounds(748, 665, 201, 35);
		submitButton.setFocusable(false);
		uiFrame.getContentPane().add(submitButton);
		uiFrame.getRootPane().setDefaultButton(submitButton);
		
		uiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		uiFrame.addWindowListener(new closure());
		uiFrame.setVisible(true);
	}

	private class closure extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			int i = JOptionPane.showOptionDialog(uiFrame,
					"Are you sure you want to quit?",
					"Risky Exit Dialog",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null, null, null);
			if(i == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
}


