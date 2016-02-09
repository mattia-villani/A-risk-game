package core.entities;

import java.util.*;

abstract public class World {
	
	protected List<State> states;
	
	public World (){ init(); }
	
	abstract protected void init();
	
}
