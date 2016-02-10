package core.entities;

import java.util.*;

import core.Constants;
import gui.map.GraphDrawer;

abstract public class World {
	final public static int VIRTUAL_WIDTH = Constants.FRAME_WIDTH;
	final public static int VIRTUAL_HEIGHT = Constants.FRAME_HEIGHT;

	private boolean invalidated = true;
	protected List<State> states;
	protected List<Continent> continents;
	
	public World (){ init(); }
	
	abstract protected void init();
	
	public List<Continent> getContinents(){
		return continents;
	}

	public List<State> getStates(){
		return states;
	}
	
	public State getState( int index ){
		return states.get(index);
	}
	
	public void invalidate(){ invalidated=true; }
	public boolean invalidated(){ return invalidated; }
	public void reset_invalidate(){ invalidated = false; }
}
