/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.util.Collections;
import java.util.LinkedList;
import core.Constants;

public class TerritoryDeck {
	
	private LinkedList<TerritoryCard> deck;
	
	/**
	 * <p>	Deck of Territory Cards.
	 * <p> 	By default there are 14 cards of each of the three types, Infantry, Cavalry & Artillery.
	 * <br>	The deck may also contain 2 Wild cards.
	 * 
	 * @param wilds If true, 2 wild cards will be added.
	 */
	public TerritoryDeck(boolean wilds) {
		deck = new LinkedList<TerritoryCard>();
		
		for (int i=0; i<Constants.NUM_COUNTRIES; i++){
			deck.add(new TerritoryCard(i));
		}
		
		if (wilds){
			deck.add(new TerritoryCard(42, true));
			deck.add(new TerritoryCard(43, true));
		}
		
		Collections.shuffle(deck);
		return;
	}
	
	/**
	 * <p>	Gets the deck.
	 * 		@return The deck of Territory Cards
	 */
	public LinkedList<TerritoryCard> getTerritoryDeck(){
		return deck;
	}
	
	/**
	 * <p>	Shuffles the deck.
	 */
	public void shuffle(){
		Collections.shuffle(deck);
		return;
	}

	/**
	 * <p>	Adds a new territory card to the deck
	 * 		@param index The country to be added.
	 * 		@param cardType The type of card to be added.
	 */
	public void addNewTerritoryCard(int index){
		deck.add(new TerritoryCard (index));
		return;
	}
	
	public void addNewWildCard(int index, boolean wild){
		deck.add(new TerritoryCard (index, wild));
		return;
	}
	
	/**
	 * <p>	Adds a pre-existing territory card to the deck
	 * 		@param territoryCard the card to be added.
	 */
	public void addTerritoryCard(TerritoryCard territoryCard){
		deck.add(territoryCard);
		return;
	}
	
	/**
	 * <p>	Draws a territory card from the deck.
	 * 		@return the top territory card.
	 */
	public TerritoryCard drawTerritoryCard(){
		return deck.pop();
	}	
}
