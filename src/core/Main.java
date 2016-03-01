/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import core.entities.*;
import gui.GUI;
import gui.Rolling;
import gui.map.MapRenderer;
import oracle.Tree;


public class Main {
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
		Main.world = world;
	}

	public static void pressed() {
		didPress = true;
	}

	public static void main(String[] args) throws IOException {

		world = WorldBuilder.Build();		
		window = new GUI(world);//create frame


		Rolling.loadImages( window.getClass() );

		getNames();
		//create players, then world, then give states armies
		createPlayers();
		window.displayPlayerList(World.getPlayers());
		assignArmies();
		assignStates();	
		window.refreshMap();
		rollTheDiceToStart();
		chooseReinforcements();
		window.setText("Let's play!");

	}

	/**
	 * lets players choose where they should put their reinforcements before the game starts
	 */

	public static void chooseReinforcements(){
		boolean player1Turn = false;
		if (turn == 0) player1Turn = true;
		Tree[] trees = new Tree[6];


		Player[] players = new Player[]{ player1, player2, neut1, neut2, neut3, neut4};
		for (int i = 0; i < Constants.TUNRS_OF_REINFORCEMENTS; ++i){
			for (int j = 0; j < Constants.NUM_TOTAL_PLAYERS; j++ ){
				if (player1Turn){
					window.setText(player1.getName() + " choose country to place reinforcements for: " + players[turn].getName());
					window.addText("\n you will only be able to type in a name if it has the correct owner.");
				}
				else{
					window.setText(player2.getName() + " choose country to place reinforcements for: " + players[turn].getName());
					window.addText("\nYou will only be able to type in a name if it has the correct owner.");
				}
				if (turn == 0 && player1Turn == false || turn == 1 && player1Turn == true){
					turn = (turn + 1) % 6;
				}
				else{
					Player player = players[turn];
					if (trees[turn] == null )
						
						trees[turn]=window.enableOracleAndReturnTree( world, player );
					else window.enableOracle(trees[turn]);
					String str ;
					while ( (str = window.getCommand() ).length() < 4){
						window.addTextln("Mnewake sure input is unambiguous");
					};
					setReinforcements( str , turn);
					turn = (turn + 1) % 6;
				}
			}
			player1Turn = !player1Turn;
		}
		
	}

	/**
	 * Gets names from text input box, and creates the players
	 */

	public static void getNames() {
		window.setText("Welcome! What is player 1's name?");
		player1Name = window.getCommand();	
		window.setText(player1Name + " will be blue.\n\nWhat is player 2's name?");
		player2Name = window.getCommand();
		window.setText(player2Name + " will be red."); 
		return;
	}


	/**
	 *  Create players from the names each player input, then hardcode neutral players
	 */
	public static void createPlayers(){
		player1 = new Player(player1Name, new Color(0, 0, 180));
		World.getPlayers().add(player1);
		player2 = new Player(player2Name, new Color(210, 0, 0));
		World.getPlayers().add(player2);
		neut1 = new Player("Neutral 1", Color.magenta);
		World.getPlayers().add(neut1);
		neut2 = new Player("Neutral 2", new Color(0, 140, 0));
		World.getPlayers().add(neut2);
		neut3 = new Player("Neutral 3", Color.gray);
		World.getPlayers().add(neut3);
		neut4 = new Player("Neutral 4", Color.BLACK);
		World.getPlayers().add(neut4);
	}

	/**
	 *	Ration out states, give each player 9 and each neutral 6, decided by territory cards. 
	 **/

	public static void assignStates(){

		TerritoryDeck Deck = new TerritoryDeck();

		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		window.setText("It's time to draw territory cards. Press enter when ready.");
		String test = window.getCommand();
		window.resetText();

		Player tempPlayer=null;

		for (int i=0; i<world.getStates().size(); i++) {

			TerritoryCard temp=Deck.drawTerritoryCard();

			if(i < Constants.INIT_COUNTRIES_PLAYER){ 
				world.getState(temp.getIndex()).setOwner(player1);
			}
			else if(i < 2*Constants.INIT_COUNTRIES_PLAYER){ 
				world.getState(temp.getIndex()).setOwner(player2); 
			}
			else if(i < Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				world.getState(temp.getIndex()).setOwner(neut1);
			}
			else if(i < 2*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				world.getState(temp.getIndex()).setOwner(neut2);
			}
			else if(i < 3*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				world.getState(temp.getIndex()).setOwner(neut3);
			}
			else if(i < 4*Constants.INIT_COUNTRIES_NEUTRAL + 2*Constants.INIT_COUNTRIES_PLAYER ){ 
				world.getState(temp.getIndex()).setOwner(neut4);
			}
			world.getState(temp.getIndex()).setArmy(1);
			world.getState(temp.getIndex()).getOwner().setNumArmies(world.getState(temp.getIndex()).getOwner().getNumArmies()-1);

			if (world.getState(temp.getIndex()).getOwner() != tempPlayer ) {
				if (tempPlayer == null) {
					window.addText(world.getState(temp.getIndex()).getOwner().getName());
				}
				else {
					window.addTextln(world.getState(temp.getIndex()).getOwner().getName());
				}

				window.addText(" has drawn: "+world.getState(temp.getIndex()).getName());				
				tempPlayer = world.getState(temp.getIndex()).getOwner();
			}
			else {
				window.addText(", "+world.getState(temp.getIndex()).getName());
			}



			window.refreshMap();
			if (test.equals("test")){
			}
			else{

				try {
					MapRenderer.Invalidate();
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
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
		int player1Roll;
		int player2Roll;
		window.setText("Press enter to Roll, " + player1Name + "!");
		window.getCommand();
		player1Roll = diceRollNumber( player1 );
		window.setText("Press enter to Roll, " + player2Name + "!");
		window.getCommand();
		player2Roll = diceRollNumber( player2 );
		if (player1Roll > player2Roll){
			window.setText(player1Name + " will go first");
			turn = 0;
		}
		else if(player2Roll > player1Roll){
			window.setText(player2Name + " will go first");
			turn = 1;
		}
		else{
			window.setText("tie! we'll roll again! Press enter when ready");
			window.getCommand();
			rollTheDiceToStart();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

	public static int diceRollNumber(Player player){
		double rand = Math.random();
		int returnVal = 1 + (int)(rand * 6);
		window.getUiFrame().startAnimation(new Rolling(window.getUiFrame(),new int[][]{ 
			new int[] { returnVal }
		}, new Player[]{
				player
		}));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return returnVal;

	}

	public static boolean setReinforcements (String stateName, int player){
		System.out.println(stateName);
		boolean returnVal = false;
		for (State state : world.getStates()){
			if (stateName.toLowerCase().equals(state.getName().toLowerCase())){
				if(player <  2){
					state.setArmy(state.getArmy() + 3);
				}
				else{
					state.setArmy(state.getArmy() + 1);
				}
				returnVal =  true;
			}
		}
		window.refreshMap();
		return returnVal;
	}
}