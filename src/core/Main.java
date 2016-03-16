/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import core.entities.*;
import gui.DeckDrawer;
import gui.GUI;
import gui.Rolling;
import gui.Toast;
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
	private static World world;
	private static GUI window;
	private static boolean player1Start;

	
	public static void main(String[] args) throws IOException {
		/*
		 * Create world and GUI.
		 */
		world = WorldBuilder.Build();		
		window = new GUI(world);
		new Toast("Loading the pictures", Toast.SHORT);
		Rolling.loadImages( window.getClass() );
		TerritoryCard.loadImages( window.getClass(), world.getStates() );

		/*
		 *  Initial game setup.
		 */
		getNames();
		createPlayers();
		assignArmies();
		assignStates();
		
		for ( State state : world.getStates() )
			state.setArmy( 1 + (int)(Math.random()*3) );
		AttackPhase.performPhace(player1, world, window);
		
		rollTheDiceToStart();
		chooseReinforcements();
		
		/* while loop for turns:
		while (true){
		
			
			checkLosers();
			checkWinners();
			
		}
		*/
		
		
		/*
		 *  Game setup complete, ready to start turns.
		 */
		window.setText("Let's play!");
	}

	// 	Returns the world.
	public static World getWorld() {
		return world;
	}

	/**
	 * <p>	Sleeps thread for specified time.
	 * 		@param miliseconds Amount of time to sleep for in miliseconds
	 */
	public static void sleep(int miliseconds){
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * <p>	Gets the names for the 2 human players.
	 */
	public static void getNames() {
		window.setText("Welcome to Risk! Please enter a name for player 1.");
		window.clearCommands();

		player1Name="";	
		while (player1Name.length()<1){
			player1Name = window.getCommand();
			if (player1Name.length()<1){
				window.setText("Welcome to Risk! Please enter a name for player 1.\n\nYou can't have a blank name. Please pick a name.");
			}
			
		}
		
		window.setText(player1Name + " will be blue. Please enter a name for player 2.");
		
		player2Name="";
		while (player2Name.length()<1){
			player2Name = window.getCommand();
			if (player2Name.length()<1){
				window.setText(player1Name + " will be blue. Please enter a name for player 2.\n\nYou can't have a blank name. Please pick a name.");
			}
		}
		
		window.setText(player2Name + " will be red."); 
		
		sleep(1000);
		window.clearCommands();
		return;
	}
	
	/**
	 * <p>	Creates the players & Displays the player list.
	 * <br>	The 4 neutral player names are hardcoded.
	 */
	public static void createPlayers(){
		player1 = new Player(0,player1Name, new Color(0, 0, 180));
		World.getPlayers().add(player1);
		player2 = new Player(1,player2Name, new Color(210, 0, 0));
		World.getPlayers().add(player2);
		neut1 = new Player(2,"Neutral 1", Color.magenta);
		World.getPlayers().add(neut1);
		neut2 = new Player(3,"Neutral 2", new Color(0, 140, 0));
		World.getPlayers().add(neut2);
		neut3 = new Player(4,"Neutral 3", Color.gray);
		World.getPlayers().add(neut3);
		neut4 = new Player(5,"Neutral 4", Color.BLACK);
		World.getPlayers().add(neut4);
		
		window.displayPlayerList(World.getPlayers());
		return;
	}

	/**
	 * <p>	Assigns the number of armies specified in the constants to each player
	 */
	public static void assignArmies(){
		ArrayList<Player> players = World.getPlayers();
		for(int i = 0; i < players.size(); ++i){
			if (i < players.size() - Constants.NUM_NEUTRALS){
				players.get(i).setNumArmies(Constants.INIT_UNITS_PLAYER);
			}
			else{
				players.get(i).setNumArmies(Constants.INIT_UNITS_NEUTRAL);
			}
		}
		return;
	}
	
	/**
	 * <p>	Assigns all countries to the players.
	 * <br>	Number of countries assigned determined by constants.
	 * <p>	Serial country assignment to each player still provides random countries as card deck is randomized upon creation.
	 */
	public static void assignStates(){

		TerritoryDeck Deck = new TerritoryDeck(false);

		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		window.setText("It's time to draw territory cards. Press enter when ready.");
		String test = window.getCommand();
		window.resetText();

		Player tempPlayer=null;
		int singleTemp = 400;
		int tempo = singleTemp * world.getStates().size() ;
		TerritoryCard[] cards = new TerritoryCard[world.getStates().size()];
		window.getUiFrame().startAnimation(new DeckDrawer(window.getUiFrame(), world, cards,tempo+window.getUiFrame().getTransitionTime()*2, singleTemp, window.getUiFrame()),false);				

		try{
			Thread.sleep(window.getUiFrame().getTransitionTime());
		}catch(Exception e){}
		
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

//			synchronized(cards){
				cards[i] = temp;
//			}
			
			window.refreshMap();
			if (test.equals("test")){
			}
			else{
				try {
					MapRenderer.Invalidate();
					Thread.sleep(singleTemp);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
			
		}
	}
	
	/**
	 * <p>	Dice rolls to determine which player goes first with placing reinforcements
	 */
	public static void rollTheDiceToStart(){
		window.clearCommands();
		window.setText("Press enter to roll, " + player1.getName() + "!");
		window.getCommand();
		int player1Roll = diceRollNumber( player1 );
		
		window.clearCommands();
		window.setText("Press enter to roll, " + player2.getName() + "!");
		window.getCommand();
		int player2Roll = diceRollNumber( player2 );
		
		if (player1Roll > player2Roll){
			window.setText(player1.getName()+" rolled a "+player1Roll+", which beats "+player2.getName()+"'s "+player2Roll+". So "+player1.getName()+" will go first!");
			player1Start=true;
			sleep(5000);
		}
		else if(player2Roll > player1Roll){
			window.setText(player2.getName()+" rolled a "+player2Roll+", which beats "+player1.getName()+"'s "+player1Roll+". So "+player2.getName()+" will go first!");
			player1Start=false;
			sleep(5000);
		}
		else{
			window.setText("Tie! We'll roll again! Press enter when ready.");
			window.getCommand();
			rollTheDiceToStart();
		}
		window.clearCommands();
		return;
	}

	/**
	 * <p>	Handles animation of die rolls.
	 */
	public static int diceRollNumber(Player player){
		double rand = Math.random();
		int returnVal = 1 + (int)(rand * 6);
		window.getUiFrame().startAnimation(new Rolling(window.getUiFrame(),new int[][]{ 
			new int[] { returnVal }
		}, new Player[]{
				player
		}), true);
		return returnVal;
	}
	
	/**
	 * <p>	Reinforcement placing during game setup phase.
	 * <br>	Uses oracle to assist player by predicting country names based upon text entered.
	 */
	public static void chooseReinforcements(){
		
		window.clearCommands();
		window.toggleMouseInput();
		Player[] players = new Player[]{ player2, neut1, neut2, neut3, neut4};
		Tree[] trees = new Tree[5];
		Tree player1Tree=null;
		Tree player2Tree=null;

		if (player1Start) players[0]=player1;
		for (int i = 0; i < Constants.TURNS_OF_REINFORCEMENTS; ++i){
			for (int j=0; j < Constants.NUM_TOTAL_PLAYERS-1; j++ ){
				
				// Display text to the user
				if (j==0) window.setText(players[0].getName()+" please choose one of your countries to reinforce.");
				else window.setText(players[0].getName()+" please choose a country to reinforce for: "+players[j].getName()+".");

				window.addText("\nYou will only be able to type in a name if it has the correct owner.");
				
				Player player = players[j];
				
				// Generate and store oracle trees.
				if (trees[j] == null) trees[j]=window.enableOracleAndReturnTree( world, player );
				else window.enableOracle(trees[j]);
				String command;
				
				// Handling bad text or mouse inputs
				String badText=window.getText()+"\nPlease make sure input is unambiguous and not blank.";
				String badClick=window.getText()+"\nPlease select a country owned by "+players[j].getName()+".";
				if(j==0) badClick=window.getText()+"\nPlease select one of your own countires.";
				while ( ((command = window.getCommand()).length() < 4) || (world.getStateByName(command).getOwner()!=players[j]) ) {				
					if(command.length()<4){
						window.setText(badText);
					}
					else
					{
						window.setText(badClick);
					}
				}
				setReinforcements(command, j);
			}	
			
			// Swap position 0 to the other human player. Also swap the two players trees.
			if(players[0]==player1){
				players[0]=player2;
				player1Tree=trees[0];
				trees[0]=player2Tree;
			}
			else{
				players[0]=player1;
				player2Tree=trees[0];
				trees[0]=player1Tree;
			}
		}		
		window.disableOracle();
		window.clearCommands();
		return;
	}		

	/**
	 * <p>	Sets reinforcements into state.
	 * <br>	Decrements army size of state owner.
	 * 		@param stateName The state to reinforce
	 * 		@param player To determine if player is human or neutral
	 */
	public static void setReinforcements (String stateName, int player){
		for (State state : world.getStates()){
			if (stateName.toLowerCase().equals(state.getName().toLowerCase())){
				if(player == 0){
					state.setArmy(state.getArmy() + 3);
				}
				else{
					state.setArmy(state.getArmy() + 1);
				}
			}
		}
		window.refreshMap();
		return;
		
	}
	

	public static void checkLosers() {
		for (Player player : world.getPlayers() ){
			if (player.getNumStates() == 0){
				world.getPlayers().remove(player);
			}
		}
	}

	public static void checkWinners(){
		if (!world.getPlayers().contains(player1)){
			window.setText(player1.getName() + " is the winner! Thanks for playing!");
		}
		else if (!world.getPlayers().contains(player2)){
			window.setText(player2.getName() + " is the winner! Thanks for playing!");
		}

	}

	public static void moveArmies(){
		boolean done = false;
		
		String giverString = null;
		String getterString = null;
		int numMoved = 0;

		while (!done){
/*			window.setText(currentPlayer.getName() + ", please choose a country to move armies from");
			giverString = window.getCommand();



			window.setText("Now choose a connected country to move armies to");
			getterString = window.getCommand();
			State giver = world.getStateByName(giverString);
			State getter = world.getStateByName(getterString);
			if (isConnected(giver, getter)){
*/				done = true;
/*			}
			else {
				window.setText("Needs to be a country of yours that is connected. Try again.");
				sleep(500);
			}
*/
		}
		
			window.setText("And how many countries do you like moved?");
			done = false;
			while (!done){
				State giver = world.getStateByName(giverString);
				State getter = world.getStateByName(getterString);
				String stringNumMoved = window.getCommand();
				try
				{
					numMoved = Integer.parseInt(stringNumMoved);
					if (numMoved >= giver.getArmy()){
						window.setText("Needs to be a number less " + giver.getArmy());
					}
					else{
						done = true;
					}
				} catch (NumberFormatException ex)
				{
					window.setText("Needs to be a number!");

				}
				getter.setArmy(getter.getArmy() + numMoved);
				giver.setArmy(giver.getArmy() - numMoved);
			}
		

	}
	
	public static boolean isConnected(State start, State end, Player owner){
		for (int state : start.getAdjacent()){
			if (end == world.getState(state)) return true;
			else if (world.getState(state).getOwner() == owner) return isConnected (world.getState(state), end, owner);
		}
		return false;
	}

}