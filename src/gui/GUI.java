package gui;
/**
 * @author Andrew Kilbride
 */

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import core.WorldBuilder;
import core.main;
import core.entities.World;
import gui.map.MapRenderer;
import core.entities.Player;
import core.entities.World;

public class GUI implements ActionListener{
	private static JFrame uiFrame;
	private static JLayeredPane layeredPane;
	private static JComponent worldMap;
	private static JLabel map;
	private static JScrollPane scrollPane;
	private static JLabel leftLabel;
	private static JLabel rightLabel;
	private static JTextArea textLog;
	private static JTextField textInput;
	private static JButton submitButton;
	private static JEditorPane playerListLeft;
	private static JEditorPane playerListRight;
	final static String newline = "\n";
	
	public GUI(World world) throws IOException{	
		uiFrame=new JFrame();
		uiFrame.setBounds(0, 0, 1005,728);
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.getContentPane().setLayout(null);
		uiFrame.setResizable(false);
		
		
		// Left wooden border
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

		// Right wooden border
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
		worldMap = new MapRenderer(world);
		worldMap.setBounds(50, 0, 1000, 600);
		
		// Combination of Background and Node maps
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
		textLog.setBackground(new Color(244, 239, 202));
		textLog.setMargin(new Insets(5,5,5,5));
		
		scrollPane = new JScrollPane(textLog);
		scrollPane.setBounds(50, 601, 700, 64);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);	
		uiFrame.getContentPane().add(scrollPane);

		
		// Player list areas.
		playerListLeft = new JEditorPane();
		playerListLeft.setBounds(750, 601, 99, 63);
		playerListLeft.setEditable(false);
		playerListLeft.setContentType("text/html");
		playerListLeft.setBackground(new Color(244, 239, 202));
		playerListLeft.setMargin(new Insets(0,5,5,5));
		uiFrame.getContentPane().add(playerListLeft);
		
		playerListRight = new JEditorPane();
		playerListRight.setBounds(849, 601, 100, 63);
		playerListRight.setEditable(false);
		playerListRight.setContentType("text/html");
		playerListRight.setBackground(new Color(244, 239, 202));
		playerListRight.setMargin(new Insets(0,5,5,5));
		uiFrame.getContentPane().add(playerListRight);
		
		
		// Input area - Focuses on creation
		textInput = new JTextField(){
			public void addNotify(){
				super.addNotify();
				requestFocus();
			}
		};  
		textInput.setBounds(50, 665, 700, 35);
		textInput.setBackground(new Color(244, 239, 202));
		textInput.setMargin(new Insets(5,5,5,5));
		
		// Prevents focus being lost when clicking on player list
		textInput.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e){
				textInput.requestFocus();
			}
		});
		uiFrame.getContentPane().add(textInput);
		
		
		// Submit button, non focusable, default action for enter key
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this); 
		submitButton.setBounds(748, 665, 201, 35);
		submitButton.setFocusable(false);
		uiFrame.getContentPane().add(submitButton);
		uiFrame.getRootPane().setDefaultButton(submitButton);
		
		
		uiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		uiFrame.addWindowListener(new closure());
		uiFrame.setVisible(true);
		
	}
	
	// Text log methods. Get, set, add and reset.
	public String getLog(){
		return textLog.getText();
	}
	
	public void setLog(String s){
		textLog.setText(s);
	}
	
	public static void addLog(String s){
		textLog.append(newline+s);
	}
	
	public static void resetLog(){
		textLog.setText("");
	}
	
	// Input field methods - get and reset
	public String getInput(){
		return textInput.getText();
	}

	public void resetInput(){
		textInput.setText("");
	}
	
	// Displays the player list
	public void displayPlayerList(){
		ArrayList <Player> players = new ArrayList<Player>();
		String[] names=new String[6];
		String[] colors=new String[6];
		players = main.getPlayers();
		
		String color="";
		String html="";
		String html2="";
		
		// Populates names array for use in in html strings
		for (int i=0; i<6; i++){
			if (players.get(i).getName().length() > 12){
				names[i]=players.get(i).getName().substring(0, 12)+"..";
			}
			else{
				names[i]=players.get(i).getName();
			}
			
			// Populates colors array for use in html strings. Converts RGB value into hex for HTML
			color=Integer.toString(players.get(i).getColor().getRGB() & 0x00ffffff, 16);
			while(color.length() < 6){
				color = "0" + color;
			}
			colors[i]=color;
	    }	
		
		/*
		 * Html strings to feed to the playerLists. At the moment causes the name to not appear if the name contains < or >
		 * as the < and > are interpreted as html code. N.B list split over 2 editorPanes, rather than a single one,
		 * to prevent unusual cell contents pushing the other column out of alignment.
		 */
		html="<html><table width=90 border=0 cellpadding=0 cellspacing=1><tr>"
				+"<td><font color="+colors[0]+">"+names[0]+"</td>"
				+"</tr><tr>"
				+"<td><font color="+colors[2]+">"+names[2]+"</td>"
				+"</tr><tr>"
				+"<td><font color="+colors[4]+">"+names[4]+"</td>"
				+"</tr></table></html>";
		
		html2="<html><table width=90 border=0 cellpadding=0 cellspacing=1><tr>"
				+"<td><font color="+colors[1]+">"+names[1]+"</td>"
				+"</tr><tr>"
				+"<td><font color="+colors[3]+">"+names[3]+"</td>"
				+"</tr><tr>"
				+"<td><font color="+colors[5]+">"+names[5]+"</td>"
				+"</tr></table></html>";
				
		playerListLeft.setText(html);
		playerListRight.setText(html2);
	}
	
	// Window close dialog
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

	// Submit button action
	@Override
	public void actionPerformed(ActionEvent e) {
		main.didPress= true;
	}
}


