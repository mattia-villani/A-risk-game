package oracle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.sun.glass.ui.Window;

import core.entities.Player;
import core.entities.State;
import core.entities.World;
import gui.TextArea;

public class Oracle extends Tree{
	
	private String instruction = null;
	
	@Override
	public String getInstruction(){
		return instruction;
	}
	
	public Oracle ( Collection<String> strings, String instruction ){
		super(strings);
		this.instruction = instruction;
	}
	
	public void add( String string ){
		super.add(string.toLowerCase());
	}
	
	public Tree evalue( String string ){
		return super.evalue(string.toLowerCase());
	}
	
	static public Tree GenerateOracleTreeForAttacking(World world, Player player){
		List<String> legalStrings = new ArrayList<String>();
				
		boolean first = true;
		String prefix = "attack ";
		String inst = "type \""+prefix+"<";
		for ( State state : world.getStates() )
			if ( state.getOwner() == player )
				for ( int index : state.getAdjacent() ){
					inst+=(first?"":"|")+world.getState(index).getName();
					first = false;
					legalStrings.add( prefix + world.getState(index).getName() );
				}
		inst +=">\"";
		return new Oracle(legalStrings, inst );
	}

	static public Tree GenerateOracleTreeForIncreasingArmy(World world, Player player, TextArea textArea){
		List<String> legalStrings = new ArrayList<String>();
				
		boolean first = true;
		String prefix = "";
		String inst = "enter the name of the country to reinforce \""+prefix+"<";
		for ( State state : world.getStates() )
			if ( player == state.getOwner() ){
				inst+=(first?"":"|")+state.getName();
				first = false;
				legalStrings.add( prefix + state.getName() );
			}
		inst +=">\"";
		return new Oracle(legalStrings, inst );
	}

}
