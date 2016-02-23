/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

abstract public class Continent implements Iterable<State>{
	/**
	 * ABSTRACT : read core.WorldBuilder.java header to read why.
	 * 
	 * This class represent a continent. It contains all the informations that describe it.
	 * This class is unmodificable since there is no reason it should change during the game.
	 * The iteration is performed over the states belonging to this country.
	 */	
	/**
	 * name is the name of the country. 
	 */
	protected String name;
	/**
	 * Unique identifier of this country
	 */
	protected int index;
	/**
	 * ?? just saved.
	 */
	protected int value;
	/**
	 * List of states that are in the country. The relation Country - State is of the type 1-N (should have a ref to the country)
	 * WARNING: no checks are ever performed to see if for each i in [0,states.lenght()) : this.states.get(i).country == this
	 */
	protected List<State> states;
	/**
	 * Color that identify the continent.
	 */
	protected Color color;

	/** 
	 * Constructor, it just call the must-to-inherit method init passing the index.
	 * @param i index of the country (id)
	 */
	public Continent (int i){ index=i; init(i); }
	/**
	 * Initializator that must be implemented by the hinheriting class
	 * @param i is the index of the country
	 */
	abstract protected void init(int i);
	
	/**
	 * Fancy getter of the list of states that are in the continent
	 * @return the iterator over such list.
	 */
	@Override
	public Iterator<State> iterator() {
		return states.iterator();
	}
	
	/**
	 * Getter
	 * @return the color of the country
	 */
	public Color getColor(){
		return color;
	}
	
}
