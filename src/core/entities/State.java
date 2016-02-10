package core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Constants;

abstract public class State {
	
	protected int index;
	protected String name;
	protected int x,y;
	protected int[] adjacent;
	protected List<State> smaller_adjacent;
	protected Continent continent;
	
	public State (int i){ init(i); }
	abstract protected void init(int i);

	public int getX(){ return x; }
	public int getY(){ return y; }
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
}
