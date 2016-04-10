/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import core.Constants;
import core.entities.Player;
import core.entities.World;
import gui.map.MapRenderer;
import oracle.Oracle;
import oracle.Tree;

public class GUI {
	private static FancyFullFrameAnimation uiFrame;
	private static OracledTextField inputArea;
	private static MapPanel mapPanel;
	private static RightBorder rightBorder;
	private static LeftBorder leftBorder;
	private static CentreBorder centreBorder;
	private static TextArea textArea;
	private static PlayerList playerList;
	private static PlayerPanel1 playerPanel1;
	private static PlayerPanel2 playerPanel2;
	private static MapRenderer worldMap;
	private World gameWorld;
	private Animator animator;
	final static String newline = "\n";
	private LinkedList<String> commandBuffer = new LinkedList<String>();
	private boolean acceptMouseInput=false;

	/**
	 * 	<p>The visual interface for the game.
	 * 
	 * 	@param world.
	 * 	@throws IOException exception handled due to images used.
	 */
	public GUI (World world) throws IOException {
		gameWorld = world;
		animator = new Animator();	
		uiFrame = new FancyFullFrameAnimation();
		uiFrame.getContentPane().setPreferredSize(new Dimension(1350, 700));
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.setResizable(false);
		uiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		uiFrame.addWindowListener(new closure());
		uiFrame.getContentPane().setLayout(null);

		// Left wooden image
		leftBorder = new LeftBorder();
		leftBorder.setBounds(0,0,50,700);
		uiFrame.getContentPane().add(leftBorder);

		// Centre wooden image
		centreBorder = new CentreBorder();
		centreBorder.setBounds(950,0,50,700);
		uiFrame.getContentPane().add(centreBorder);
		
		// Right wooden image
		rightBorder = new RightBorder();
		rightBorder.setBounds(1300,0,50,700);
		uiFrame.getContentPane().add(rightBorder);

		// Central map image and world graph overlay
		mapPanel = new MapPanel();
		worldMap = new MapRenderer(world);
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(900, 600));
		mapPanel.setBounds(0, 0, 900, 600);
		worldMap.setBounds(0, 0, 1000, 600);
		layeredPane.add(mapPanel,1);
		layeredPane.add(worldMap,0);
		layeredPane.addMouseListener(new MouseInput());
		layeredPane.setBounds(50,0,900,600);
		uiFrame.getContentPane().add(layeredPane);

		// Display area
		textArea = new TextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setBounds(51, 601, 899, 69);
		uiFrame.getContentPane().add(scrollPane);

