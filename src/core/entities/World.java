package core.entities;

import java.util.*;

abstract public class World {
	
	protected List<State> states;
	protected List<Continent> continents;
	
	public World (){ init(); }
	
	abstract protected void init();
	
}
