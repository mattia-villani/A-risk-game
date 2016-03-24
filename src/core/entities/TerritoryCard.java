/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.awt.Image;
import java.util.List;
import core.Constants;
import javax.imageio.ImageIO;

public class TerritoryCard {
	
	static Image[] images;
	private int index;
	private String cardType;
	private String stateName;
	
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
	
	public Image getImage(){
		return images[index];
	}
	
	/** 
	 * <p>	Territory cards constructor
	 * 		@param index The country index.
	 */
	public TerritoryCard(int index){
		this.index = index;
		this.stateName = Constants.COUNTRY_NAMES[index];
		this.cardType = Constants.CARD_TYPES[index];
		return;
	}
	
	/**
	 * <p>	Wild Territory card constructor
	 * 		@param index The country index.
	 * 		@param wild boolean to decide if it should be a wild card.
	 */
	public TerritoryCard(int index, boolean wild){
		if ( wild==false || index < 42) throw new RuntimeException("Use the other constructor");
		this.index = index;
		this.stateName = "";
		this.cardType = "WILD";
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
	public String getCardType(){
		return cardType;
	}
	
	/**
	 * <p>	Gets the country name.
	 * 		@return The CardType of the card.
	 * <br>	i.e INFANTRY, CAVALRY, ARTILLERY or WILD.
	 */
	public String getStateName(){
		return stateName;
	}
}
