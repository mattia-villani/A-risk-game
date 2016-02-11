
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import core.entities.Player;
import gui.GUI;

public class main {
	public static String player1Name;
	public static String player2Name;
	public static Player player1;
	public static Player player2;
	public static boolean didPress = false;
	
	public static void main(String[] args) {
		System.out.println("Hello Wold!");
		GUI window=new GUI();
		window.createGUI(); //create frame
		
		
	
		while(!didPress){
			gui.GUI.textLog.setText("Welcome! What is player 1's name?");
		}
		didPress = false;
		player1Name = gui.GUI.textInput.getText();
		gui.GUI.textInput.setText("");
		while(!didPress){
			gui.GUI.textLog.setText(player1Name + " will be blue. What is player two's name?");
		}
		String player2Name = gui.GUI.textInput.getText();
		gui.GUI.textLog.setText(player2Name + " will be red");	

			
		
	
		
		
	}
}