/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import core.entities.Player;
import core.entities.World;
import gui.map.MapRenderer;
import oracle.Oracle;

public class GUI {
	private static FancyFullFrameAnimation uiFrame;
	private static OracledTextField inputArea;
	private static MapPanel mapPanel;
	private static RightBorder rightBorder;
	private static LeftBorder leftBorder;
	private static TextArea textArea;
	private static PlayerList playerList;
	private static MapRenderer worldMap;
	private Animator animator;
	final static String newline = "\n";
	private LinkedList<String> commandBuffer = new LinkedList<String>();

	public GUI (World world) throws IOException {
		animator = new Animator();	
		uiFrame=new FancyFullFrameAnimation();
		uiFrame.getContentPane().setPreferredSize(new Dimension(1000, 700));
		uiFrame.setTitle("Risk: The Game of Software Engineering");
		uiFrame.setResizable(false);
		uiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		uiFrame.addWindowListener(new closure());

		uiFrame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0,0,0,0);

		leftBorder = new LeftBorder();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		uiFrame.getContentPane().add(leftBorder, gbc);

		rightBorder = new RightBorder();
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.weightx = 4;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		uiFrame.getContentPane().add(rightBorder, gbc);

		mapPanel = new MapPanel();
		worldMap = new MapRenderer(world);
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(900, 600));
		mapPanel.setBounds(0, 0, 900, 600);
		worldMap.setBounds(-1, 0, 1000, 600);
		layeredPane.add(mapPanel,1);
		layeredPane.add(worldMap,0);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 2;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		uiFrame.getContentPane().add(layeredPane, gbc);

		textArea = new TextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(650, 64));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 2;
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;
		uiFrame.getContentPane().add(scrollPane, gbc);

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
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 2;
		gbc.weighty = 3;
		gbc.fill = GridBagConstraints.BOTH;
		uiFrame.getContentPane().add(inputArea, gbc);

		playerList = new PlayerList();
		playerList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.weightx = 3;
		gbc.weighty = 3;
		gbc.fill = GridBagConstraints.BOTH;
		uiFrame.getContentPane().add(playerList, gbc);

		uiFrame.pack();
		uiFrame.setVisible(true);
		return;
	}
	
	public void enableOracle(World world, Player player){
		// save the tree in this situation to not to regenerate this every time.
		// one tree for each player
		this.inputArea.enableOracle(Oracle.GenerateOracleTreeForIncreasingArmy(world, player));
	}

	 public String getText() {
		return textArea.getText();
	}

	public void setText(String s) {
		textArea.setText(s);
		return;
	}

	public void addText(String s) {
		textArea.append(s);
		return;
	}

	public void addTextln(String s) {
		textArea.append(newline+s);
		return;
	}
	
	public void resetText() {
		textArea.setText("");
		return;
	}

	public void refreshMap() {
		MapRenderer.Invalidate();
		return;
	}

	public void displayPlayerList(ArrayList<Player> players) {
		playerList.requestToDrawList(players);
		return;
	}

	private class InputAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (commandBuffer) {
				commandBuffer.add(inputArea.getText());
				inputArea.setText("");
				commandBuffer.notify();
			}
			return;
		}
	}
	
	public FancyFullFrameAnimation getUiFrame() {
		return uiFrame;
	}

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