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
	private static ArrayList <Player> players = new ArrayList<Player>();
	public static boolean didPress = false;
	private static World world;

	public static World getWorld() {
		return world;
	}

	public static void setWorld(World world) {
		main.world = world;
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static void setPlayers(ArrayList<Player> players) {
		main.players = players;
	}

	

	

	public static void main(String[] args) throws IOException {

		world = WorldBuilder.Build();		
		GUI window=new GUI(world);//create frame

		while(!didPress){
			window.setLog("Welcome! What is player 1's name?");
		}
		didPress = false;
		player1Name = window.getInput();
		window.resetInput();
		while(!didPress){
			window.setLog(player1Name + " will be blue. What is player two's name?");
		}
		didPress = false;
		player2Name = window.getInput();
		window.setLog(player2Name + " will be red. Lets begin!"); 
		window.resetInput();

		//create players, then world, then give states armies

		createPlayers();
		window.displayPlayerList(players);
		assignArmies();	

		//now add correct numbers and colors to the map
		MapRenderer.Invalidate();

	}

	/**
	 *  Create players from what they inputed, then hardcode neutral players
	 */
	public static void createPlayers(){

		player1 = new Player(player1Name, Color.blue);
		players.add(player1);
		player2 = new Player(player2Name, Color.red);
		players.add(player2);
		neut1 = new Player("neutral 1", Color.BLACK);
		players.add(neut1);
		neut2 = new Player("neutral 2", Color.green);
		players.add(neut2);
		neut3 = new Player("neutral 3", Color.darkGray);
		players.add(neut3);
		neut4 = new Player("neutral 4", Color.ORANGE);
		players.add(neut4);
	}

	/**
	 *	ration out states, give each player 9 and each neutral 6
	 */
	public static void assignArmies(){


		int statesOwned = 0;
		for (int i = 0; i < world.getStates().size(); i++){
			
			if(i </* statesOwned + */Constants.INIT_COUNTRIES_PLAYER){
				statesOwned += Constants.INIT_COUNTRIES_PLAYER; // <- ERROR HERE : this keeps increasing statesOwnerd of INIT_COUNTRIES_PLAYER each cicle so this if is never false
				world.getState(i).setOwner(player1);        
			}
			else if(i < /*statesOwned + */2*Constants.INIT_COUNTRIES_PLAYER){ // <- FIX
				statesOwned += Constants.INIT_COUNTRIES_PLAYER;
				world.getState(i).setOwner(player2);        
			}
			else if(i < /*statesOwned + */Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ // <- FIX
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut1);        
			}
			else if(i < /*statesOwned + */2*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ // <- FIX
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut2);        
			}
			else if(i < /*statesOwned + */3*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ // <- FIX
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut3);        
			}
			else if(i < /*statesOwned + */4*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ // <- FIX
				statesOwned += Constants.INIT_COUNTRIES_NEUTRAL;
				world.getState(i).setOwner(neut4);        
			}
			world.getState(i).setArmy(1);
		}
	}
}