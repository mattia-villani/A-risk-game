/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.util.ArrayList;
import java.util.Collections;

public class TerritoryDeck {
	
	private ArrayList<TerritoryCard> Deck;
	private int top = 0;
	
	
	public TerritoryDeck() {
		Deck = new ArrayList<TerritoryCard>(42);
		
		for (int i=0; i<42; i++){
			Deck.add(new TerritoryCard(i));
		}
		
		Collections.shuffle(Deck);
	}
	
	public ArrayList<TerritoryCard> getTerritoryDeck(){
		return Deck;
	}
	
	public int getTop(){
		return top;
	}
	
	public void shuffle(){
		Collections.shuffle(Deck);
	}

	public void addTerritoryCard(int index){
		Deck.add(new TerritoryCard (index));
	}
	
	public TerritoryCard drawTerritoryCard(){
		TerritoryCard topTerritoryCard = Deck.get(top);
		top++;
		return topTerritoryCard;	
	}	
}
