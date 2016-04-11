/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

import core.Constants;
import gui.GUI;

public abstract class Player {
	/**
	 * This class represent a player.
	 */

	/**
	 * Unmodificable attribute name, it is just initialized and represent the name of the player
	 */
	private String name;
	/**
	 * Unmodificable attribute color, it is just initialized and represent the color of the player to be shown in the map
	 */
	private Color color;

	private int numArmies;

	private int numStates;

	private int id;

	private ArrayList<TerritoryCard> hand;

	public int getNumStates() {
		return numStates;
	}

	public void setNumStates(int numStates) {
		this.numStates = numStates;
	}

	/**
	 * Query the behaviour of the player through the questions 
	 */
	abstract public QuestionsForGenericPlayer getQuestions(GUI gui);
	
	/**
	 * Constructor
	 * @param name of the player
	 * @param color to show in the map
	 */
	public Player(int id, String name, Color color){
		this.name=name;
		this.color=color;
		this.numArmies = 0;
		this.id = id;
		this.hand = new ArrayList<TerritoryCard>();
	}

	public int getId(){
		return id;
	}
	@Override
	public int hashCode(){
		return getId();
	}
	public int getNumArmies() {
		return numArmies;
	}

	public void setNumArmies(int numArmies) {
		this.numArmies = numArmies;
	}

	/**
	 * Getter
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter
	 * @return the color of the player
	 */
	public Color getColor() {
		return color;
	}

	public ArrayList<TerritoryCard> getHand() {

		return hand;

	}
	public void addToHand(TerritoryCard added) {

		this.hand.add(added);

	}



	/*

	 * added without wifi 

	 */

	public boolean removeHand (String types, TerritoryDeck deck){

		LinkedList<TerritoryCard> returnList = new LinkedList<TerritoryCard>();

		for (int i = 0; i < types.length(); ++i){

			String typeString = "";

			char type = types.charAt(i);



			if (type == 'i' || type == 'I') typeString = "INFANTRY";

			else if (type == 'c' || type == 'C') typeString = "CAVALRY";

			else if (type == 'a' || type == 'A') typeString = "ARTILLERY"; 

			else typeString = "WILD";



			for (TerritoryCard card : hand){

				if (Constants.CARD_TYPES[card.getIndex()].equals(typeString)){

					returnList.add(card);

					hand.remove(card);

					break;

				}

			}

		}


		return removeHand(returnList, deck);

	}

	/*

	 * added without wifi 

	 */

	public boolean removeHand (LinkedList<TerritoryCard> toDeck, TerritoryDeck deck){

		if (toDeck.size() < 3){

			for (int i = 0; i < toDeck.size(); ++i){

				hand.add(toDeck.pop());

			}

			return false;

		}

		else {

			for (int i = 0; i < toDeck.size(); ++i){

				//deck.addNewTerritoryCard((toDeck.pop()).getIndex());
				toDeck.pop();

			}

			return true;

		}



	}

	public int decreaseAndGetNumOfState(int decreaseOf){
		this.numStates -= decreaseOf;
		return this.numStates;
	}
	@Override
	public String toString(){
		return getName();
	}

}
