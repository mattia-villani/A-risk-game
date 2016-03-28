package core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import core.entities.Player;
import core.entities.State;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.Notification;
import gui.Rolling;
import gui.Toast;
import oracle.Oracle;

public class AttackManager {
	// this MUST be kept updated
	static Map<Player, Set<State>> storedStatesThatAreAbleToAttack = new HashMap<>();
	static Map<Player, Set<State>> storedStatesThatAdjacentToPlayerOwnerships = new HashMap<>();
	static Map<State, Set<State>> storedEnemyStatesThatAreAdjacentToState = new HashMap<>();

	static public class ChangeOfMindException extends RuntimeException { 
		private static final long serialVersionUID = 1L;
		static public String throwCommand = "skip";
	}
	static public class BreakException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		static public String throwCommand = "break";
	}
	// shouldn't be possible to throw but a break operator will be introduced during the attack phases
	static public class OutOfContextException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public OutOfContextException(String message){
			super(message);
			new Toast.ErrorToast(message, Toast.LONG);
		}
	}
	public static abstract class Question<T>{
		static final public String YES = "yes", NO= "no";
		static final public Set<String> yesNoSet = new HashSet<>();
		static final public Set<Integer> oneSet = new HashSet<>();
		static final public Set<Integer> twoSet = new HashSet<>();
		static final public Set<Integer> threeSet = new HashSet<>();
		static {
			yesNoSet.add(YES);
			yesNoSet.add(NO);
			oneSet.add(1);
			twoSet.add(1);
			twoSet.add(2);
			threeSet.add(1);
			threeSet.add(2);
			threeSet.add(3);
		}
		abstract public T askQuestion(Set<T> context, String title) throws OutOfContextException, RuntimeException, BreakException;
		public String outOfContextMessage(){
			return "The value entered is invalid";
		}
		final public T notTrivialValidatedAskQuestion(Set<T> context, String title){
			if ( context.size() == 0 ) throw new RuntimeException("Something is wrong: this set shouldn't be empty");
			if ( context.size() == 1 ) return context.iterator().next();
			return validatedAskQuestion(context,title);
		}
		final public T validatedAskQuestion(Set<T> context,String title) {
			T result = askQuestion(context,title);
			if ( context.contains(result) == false ) 
				throw new OutOfContextException( outOfContextMessage() );
			return result;
		}
		final public Oracle createExtendedOracle(Set<String> context, String prefix){
			List<String> extendedContext = new LinkedList<>();
			extendedContext.addAll(context);
			extendedContext.add(0,ChangeOfMindException.throwCommand);
			extendedContext.add(1,BreakException.throwCommand);
			String options = "";
			for ( String string : extendedContext )
				options = (options.length()>0? options+"|" : "") + string;
			return new Oracle(extendedContext, prefix+"<"+options+">");
		}
		final public String throwExceptionsIfControlsAreUsed(String command){
			if ( ChangeOfMindException.throwCommand.equals(command) ) 
				throw new ChangeOfMindException();
			else if ( BreakException.throwCommand.equals(command) ) 
				throw new BreakException();
			return command; 
		}
	}
	
	static private List<Player> losers = new LinkedList<Player>();
	
	private Player player;
	private World world;

	public AttackManager(Player player, World world){
		losers.clear(); 
		this.player = player;
		this.world = world;
	}
	
	public List<Player> getLosers(){ return losers; }
	
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
		n = Math.min( n , max );
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
		from.getOwner().decreaseAndGetNumOfState(-1);
		if ( to.getOwner().decreaseAndGetNumOfState(1) == 0 ){
			losers.add(to.getOwner());
			new Notification(FancyFullFrameAnimation.frame, to.getOwner().getName()+" lost", to.getOwner(), Notification.SHORT);
		}
		to.setOwner( from.getOwner() );
		to.setArmy( with );
		from.updateArmyWithVariation(-with);
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

		FancyFullFrameAnimation.frame.startAnimation(new Rolling(FancyFullFrameAnimation.frame,new int[][]{ 
			attackingDices, defendingDices
		}, new Player[]{
			from.getOwner(), to.getOwner()
		}), true);

		Arrays.sort(attackingDices);
		Arrays.sort(defendingDices);
		// fight
		while ( (--i)>=0 && (--j)>=0 && with!=0 && defending!=0 )
			if ( attackingDices[i]>defendingDices[j] ){ 
				new Toast("The attacker rolled a "+attackingDices[i]+", which is greater than the defender's roll of "+defendingDices[j]+"... one defending unit has been destroyed", Toast.LONG);
				defending--;
			}
			else{
				new Toast("The attacker rolled a "+attackingDices[i]+", which is NOT greater than the defender's roll of "+defendingDices[j]+"... one attacking unit has been destroyed", Toast.LONG);
				with--;
			}
		// interpretating the results.
		from.updateArmyWithVariation( -(originWith-with) ); // remove dead armies
		to.updateArmyWithVariation( -(originDefending-defending) ); // remove dead armies
		if ( defending == 0 && to.getArmy() == 0){
			conquerContry( from, to, with );
			new Toast( from.getOwner().getName()+" conquers "+to.getArmy(), Notification.SHORT);
		}
	}
	
	// loosers
	public List<Player> attackLoop(
			Question<State> stateToAttackQuestion, 
			Question<State> stateToAttackFromQuestion,
			Question<Integer> numberOfArmyToAttackWithQuestion,
			Question<Integer> numberOfArmyToDefendWithQuestion,
			Question<String> keepAttackingTheSameStateFromTheSameStateQuestion){
		boolean keepAttacking = true;
		boolean keepAttackingTheSameStateFromTheSameState;
		while(keepAttacking)
			try{
				Set<State> stateWhichMayBeAttacked = this.getAttackableStates();
				State stateToAttack = stateToAttackQuestion.validatedAskQuestion(stateWhichMayBeAttacked,player.getName()+", choose the country you would like to attack, or type 'skip' to end your attack phase.");
				State attackingState = stateToAttackFromQuestion.notTrivialValidatedAskQuestion(
						// possible attacking states
						intersect( this.getStatesAbleToAttack(), this.getAdjacentEnemyStatesFromState(stateToAttack) ),
						player.getName()+", choose the country you would like to attack "+stateToAttack.getName()+" with"
						);
				do{
					keepAttackingTheSameStateFromTheSameState = false;
					Integer attackingAmount = numberOfArmyToAttackWithQuestion.notTrivialValidatedAskQuestion( 
							this.getSetOfArmyAmounts(attackingState, 3, 1), 
							player.getName()+", choose how many units you wish to attack "+stateToAttack.getName()+" with from "+attackingState.getName());
					Integer defendingAmount = numberOfArmyToAttackWithQuestion.notTrivialValidatedAskQuestion( 
							this.getSetOfArmyAmounts(stateToAttack, 2, 0),
							stateToAttack.getOwner().getName()+", choose with how many units from "+stateToAttack.getName()+" you wish defend with against the attack from "+attackingState.getName());
					performAttackFromStateWithArmyToState( attackingState, attackingAmount, stateToAttack, defendingAmount );
					if ( this.getStatesAbleToAttack().contains(attackingState) && !stateToAttack.getOwner().equals(player) ) 
						keepAttackingTheSameStateFromTheSameState = keepAttackingTheSameStateFromTheSameStateQuestion.askQuestion(Question.yesNoSet,
								player.getName()+", would you like to keep inviding "+stateToAttack.getName()+" from "+attackingState.getName()
								).toLowerCase().equals(Question.YES.toLowerCase());
				}while ( keepAttackingTheSameStateFromTheSameState );
			}catch(ChangeOfMindException e){
				keepAttacking = false;
			}catch(BreakException e){
			}catch(OutOfContextException e){
			}finally{
				if ( keepAttacking ){
					keepAttacking = isPlayerAbleToAttack();
					if ( ! keepAttacking ) new Toast(player.getName()+" unable to attack", Toast.LONG);
				}
			}
		return losers;
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
