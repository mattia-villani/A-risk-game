package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.entities.Player;
import core.entities.State;
import core.entities.World;
import oracle.Oracle;

public class AttackManager {
	// this MUST be kept updated
	static Map<Player, Oracle> storedOracles = new HashMap<>();
	static Map<Player, Set<State>> storedStatesThatAdjacentToPlayerOwnerships = new HashMap<>();
	static Map<State, Set<State>> storedStatesThatAreAdjacentToState = new HashMap<>();

	public class ChangeOfMindException extends RuntimeException {}
	public class OutOfContextException extends RuntimeException {
		public OutOfContextException(String message){super(message);}
	}
	public abstract class Question<T>{
		abstract public T askQuestion(Set<T> context);
		abstract public String outOfContextMessage();
	}
	
	
	private Player player;
	private World world;

	public AttackManager(Player player, World world){
		this.player = player;
		this.world = world;
	}
	
	public void attackLoop(Question<Boolean> keepAttackingQuestion, Question<State> stateToAttackQuestion){
		while ( keepAttackingQuestion.askQuestion(null) ){
			try{
				Set<State> attackableContext = this.getAttackableStates();
				State stateToAttack = stateToAttackQuestion.askQuestion(attackableContext);
				if ( attackableContext.contains(stateToAttack) == false ) 
					throw new OutOfContextException( stateToAttackQuestion.outOfContextMessage() );
				if ( stateToAttack )
			}catch(ChangeOfMindException e){
			}catch(OutOfContextException e){}
		}
	}
	
	public Set<State> getAttackableStates(){
		Set<State> states = new HashSet<>();
		for ( State state : getStatesAbleToAttack() )
			states.addAll(getAdjacentEnemyStatesFromState(state));
		return states;
	}
	
	public Set<State> getStatesAbleToAttack(){
		Set<State> states = new HashSet<>();
		for ( State state : world.getStates() )
			if ( state.getOwner().equals(player) && state.getArmy()>1 )
				states.add(state);
		return states;
	}
	
	public Set<State> getAdjacentEnemyStatesFromState(State state){
		if ( ! storedStatesThatAreAdjacentToState.containsKey(state) ){
			Set<State> set = new HashSet<>();
			for ( int index : state.getAdjacent() )
				if ( world.getState(index).getOwner().equals(state.getOwner()) == false )
					set.add( world.getState(index) );
			storedStatesThatAreAdjacentToState.put(state, set);
		}
		return storedStatesThatAreAdjacentToState.get(state);		
	}
	
	public Set<State> getAdjacentStatesFromPlayerOwnerships(){
		if ( ! storedStatesThatAdjacentToPlayerOwnerships.containsKey(player) ){
			Set<State> set = new HashSet<>();
			for ( State state : world.getStates() )
				if ( state.getOwner().equals(player) )
					set.addAll( getAdjacentEnemyStatesFromState(state) );
			storedStatesThatAdjacentToPlayerOwnerships.put(player, set);
		}
		return storedStatesThatAdjacentToPlayerOwnerships.get(player);
	}

	
}
