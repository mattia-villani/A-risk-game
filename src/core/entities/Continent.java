package core.entities;

import java.util.Iterator;
import java.util.List;

abstract public class Continent implements Iterable<State>{

	protected String name;
	protected int index;
	protected int value;
	protected List<State> states;
	protected String color;

	public Continent (int i){ init(i); }
	abstract protected void init(int i);
	
	@Override
	public Iterator<State> iterator() {
		return states.iterator();
	}
	
}
