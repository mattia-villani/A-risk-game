package core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class State {
	
	private Player owner;
	private int army;

	protected int index;
	protected String name;
	protected int x,y;
	protected int[] adjacent;
	protected List<State> smaller_adjacent;
	protected Continent continent;
	
	public State (int i){ 
		army=0; 
		init(i); 
	}
	
	abstract protected void init(int i);

	public int getX(){ return x; }
	public int getY(){ return y; }
	public String getName(){ return name; }
	public Continent getContinet(){ return continent; }
	public List<State> getSmallerAdjacent(World world){
		if ( smaller_adjacent != null ) return smaller_adjacent;
		List<State> l = new ArrayList<>();
		for (int i=0;i<adjacent.length;i++)
			if ( adjacent[i] < index ) 
				l.add(world.getState( adjacent[i] ));
		smaller_adjacent = Collections.unmodifiableList(l);
		return smaller_adjacent;
	}
	
	public Player getOwner() { return owner; }
	public void setOwner(Player owner) { this.owner = owner; }
	public int getArmy() { return army; }
	public void setArmy(int army) {
		assert army>=0 : "something wired happend: army in a state can't be negative";
		this.army = army;
	}

}
