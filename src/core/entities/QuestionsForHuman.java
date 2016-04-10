package core.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.entities.Question.BreakException;
import core.entities.Question.ChangeOfMindException;
import gui.GUI;
import gui.Toast;
import oracle.Oracle;

public class QuestionsForHuman extends QuestionsForGenericPlayer {
	Question<State> stateQuestion = null;
	Question<Integer> numberQuestion = null;
	Question<String> yesNoQuestion = null;
	ConfQuestion confQuestion = null;
	
	public QuestionsForHuman ( GUI gui ){	super(gui);	}
	
	static <T> Set<String> createStringSet( Set<T> context ){
		Set<String> ret = new HashSet<String>();
		for ( T t : context )
			ret.add(t.toString());
		return ret;
	}
	
	private Question<String> getYesNoQuestion(){
		return ( yesNoQuestion = ( yesNoQuestion!=null?yesNoQuestion:
			new Question<String>(){
				Oracle oracle ;
				@Override
				public String askQuestion(Set<String> context, String title) {
					if ( context.equals(Question.yesNoSet) == false )
						throw new OutOfContextException("This question should be yes/no");
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
		}) );
	}

	private Question<State> getStateQuestion(){
		return ( stateQuestion = ( stateQuestion!=null?stateQuestion:
			new Question<State>(){
				@Override
				public State askQuestion(Set<State> context, String title) {
					new Toast(title,Toast.LONG);
					gui.setText(title);
					gui.enableOracle(createExtendedOracle(createStringSet(context), "Answer with a state name"));
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
					throw new OutOfContextException("Something went wrong");
				}
		}));
	}

	
	private Question<Integer> getNumberQuestion(){
		return ( numberQuestion = ( numberQuestion!=null?numberQuestion:
			new Question<Integer>(){
				Map<Set<Integer>,Oracle> oracles = new HashMap<>() ;
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
						throw new OutOfContextException("This should be a number");
					}catch( Exception e ){
						gui.disableOracle();
						throw e;
					}
				}
		}));
	}
	
	
	private ConfQuestion getStringBasedConfQuestion(){
		return ( confQuestion = ( confQuestion!=null?confQuestion:
			new ConfQuestion(){
				@Override
				public String askQuestion(Set<String> context, String title)
						throws OutOfContextException, RuntimeException, BreakException {
					new Toast(title,Toast.LONG);
					gui.setText(title);
					gui.enableOracle(QuestionsForHuman.createExtendedOracle(context, "Chose a card conf", skipAllowed));
					try{
						String ret = throwExceptionsIfControlsAreUsed(gui.getCommand());
						gui.disableOracle();
						return ret;
					}catch( Exception e ){
						gui.disableOracle();
						throw e;
					}
				}
		}));
	}

	
	
	/****** HELPRES ****/
	
	static public Oracle createExtendedOracle(Set<String> context, String prefix){
		return createExtendedOracle(context, prefix, true);
	}
	
	
	static protected final Oracle createExtendedOracle(Set<String> context, String prefix, boolean putControls){
		List<String> extendedContext = new LinkedList<>();
		extendedContext.addAll(context);
		if ( putControls ){
			extendedContext.add(0,ChangeOfMindException.throwCommand);
			extendedContext.add(1,BreakException.throwCommand);
		}
		String options = "";
		for ( String string : extendedContext )
			options = (options.length()>0? options+"|" : "") + string;
		return new Oracle(extendedContext, prefix+"<"+options+">");
	}
	
	
	/******************** Method overridden!! ***********************/

	@Override
	public Question<State> getStateToAttackQuestion() {
		return this.getStateQuestion();
	}

	@Override
	public Question<State> getStateToAttackFromQuestion() {
		return this.getStateQuestion();
	}

	@Override
	public Question<Integer> getNumberOfArmyToAttackWithQuestion() {
		return this.getNumberQuestion();
	}

	@Override
	public Question<String> getKeepAttackingTheSameStateFromTheSameStateQuestion() {
		return this.getYesNoQuestion();
	}

	@Override
	public Question<Integer> getUnitToMoveForFortifingAfterAttackQuestion() {
		return this.getNumberQuestion();
	}

	@Override
	public ConfQuestion getConfQuestion() {
		return this.getStringBasedConfQuestion();
	}
	
	
	/*
	static private class FortificationAfterAttackQuestion extends Question<String>{
		static Oracle oracle ;
		@Override
		public String askQuestion(Set<String> context, String title) {
			if ( context.equals(Question.yesNoSet) == false )
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
*/
	
}
