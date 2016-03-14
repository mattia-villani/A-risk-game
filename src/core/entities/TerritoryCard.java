/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.awt.Image;
import java.util.List;

import javax.imageio.ImageIO;

public class TerritoryCard {
	
	static Image[] images;
	
	public static Image getImage(int index){
		return images[index];
	}
	public static void loadImages (Class clazz, List<State> states) {
		if ( images != null ) return;
		int numOfJolly = 2;
		images = new Image[states.size()+numOfJolly];
		int i=0;
		for ( State state: states )
			try {
				images[i++] = ImageIO.read(clazz.getResourceAsStream("/images/cards/"+state.getName()+".png"));
		    } catch (Exception e) {
		        e.printStackTrace();
		    }		
		for ( int j=0; j<numOfJolly; j++ )
			try {
				images[i++] = ImageIO.read(clazz.getResourceAsStream("/images/cards/Jolly.png"));
		    } catch (Exception e) {
		        e.printStackTrace();
		    }		
	}
	
	
	private int index;
	private CardType cardType;
	
	public Image getImage(){
		return images[index];
	}
	
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
		return;
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
