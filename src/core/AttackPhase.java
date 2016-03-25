package core;

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
import gui.Toast;
import oracle.Oracle;

public class AttackPhase {
	static GUI gui;
	static StateQuestion stateQuestion = new StateQuestion();
	static NumberQuestion numberQuestion = new NumberQuestion();
	static YesNoQuestion yesNoQuestion = new YesNoQuestion();
	
	
	static <T> Set<String> createStringSet( Set<T> context ){
		Set<String> ret = new HashSet<String>();
		for ( T t : context )
			ret.add(t.toString());
		return ret;
	}

	static private class StateQuestion extends AttackManager.Question<State>{
		static Map<Set<State>,Oracle> oracles = new HashMap<>() ;
		static final private int sizeBeforeClearing = 20; // otherwise the ram will be wasted
		@Override
		public State askQuestion(Set<State> context, String title) {
			new Toast(title,Toast.LONG);
			if ( oracles.get(context) == null )
				oracles.put(context, createExtendedOracle(createStringSet(context), "Answer with a state name") );
			gui.setText(title);
			gui.enableOracle(oracles.get(context));
			if ( oracles.size()>=sizeBeforeClearing ) oracles.clear(); // free memory
			try{
				String ret = throwExceptionsIfControlsAreUsed(gui.getCommand());
				gui.disableOracle();
				for ( State state : context )
					if ( state.toString().toLowerCase().equals(ret.toLowerCase()) ) 
						return state;
			}catch( Exception e ){
				gui.disableOracle();
				throw e;
			}
			throw new AttackManager.OutOfContextException("Something went wrong");
		}
	}

	
	static private class NumberQuestion extends AttackManager.Question<Integer>{
		static Map<Set<Integer>,Oracle> oracles = new HashMap<>() ;
		@Override
		public Integer askQuestion(Set<Integer> context, String title) {
			new Toast(title,Toast.LONG);
			if ( oracles.get(context) == null )
				oracles.put(context, createExtendedOracle(createStringSet(context), "Answer with a number") );
			gui.setText(title);
			gui.enableOracle(oracles.get(context));
			try{
				String ret = throwExceptionsIfControlsAreUsed(gui.getCommand());
				gui.disableOracle();
				return Integer.valueOf(ret);
			}catch( NumberFormatException e){
				gui.disableOracle();
				throw new AttackManager.OutOfContextException("This should be a number");
			}catch( Exception e ){
				gui.disableOracle();
				throw e;
			}
		}
	}
	
	static private class YesNoQuestion extends AttackManager.Question<String>{
		static Oracle oracle ;
		@Override
		public String askQuestion(Set<String> context, String title) {
			if ( context.equals(AttackManager.Question.yesNoSet) == false )
				throw new AttackManager.OutOfContextException("This question should be yes/no");
			new Toast(title,Toast.LONG);
			if ( oracle == null )
				oracle = createExtendedOracle(context, "Answer with yes or no");
			gui.setText(title);
			gui.enableOracle(oracle);
			try{
				String ret = throwExceptionsIfControlsAreUsed(gui.getCommand());
				gui.disableOracle();
				return ret;
			}catch( Exception e ){
				gui.disableOracle();
				throw e;
			}
		}
	}

	static void performPhase( Player player, World world, GUI gui ){
		AttackPhase.gui = gui;
		gui.toggleMouseInput();
		new Notification(FancyFullFrameAnimation.frame, "Attack Phase", player, Notification.SHORT);
		new AttackManager(player, world).attackLoop(
				stateQuestion, 
				stateQuestion, 
				numberQuestion, 
				numberQuestion, 
				yesNoQuestion);
		new Notification(FancyFullFrameAnimation.frame, "Attack Phase ended", player, Notification.SHORT);
		gui.toggleMouseInput();
		gui.clearCommands();
	}
	
}
