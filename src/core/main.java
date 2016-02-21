/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import core.entities.Player;
import core.entities.World;
import gui.GUI;
import gui.map.MapRenderer;

public class main {
	private static String player1Name;
	private static String player2Name;
	private static Player player1;
	private static Player player2;
	private static Player neut1;
	private static Player neut2;
	private static Player neut3;
	private static Player neut4;
	private static boolean didPress = false;
	private static World world;
	private static int turn = -1;
	private static GUI window;


	public static World getWorld() {
		return world;
	}

	public static void setWorld(World world) {
		main.world = world;
	}



	public static void pressed() {

		didPress = true;

	}




	public static void main(String[] args) throws IOException {
		world = WorldBuilder.Build();		
		window=new GUI(world);//create frame

		getNames();
		//create players, then world, then give states armies

		createPlayers();
		window.displayPlayerList(World.getPlayers());
		assignStates();
		assignArmies();	

		rollTheDiceToStart();

		//now add correct numbers and colors to the map
		MapRenderer.Invalidate();

	}

	/**
	 * Gets names from text input box, and creates the players
	 */

	public static void getNames() {

		if (didPress = true) didPress = false;
		while(!didPress){
			window.setLog("Welcome! What is player 1's name?");
		}
		didPress = false;
		player1Name = window.getInput();
		window.resetInput();

		if (didPress = true) didPress = false;
		while(!didPress){
			window.setLog(player1Name + " will be blue. What is player two's name?");
		}
		didPress = false;
		player2Name = window.getInput();
		window.setLog(player2Name + " will be red. Lets begin!"); 
		window.resetInput();
	}


	/**
	 *  Create players from the names each player inputed, then hardcode neutral players
	 */
	public static void createPlayers(){

		player1 = new Player(player1Name, Color.blue);
		World.getPlayers().add(player1);
		player2 = new Player(player2Name, Color.red);
		World.getPlayers().add(player2);
		neut1 = new Player("neutral 1", Color.magenta);
		World.getPlayers().add(neut1);
		neut2 = new Player("neutral 2", Color.green);
		World.getPlayers().add(neut2);
		neut3 = new Player("neutral 3", Color.gray);
		World.getPlayers().add(neut3);
		neut4 = new Player("neutral 4", Color.BLACK);
		World.getPlayers().add(neut4);
	}

	/**
	 *	ration out states, give each player 9 and each neutral 6
	 */
	public static void assignStates(){


		int statesOwned = 0;
		for (int i = 0; i < world.getStates().size(); i++){

			if(i < Constants.INIT_COUNTRIES_PLAYER){
				statesOwned += Constants.INIT_COUNTRIES_PLAYER; 
				world.getState(i).setOwner(player1);        
			}
			else if(i < 2*Constants.INIT_COUNTRIES_PLAYER){ 
				statesOwned += Constants.INIT_COUNTRIES_PLAYER;
				world.getState(i).setOwner(player2);        
			}
			else if(i < Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut1);        
			}
			else if(i < 2*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut2);        
			}
			else if(i < 3*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut3);        
			}
			else if(i < 4*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut4);        
			}
			world.getState(i).setArmy(1);
		}
	}

	public static void assignArmies(){
		ArrayList<Player> players = World.getPlayers();
		for(int i = 0; i < players.size(); ++i){
			if (i < players.size() - Constants.NUM_NEUTRALS){
				players.get(i).setNumArmies(Constants.INIT_UNITS_PLAYER);
			}
			else{
				players.get(i).setNumArmies(Constants.INIT_UNITS_NEUTRAL);
			}
			System.out.println(players.get(i).getName() + " " + players.get(i).getNumArmies());
		}
	}

	public static void rollTheDiceToStart(){
		int player1Roll = diceRollNumber();
		int player2Roll = diceRollNumber();

		if (player1Roll > player2Roll){
			window.setLog("Player1 will go first");
			turn = 0;
		}
		else if(player2Roll > player1Roll){
			window.setLog("Player2 will go first");
			turn = 1;
		}
		else{
			window.setLog("tie! we'll roll again!");
			
			rollTheDiceToStart();
		}


	}

	public static int diceRollNumber(){
		double rand = Math.random();
		int returnVal = 1 + (int)(rand * 6);
		return returnVal;

	}


}