		// User input area
		inputArea = new OracledTextField() {
			private static final long serialVersionUID = 1L;
			public void addNotify() {
				super.addNotify();
				requestFocus();
			}
		};
		inputArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				inputArea.requestFocusInWindow();
			}
		});
		inputArea.addActionListener(new InputAction());
		inputArea.setBackground(new Color(244, 239, 202));
		inputArea.setMargin(new Insets(5,5,5,5));
		inputArea.setBounds(51, 670, 899, 30);
		uiFrame.setInputToDisable(inputArea);
		uiFrame.getContentPane().add(inputArea);
	
		// Player 1 panel display
		playerPanel1 = new PlayerPanel1();
		playerPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		playerPanel1.setBounds(1000, 0, 300, 300);
		uiFrame.getContentPane().add(playerPanel1);
		
		// Player 2 panel display
		playerPanel2 = new PlayerPanel2();
		playerPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		playerPanel2.setBounds(1000, 300, 300, 300);
		uiFrame.getContentPane().add(playerPanel2);
		
		// Player list display
		playerList = new PlayerList();
		playerList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		playerList.setBounds(1001, 600, 299, 100);
		uiFrame.getContentPane().add(playerList);

		uiFrame.pack();
		uiFrame.setVisible(true);
		return;
	}

	/**
	 * <p>	Generates oracle tree and returns tree for storage.
	 * <br>	Enables oracle text detection.
	 * 		@param world The game world.
	 * 		@param player Player to generate tree for.
	 * 		@return Oracle tree.
	 */
	public Tree enableOracleAndReturnTree(World world, Player player){
		Tree tree = Oracle.GenerateOracleTreeForIncreasingArmy(world, player);
		inputArea.enableOracle(tree);
		return tree;
	}
	
	public Tree enableOracleAndReturnTreeForMove(World world, Player player){
		Tree tree = Oracle.GenerateOracleTreeForMoving(world, player);
		inputArea.enableOracle(tree);
		return tree;
	}
	/**
	 * <p>	Enables oracle text detection.
	 * 		@param tree Tree to be used for text detection.
	 */
	public void enableOracle(Tree tree){
		inputArea.enableOracle(tree);
	}
	
	/**
	 * <p>	Disables oracle text detection.
	 */
	public void disableOracle(){
		inputArea.disableOracle();
	}

	/**
	 * <p>	Gets the contents of the text display.
	 * 		@return	A string composed of the contents of the text display.
	 */

	public String getText() {
		return textArea.getText();
	}

	/**
	 * <p>	Sets the text display.
	 * @param string The string to set the display to.
	 */
	public void setText(String string) {
		textArea.setText(string);
		return;
	}

	/**
	 * <p>	Adds to the text display.
	 * @param string The string to add.
	 */
	public void addText(String string) {
		textArea.append(string);
		return;
	}

	/**
	 * <p>	Adds to the text display on a new line.
	 * @param string The string to add.
	 */
	public void addTextln(String string) {
		textArea.append(newline+string);
		return;
	}
	
	/**
	 * <p>	Clears the text display.
	 */
	public void resetText() {
		textArea.setText("");
		return;
	}
	
	/**
	 * <p>	Gets the GUI frame to display animation.
	 * 		@return Frame in use.
	 */
	public FancyFullFrameAnimation getUiFrame() {
		return uiFrame;
	}

	/**
	 * <p>	Refreshes the world map.
	 */
	public void refreshMap() {
		MapRenderer.Invalidate();
		return;
	}

	/**
	 * <p>	Displays the player list.
	 * 		@param players ArrayList of players to be displayed.
	 */
	public void displayPlayerList() {
		ArrayList<Player> players = gameWorld.getPlayers();
		playerList.requestToDrawList(players);
		return;
	}
	
	/**
	 * <p>	Displays the player's hands.
	 * 		@param players ArrayList of players in the world.
	 */
	public void displayPlayerHands() {
		ArrayList<Player> players = gameWorld.getPlayers();
		playerPanel1.requestToDrawHand(players);
		playerPanel2.requestToDrawHand(players);
		return;
	}

	/**
	 * <p>	Private class for the action listener.
	 * <br>	Notifies when the commandBuffer has been added to.
	 */
	private class InputAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (commandBuffer) {
				commandBuffer.add(inputArea.getValidatedText());
				inputArea.setText("");
				commandBuffer.notify();
			}
			return;
		}
	}
	
	/**
	 * <p>	Toggles the ability to accept mouse commands. Default off.
	 */
	public void toggleMouseInput(){
		acceptMouseInput=!acceptMouseInput;
	}
	
	/**
	 * <p>	Private class for the mouse listener
	 * <br>	Determines state from mouse click location,adds to command buffer and notifies sync
	 */
	private class MouseInput implements MouseListener{	
		@Override
		public void mouseClicked(MouseEvent e) {		
			int x=e.getX();
			int y=e.getY();
			String stateFound=findState(x,y);
			if(stateFound.length()>0 && acceptMouseInput==true){
				synchronized (commandBuffer) {
					commandBuffer.add(stateFound);
					commandBuffer.notify();
				}
			} 
			return;
		}
		@Override
		public void mouseEntered(MouseEvent e) {	
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		/**
		 * <p>	Method for finding a state clicked by mouse input.
		 * 		@param x The X coordinate of the mouse click.
		 * 		@param y The Y coordinate of the mouse click.
		 * 		@return The state clicked on or "" if no state is found.
		 */
		public String findState(int x, int y){
			int[][] COUNTRY_COORD=Constants.COUNTRY_COORD;
			
			for (int i=0; i<42; i++){
				if ( (x-21<COUNTRY_COORD[i][0] && COUNTRY_COORD[i][0]<x+21) && (y-21<COUNTRY_COORD[i][1] && COUNTRY_COORD[i][1]<y+21) ){
					return gameWorld.getState(i).getName();
				}			
			}
			return "";
		}

	}
	
	/**
	 * <p>	Clears the command buffer
	 * <p>	Used to prevent additional unwanted commands being input by the user and then retrieved by getCommand()
	 * <br>	For example hitting the enter key when sleep has been called.
	 */
	public void clearCommands(){
		commandBuffer.clear();
		return;
	}
	
	/**
	 * <p> 	Checks if the commandBuffer has content.
	 * 		@return true if the buffer has content.
	 */
	public boolean queuedCommands(){
		if(commandBuffer.isEmpty())return false;	
		return true;
	}
	
	/**
	 * <p>	Gets commands entered by the user. Synced to the command buffer.
	 * 		@return the latest command added to the buffer.
	 */
	public String getCommand() {
		String command;
		synchronized (commandBuffer) {
			while (commandBuffer.isEmpty()) {
				try {
					commandBuffer.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			command = commandBuffer.pop();
		}
		return command;
	}
	
	/**
	 * <p>	Private class to handle window closure
	 */
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