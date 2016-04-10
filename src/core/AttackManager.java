package core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import core.entities.Player;
import core.entities.Question;
import core.entities.Question.*;
import core.entities.QuestionsForGenericPlayer;
import core.entities.State;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.Notification;
import gui.Rolling;
import gui.Toast;

public class AttackManager {
	static final private boolean TEST_LUCKY = false;	
		
	private Player player;
	private World world;
	private List<Player> losers = null;

	public AttackManager(Player player, World world){
		this.player = player;
		this.world = world;
	}
	
	synchronized public boolean isPlayerAbleToAttack(){
		return this.getStatesAbleToAttack().size()!=0;
	}
	
	synchronized public <T> Set<T> intersect(Set<T> a, Set<T> b){
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
	
	synchronized public Set<Integer> getSetOfArmyAmounts(State state, int max, int decreaseOf){
		final int MAX = 3 ; 
		assert max>=1 && max<=MAX : "Invalid max number" ;
		int n = state.getArmy() - decreaseOf; 
		n = Math.min( n , max );
		if ( n == 1 ) return Question.oneSet;
		if ( n == 2 ) return Question.twoSet;
		if ( n == 3 ) return Question.threeSet;
		throw new RuntimeException("Controls are wrong");
	}
	
	synchronized public Set<Integer> getSetOfRangeNumberWithMax(int max){
		Set<Integer> set = new HashSet<>();
		for (int i=0;i<=max;i++) set.add(i);
		return set;
	}
	
	synchronized public void conquerContry( State from, State to, int with, Question<Integer> unitToMoveForFortifingAfterAttackQuestion ){
		from.getOwner().decreaseAndGetNumOfState(-1);
		boolean hardExit = false;
		if ( losers == null ) losers = new LinkedList<Player>();
		if ( to.getOwner().decreaseAndGetNumOfState(1) == 0 ){
			losers.add(to.getOwner());
			new Notification(FancyFullFrameAnimation.frame, to.getOwner().getName()+" lost", to.getOwner(), Notification.SHORT);
			if ( Main.isPlayerHuman(to.getOwner()) ) hardExit = true;
		}
		to.setOwner( from.getOwner() );
		to.setArmy( with );
		from.updateArmyWithVariation(-with);

		if ( hardExit ) throw new EndOfGameException();
		else{// fortification
			int amountToMove = unitToMoveForFortifingAfterAttackQuestion.notTrivialValidatedAskQuestion(getSetOfRangeNumberWithMax(from.getArmy()-1), "Do you want to fortify the "+to+" moving army from "+from);
			to.updateArmyWithVariation(amountToMove);
			from.updateArmyWithVariation(-amountToMove);
		}
	}
	
	synchronized public void performAttackFromStateWithArmyToState( State from, int with, State to, int defending, Question<Integer> unitToMoveForFortifingAfterAttackQuestion ){
		assert with != 0 && defending != 0 : "There should be some army fighting";
		int originWith = with , originDefending = defending;
		int[] attackingDices = new int[with];
		int[] defendingDices = new int[defending];
		int i,j;
		for ( i=0; i<with; i++ ) attackingDices[i] = TEST_LUCKY ? 6 : (int) (1+Math.random()*6) ;
		for ( j=0; j<defending; j++ ) defendingDices[j] = TEST_LUCKY ? 1 : (int) (1+Math.random()*6);

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
			new Toast( from.getOwner().getName()+" conquers "+to.getName(), Notification.SHORT);
			conquerContry( from, to, with, unitToMoveForFortifingAfterAttackQuestion );
		}
	}
	
	// loosers
	synchronized public List<Player> attackLoop( QuestionsForGenericPlayer questions ){
		boolean keepAttacking = true;
		boolean keepAttackingTheSameStateFromTheSameState;
		losers = null; 
		while(keepAttacking)
			try{
				Set<State> stateWhichMayBeAttacked = this.getAttackableStates();
				State stateToAttack = questions.getStateToAttackQuestion().validatedAskQuestion(stateWhichMayBeAttacked,player.getName()+", choose the country you would like to attack, or type 'skip' to end your attack phase.");
				State attackingState = questions.getStateToAttackFromQuestion().notTrivialValidatedAskQuestion(
						// possible attacking states
						intersect( this.getStatesAbleToAttack(), this.getAdjacentEnemyStatesFromState(stateToAttack) ),
						player.getName()+", choose the country you would like to attack "+stateToAttack.getName()+" with"
						);
				do{
					keepAttackingTheSameStateFromTheSameState = false;
					Integer attackingAmount = questions.getNumberOfArmyToAttackWithQuestion().notTrivialValidatedAskQuestion( 
							this.getSetOfArmyAmounts(attackingState, 3, 1), 
							player.getName()+", choose how many units you wish to attack "+stateToAttack.getName()+" with from "+attackingState.getName());
					Integer defendingAmount = questions.getNumberOfArmyToAttackWithQuestion().notTrivialValidatedAskQuestion( 
							this.getSetOfArmyAmounts(stateToAttack, 2, 0),
							stateToAttack.getOwner().getName()+", choose with how many units from "+stateToAttack.getName()+" you wish defend with against the attack from "+attackingState.getName());
					performAttackFromStateWithArmyToState( attackingState, attackingAmount, stateToAttack, defendingAmount, questions.getUnitToMoveForFortifingAfterAttackQuestion() );
					if ( this.getStatesAbleToAttack().contains(attackingState) && !stateToAttack.getOwner().equals(player) ) 
						keepAttackingTheSameStateFromTheSameState = questions.getKeepAttackingTheSameStateFromTheSameStateQuestion().askQuestion(Question.yesNoSet,
								player.getName()+", would you like to keep inviding "+stateToAttack.getName()+" from "+attackingState.getName()
								).toLowerCase().equals(Question.YES.toLowerCase());
				}while ( keepAttackingTheSameStateFromTheSameState );
			}catch(ChangeOfMindException e){
				keepAttacking = false;
			}catch(EndOfGameException e){ 
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
	
	synchronized public Set<State> getAttackableStates(){
		Set<State> states = new HashSet<>();
		for ( State state : getStatesAbleToAttack() )
			states.addAll(getAdjacentEnemyStatesFromState(state));
		return states;
	}
	
	synchronized public Set<State> getStatesAbleToAttack(){
		Set<State> states = new HashSet<>();
		for ( State state : world.getStates() )
			if ( state.getOwner().equals(player) && state.getArmy()>1 )
				states.add(state);
		return states;
	}
	
	synchronized public Set<State> getAdjacentEnemyStatesFromState(State state){
		Set<State> set = new HashSet<>();
		for ( int index : state.getAdjacent() )
			if ( world.getState(index).getOwner().equals(state.getOwner()) == false )
				set.add( world.getState(index) );
		return set;
	}
	
	synchronized public Set<State> getAdjacentStatesFromPlayerOwnerships(){
		Set<State> set = new HashSet<>();
		for ( State state : world.getStates() )
			if ( state.getOwner().equals(player) )
				set.addAll( getAdjacentEnemyStatesFromState(state) );
		return set;
	}

	
}
