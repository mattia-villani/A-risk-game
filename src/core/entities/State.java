package core.entities;

abstract public class State {
	
	protected int index;
	protected String name;
	protected int x,y;
	protected int[] adjacent;
	protected Continent continent;
	
	public State (int i){ init(i); }
	
	abstract protected void init(int i);
	
}
