package core.entities;

import java.util.HashSet;
import java.util.Set;

import gui.Toast;

public abstract class Question<T>{
	
	static public class ChangeOfMindException extends RuntimeException { 
		private static final long serialVersionUID = 1L;
		static public String throwCommand = "skip"; }
	static public class EndOfGameException extends RuntimeException { private static final long serialVersionUID = 1L; }
	static public class BreakException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		static public String throwCommand = "break"; }	
	// shouldn't be possible to throw but a break operator will be introduced during the attack phases
	static public class OutOfContextException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public OutOfContextException(String message){
			super(message);
			new Toast.ErrorToast(message, Toast.LONG);
		}
	}

	
	
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
	
	
	
	public String outOfContextMessage(){return "The value entered is invalid";}	
	
	/**
	 * calls validatedAskQuestion only if the question is not trivial: the question has more than a possibile answer.
	 */
	final public T notTrivialValidatedAskQuestion(Set<T> context, String title){
		if ( context.size() == 0 ) throw new RuntimeException("Something is wrong: this set shouldn't be empty");
		if ( context.size() == 1 ) return context.iterator().next();
		return validatedAskQuestion(context,title);
	}
	
	/**
	 * calls askQuestion checking that the result belongs to the context
	 */
	final public T validatedAskQuestion(Set<T> context,String title) {
		T result = askQuestion(context,title);
		if ( context.contains(result) == false ) 
			throw new OutOfContextException( outOfContextMessage() );
		return result;
	}
	
	final public String throwExceptionsIfControlsAreUsed(String command){
		if ( ChangeOfMindException.throwCommand.equals(command) ) 
			throw new ChangeOfMindException();
		else if ( BreakException.throwCommand.equals(command) ) 
			throw new BreakException();
		return command; 
	}
}