package oracle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import core.entities.Player;
import core.entities.State;
import core.entities.World;

public class Oracle extends Tree{
	
	public Oracle ( Collection<String> strings ){
		super(strings);
	}
	
	public void add( String string ){
		super.add(string.toLowerCase());
	}
	
	public Tree evalue( String string ){
		return super.evalue(string.toLowerCase());
	}
	
	static public Tree GenerateOracleTree(World world, Player player){
		List<String> legalStrings = new ArrayList<String>();
		
		String prefix = "attack ";
		for ( State state : world.getStates() )
			if ( state.getOwner() == player )
				for ( int index : state.getAdjacent() )
					legalStrings.add( prefix + world.getState(index).getName() );
		
		return new Oracle(legalStrings);
	}
	
}
