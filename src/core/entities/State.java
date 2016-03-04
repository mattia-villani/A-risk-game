/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class State {
	/**
	 * ABSTRACT : read core.WorldBuilder.java header to read why.
	 * 
	 * This class represent a country. It contains all the informations that describe it.
	 * The most of the attributes are unmodifible ( the inizialization and the setting of them is not allowed by this class )
	 * The only attributes which may change over time are the ownership ( it may be conquered ) and the number of army.
	 */
	
	/** modificables */
	/** Owner of this country */
	private Player owner;
	/** Numerosity of the army */
	private int army;

	/** Index representing an ID of the country */
	protected int index;
	/** Just the name of it to be displayed */
	protected String name;
	/** Coord in which the country must be shown. ( the size of the view is not specified here ) */
	protected int x,y;
	/** Indexes of of the countries which this one is attached to */
	protected int[] adjacent;
	/** Subset of the countries referred by adjacent with the property of having their indexes smaller then this.index */
	protected List<State> smaller_adjacent;
	/** Continent which the country belongs to */
	protected Continent continent;
	
	/** 
	 * Constructor, it just call the init method which will be implemented by an hinerring class
	 * @param i is the index (id) of this country
	 */
	public State (int i){ 
		index = i;
		army=0; 
		init(i); 
	}
	
	/**
	 * This method initializes all the property of the state. It will be inherited by a country builder.
	 * @param i is the index of this country
	 */
	abstract protected void init(int i);


	public int[] getAdjacent(){ return adjacent; }

	/**
	 * Getter.
	 * @return the x attribute
	 */
	public int getX(){ return x; }
	/**
	 * Getter.
	 * @return the y attribute
	 */
	public int getY(){ return y; }
	/**
	 * Getter.
	 * @return the name
	 */
	public String getName(){ return name; }
	/**
	 * Getter.
	 * @return the continent attribute
	 */
	public Continent getContinet(){ return continent; }
	/**
	 * Getter & modifier: it returns the smallerAdjacent list. It creates and saves that if not already created.
	 * @param world is the world from which recover the states
	 * @return the list of the states which have a smaller index and that are adjacent
	 */
	public List<State> getSmallerAdjacent(World world){
		// in case it is already created, nothing else must be done
		if ( smaller_adjacent != null ) return smaller_adjacent;
		// otherwise it create the list
		List<State> l = new ArrayList<>();
		for (int i=0;i<adjacent.length;i++)
			// for each adjacent country which has a smaller index
			if ( adjacent[i] < index ) 
				// the country is got from the world by index and stored in the list
				l.add(world.getState( adjacent[i] ));
		/* since the list is stored in the internal state of this country to be provided next, 
		 * it can't be possible to modify it from the out of the class */
		smaller_adjacent = Collections.unmodifiableList(l);
		return smaller_adjacent;
	}
	/**
	 * Getter.
	 * @return the owner attribute
	 */
	public Player getOwner() { return owner; }
	/**
	 * Setter. Since the country may be conquered
	 * WARNING: no checks are performed over the values.
	 * @param owner new owner. 
	 */
	public void setOwner(Player owner) { this.owner = owner; }
	/**
	 * Getter.
	 * @return the army attribute
	 */
	public int getArmy() { return army; }
	/**
	 * Setter. Since the army may vary over time.
	 * WARNING: the 0 value is allowed but it should never be setted due to the risk game rule.
	 * 		& negative values are just asserted so if the asserts are disabled, also the negatives one are not checked.
	 * @param army is the new value to set.
	 */
	public void setArmy(int army) {
		assert army>=0 : "something wired happend: army in a state can't be negative";
		this.army = army;
	}
}
