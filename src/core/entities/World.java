package core.entities;
/**
 * @author Mattia Villani
 */
import java.util.*;

import core.Constants;

abstract public class World {
	/**
	 * ABSTRACT : read core.WorldBuilder.java header to read why.
	 * 
	 * This class represent the whole world. It contains all the informations that describe it.
	 * This class is unmodificable (but invalidate attribute) since there is no reason it should change during the game.
	 * It offers the possibility to memorized (used for rendering purpose) if there have been changes in the world (like for example states's ownership)
	 * WORNING : for the getters, if they are not stored lie unmodifiables, then they may be expose the internal rapresentation of the world, since the values returned are not copies
	 */	

	/** VIRTUAL_WIDTH/HEIGHT are the size of the rendered world. This is the referiment for the coords of the states */
	final public static int VIRTUAL_WIDTH = Constants.FRAME_WIDTH;
	final public static int VIRTUAL_HEIGHT = Constants.FRAME_HEIGHT;

	/** WORNING: for the lists the positions MUST be the same of the indexs of the object listed. */
	/** list of states in the world... */
	protected List<State> states;
	/** list of continents in the world... */
	protected List<Continent> continents;
	
	/** flag that says if something changed in the world ( this will be used by the renderer )*/
	private boolean invalidated = true;
	
	/**
	 * Constructor. It just call the init method 
	 */
	public World (){ init(); }
	
	/**
	 * Initializer method, it must be inplmented by the inherinting class 
	 */
	abstract protected void init();
	
	/**
	 * Getter. 
	 * @return the list of continents.
	 */
	public List<Continent> getContinents(){
		return continents;
	}

	/**
	 * Getter
	 * @return the list of states
	 */
	public List<State> getStates(){
		return states;
	}
	
	/**
	 * Getter
	 * WORNING: no checks over index are done, so it may return an error
	 * @param index is the index in the list. if the worning is respected by the initializator, it will be the same of the state.index
	 * @return the state at the index position
	 */
	public State getState( int index ){
		return states.get(index);
	}
	
	/**
	 * Setter : set invalidated to true
	 */
	public void invalidate(){ invalidated=true; }
	/**
	 * Getter
	 * @return true iff invalidated is true
	 */
	public boolean invalidated(){ return invalidated; }
	/**
	 * Setter : set invalidated to false
	 */
	public void reset_invalidate(){ invalidated = false; }
}
