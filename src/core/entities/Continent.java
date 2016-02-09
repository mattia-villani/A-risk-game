package core.entities;

import java.util.List;

abstract public class Continent {

	protected String name;
	protected int index;
	protected int value;
	protected List<State> states;
	protected String color;

	public Continent (int i){ init(i); }
	abstract protected void init(int i);
	
}
