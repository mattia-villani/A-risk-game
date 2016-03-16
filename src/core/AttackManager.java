package core;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.entities.Player;
import core.entities.State;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.GUI;
import gui.Notification;
import jdk.nashorn.internal.ir.SetSplitState;
import oracle.Oracle;

public class AttackManager {
	// this MUST be kept updated
	static Map<Player, Set<State>> storedStatesThatAreAbleToAttack = new HashMap<>();
	static Map<Player, Set<State>> storedStatesThatAdjacentToPlayerOwnerships = new HashMap<>();
	static Map<State, Set<State>> storedEnemyStatesThatAreAdjacentToState = new HashMap<>();

	static public class ChangeOfMindException extends RuntimeException {}
	// shouldn't be possible to throw but a break operator will be introduced during the attack phases
	static public class OutOfContextException extends RuntimeException {
		public OutOfContextException(String message){super(message);}
	}
	public static abstract class Question<T>{
		static final public Set<Boolean> trueFalseSet = new HashSet<>();
		static final public Set<Integer> oneSet = new HashSet<>();
		static final public Set<Integer> twoSet = new HashSet<>();
		static final public Set<Integer> threeSet = new HashSet<>();
		static {
			trueFalseSet.add(true);
			trueFalseSet.add(false);
			oneSet.add(1);
			twoSet.add(1);
			twoSet.add(2);
			threeSet.add(1);
			threeSet.add(2);
			threeSet.add(3);
		}
		abstract public T askQuestion(Set<T> context);
		abstract public String outOfContextMessage();
		final public T notTrivialValidatedAskQuestion(Set<T> context) throws OutOfContextException, RuntimeException{
			if ( context.size() == 0 ) throw new RuntimeException("Something is wrong: this set shouldn't be empty");
			if ( context.size() == 1 ) return context.iterator().next();
			return validatedAskQuestion(context);
		}
		final public T validatedAskQuestion(Set<T> context) throws OutOfContextException{
			T result = askQuestion(context);
			if ( context.contains(result) == false ) 
				throw new OutOfContextException( outOfContextMessage() );
			return result;
		}
	}
	
	
	private Player player;
	private World world;

	public AttackManager(Player player, World world){
		this.player = player;
		this.world = world;
	}
	
	public boolean isPlayerAbleToAttack(){
		return this.getStatesAbleToAttack().size()!=0;
	}
	
	public <T> Set<T> intersect(Set<T> a, Set<T> b){
		Set<T> result = new HashSet<T>();
		Set<T> smaller = a, bigger = b;
		if ( a.size()>b.size() ){
			smaller = b;
			bigger = a;
		}
		for ( T t : smaller )
			if ( bigger.contains(t) )
				result.add(t);
		return result;
	}
	
	public Set<Integer> getSetOfArmyAmounts(State state, int max, int decreaseOf){
		final int MAX = 3 ; 
		assert max>=1 && max<=MAX : "Invalid max number" ;
		int n = state.getArmy() - decreaseOf; 
		if ( n == 1 ) return Question.oneSet;
		if ( n == 2 ) return Question.twoSet;
		if ( n == 3 ) return Question.threeSet;		
		throw new RuntimeException("Controls are wrong");
	}
	
	public void invalidateStores(State state){
		AttackManager.storedStatesThatAdjacentToPlayerOwnerships.remove(state.getOwner());
		AttackManager.storedStatesThatAreAbleToAttack.remove(state.getOwner());
		AttackManager.storedEnemyStatesThatAreAdjacentToState.remove(state);
	}
	
	public void conquerContry( State from, State to, int with ){
		to.setOwner( from.getOwner() );
		to.setArmy( with );
		invalidateStores(from);
		invalidateStores(to);
	}
	
	public void performAttackFromStateWithArmyToState( State from, int with, State to, int defending ){
		assert with != 0 && defending != 0 : "There should be some army fighting";
		int originWith = with , originDefending = defending;
		int[] attackingDices = new int[with];
		int[] defendingDices = new int[defending];
		int i,j;
		for ( i=0; i<with; i++ ) attackingDices[i] = (int) (1+Math.random()*6);
		for ( j=0; j<defending; j++ ) defendingDices[j] = (int) (1+Math.random()*6);
		Arrays.sort(attackingDices);
		Arrays.sort(defendingDices);
		// fight
		while ( (--i)>=0 && (--j)>=0 && with!=0 && defending!=0 )
			if ( attackingDices[i]>defendingDices[j] ) defending--;
			else with--;
		// interpretating the results.
		if ( defending == 0 ){
			conquerContry( from, to, with );
			new Notification(FancyFullFrameAnimation.frame, player.getName()+" conquers "+to.getArmy(), player, Notification.SHORT);
		} else {
			from.updateArmyWithVariation( -(originWith-with) ); // remove dead armies
			to.updateArmyWithVariation( -(originDefending-defending) ); // remove dead armies
		}
	}
	
	public void attackLoop(
			Question<State> stateToAttackQuestion, 
			Question<State> stateToAttackFromQuestion,
			Question<Integer> numberOfArmyToAttackWithQuestion,
			Question<Integer> numberOfArmyToDefendWithQuestion,
			Question<Boolean> keepAttackingTheSameStateFromTheSameStateQuestion){
		boolean keepAttacking = true;
		boolean keepAttackingTheSameStateFromTheSameState;
		while(keepAttacking)
			do{
				keepAttackingTheSameStateFromTheSameState = false;
				try{
					Set<State> stateWhichMayBeAttacked = this.getAttackableStates();
					State stateToAttack = stateToAttackQuestion.validatedAskQuestion(stateWhichMayBeAttacked);
					State attackingState = stateToAttackFromQuestion.notTrivialValidatedAskQuestion(
							// possible attacking states
							intersect( this.getStatesAbleToAttack(), this.getAdjacentEnemyStatesFromState(stateToAttack) )
							);
					Integer attackingAmount = numberOfArmyToAttackWithQuestion.notTrivialValidatedAskQuestion( 
							this.getSetOfArmyAmounts(attackingState, 3, 1) );
					Integer defendingAmount = numberOfArmyToAttackWithQuestion.notTrivialValidatedAskQuestion( 
							this.getSetOfArmyAmounts(stateToAttack, 2, 0) );
					performAttackFromStateWithArmyToState( attackingState, attackingAmount, stateToAttack, defendingAmount );
					if ( this.getStatesAbleToAttack().contains(attackingState) && !stateToAttack.getOwner().equals(player) ) 
						keepAttacking = keepAttackingTheSameStateFromTheSameStateQuestion.askQuestion(Question.trueFalseSet);
				}catch(ChangeOfMindException e){
					keepAttacking = false;
				}catch(OutOfContextException e){
				}finally{
					if ( keepAttacking )
						keepAttacking = isPlayerAbleToAttack();
				}
			}while ( keepAttackingTheSameStateFromTheSameState );
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
		if ( ! storedEnemyStatesThatAreAdjacentToState.containsKey(state) ){
			Set<State> set = new HashSet<>();
			for ( int index : state.getAdjacent() )
				if ( world.getState(index).getOwner().equals(state.getOwner()) == false )
					set.add( world.getState(index) );
			storedEnemyStatesThatAreAdjacentToState.put(state, set);
		}
		return storedEnemyStatesThatAreAdjacentToState.get(state);		
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
