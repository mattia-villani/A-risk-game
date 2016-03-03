/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.util.Collections;
import java.util.LinkedList;
import core.entities.TerritoryCard.CardType;

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
		
		for (int i=0; i<42; i++){
			if (i % 3 == 0)deck.add(new TerritoryCard(i, CardType.INFANTRY));
			if (i % 3 == 1)deck.add(new TerritoryCard(i, CardType.CAVALRY));
			if (i % 3 == 2)deck.add(new TerritoryCard(i, CardType.ARTILLERY));
		}
		
		if (wilds){
			deck.add(new TerritoryCard(43, CardType.WILD));
			deck.add(new TerritoryCard(44, CardType.WILD));
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
	public void addNewTerritoryCard(int index, CardType cardType){
		deck.add(new TerritoryCard (index, cardType));
		return;
	}
	
	/**
	 * <p>	Adds a pre-existing territory card to the deck
	 * 		@param territoryCard the card to be added.
	 */
	public void addTerritoryCard(TerritoryCard territoryCard){
		deck.add(territoryCard);
	}
	
	/**
	 * <p>	Draws a territory card from the deck.
	 * 		@return the top territory card.
	 */
	public TerritoryCard drawTerritoryCard(){
		return deck.pop();
	}	
}
