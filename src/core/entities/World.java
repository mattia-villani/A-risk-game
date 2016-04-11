/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.util.*;
import core.Constants;

abstract public class World {
	/**
	 * ABSTRACT : read core.WorldBuilder.java header to read why.
	 * 
	 * This class represent the whole world. It contains all the informations that describe it.
	 * This class is unmodificable since there is no reason it should change during the game.
	 * It offers the possibility to memorized (used for rendering purpose) if there have been changes in the world (like for example states's ownership)
	 * WORNING : for the getters, if they are not stored lie unmodifiables, then they may be expose the internal rapresentation of the world, since the values returned are not copies
	 */	

	final static private boolean verbose = true;

	/** VIRTUAL_WIDTH/HEIGHT are the size of the rendered world. This is the referiment for the coords of the states */
	final public static int VIRTUAL_WIDTH = Constants.FRAME_WIDTH;
	final public static int VIRTUAL_HEIGHT = Constants.FRAME_HEIGHT;

	/** WORNING: for the lists the positions MUST be the same of the indexs of the object listed. */
	/** list of states in the world... */
	protected List<State> states;
	/** list of continents in the world... */
	protected List<Continent> continents;
	/** list of players in the world ... */
	private static ArrayList <Player> players = new ArrayList<Player>();
	private static TerritoryDeck deck = new TerritoryDeck(true);
	public static int GoldenCav=0;
	/** flag that says if something changed in the world ( this will be used by the renderer )*/
	private boolean invalidated = true;

	/**
	 * <p>	Constructor. It just calls the init method 
	 */
	public World (){ init(); }

	/**
	 * <p>	Initializer method, it must be implemented by the inheriting class 
	 */
	abstract protected void init();

	/**
	 * <p>	Getter. 
	 * 		@return the list of continents.
	 */
	public List<Continent> getContinents(){
		return continents;
	}

	/**
	 * <p>	Getter
	 * 		@return the list of states
	 */
	public List<State> getStates(){
		return states;
	}

	/**
	 * <p> 	Getter
	 * <br>	WARNING: no checks over index are done, so it may return an error
	 * 		@param index is the index in the list. if the warning is respected by the initializator, it will be the same of the state.index
	 * 		@return the state at the index position
	 */
	public State getState( int index ){
		return states.get(index);
	}

	/**
	 * <p>	Acquires a state by name
	 * 		@param string the name of the state
	 * 		@return the state
	 */
	public State getStateByName(String string){
		for (State state : getStates()){
			if (string.toLowerCase().equals(state.getName().toLowerCase())){
				return state;
			}
		}
		return null;
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static void setPlayers(ArrayList<Player> players) {
		World.players = players;
	}

	public static boolean givePlayerCard(Player player){

		if (player.getHand().size() == 5) return false;


		player.addToHand(deck.drawTerritoryCard());

		return true;

	}

	public static int returnCardsToDeck(Player player, String types){

		if (player.getHand().size() < 3 ) return 0;

		if (player.removeHand(types, deck)) return getValue(types);
		else  return 0;

	}
	public static int getValue(String types){
		
		/*
		int[] counts = new int[4]; //A, C, I, W
		 
		for (int i = 0; i < types.length(); ++i){
			if (types.charAt(i) == 'a') counts[0]++;
			if (types.charAt(i) == 'c') counts[1]++;
			if (types.charAt(i) == 'i') counts[2]++;
			if (types.charAt(i) == 'w') counts[3]++;
		}
		if (counts[0] > 1) return 8;
		if (counts[1] > 1) return 6;
		if (counts[2] > 1) return 4;
		else return 10;
		*/
		int value=0;
		if (GoldenCav<=3) value=4+(2*GoldenCav);
		if (GoldenCav>3) value=10+(5*(GoldenCav-3));
		GoldenCav++;
		return value;
		
		
		
	}


}
