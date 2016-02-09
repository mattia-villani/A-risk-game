package core.entities;

abstract public class State {
	
	protected int index;
	protected String name;
	
	public State (int i){ init(i); }
	
	abstract protected void init(int i);
	
}
