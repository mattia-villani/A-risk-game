/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

public class TerritoryCard {
	
	private int index;
	private CardType cardType;
	
	public enum CardType{
		INFANTRY, CAVALRY, ARTILLERY, WILD
	}
	
	/** 
	 * <p>	Territory cards.
	 * 		@param index The country index.
	 * 		@param cardType The type of card. 
	 */
	public TerritoryCard(int index, CardType cardType){
		this.index = index;
		this.cardType = cardType;
	}
	
	/**
	 * <p>	Gets the index (i.e country) of the card.
	 * 		@return An Int to represent the cards country.
	 */
	public int getIndex(){
		return index;
	}
	
	/**
	 * <p>	Gets the cards type.
	 * 		@return The CardType of the card.
	 * <br>	i.e INFANTRY, CAVALRY, ARTILLERY or WILD.
	 */
	public CardType getCardType(){
		return cardType;
	}
}
