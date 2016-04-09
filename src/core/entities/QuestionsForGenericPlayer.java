package core.entities;

import gui.GUI;

public abstract class QuestionsForGenericPlayer {
	/** Thtough the questions it will possible to model the bot */
	
	protected GUI gui;
	
	public QuestionsForGenericPlayer(GUI gui){
		this.gui = gui;
	}
	
	/** -- ATTACK -- */
	abstract public Question<State> getStateToAttackQuestion();
	abstract public Question<State> getStateToAttackFromQuestion();
	abstract public Question<Integer> getNumberOfArmyToAttackWithQuestion();
	abstract public Question<String> getKeepAttackingTheSameStateFromTheSameStateQuestion();
	abstract public Question<Integer> getUnitToMoveForFortifingAfterAttackQuestion();
	/** -- ATTACK END -- */
	
	
	static abstract public class ConfQuestion extends Question<String>{
		abstract public void setSkipAllowed(boolean skipAllowed);		
	}
	
	/** -- CARDS -- */
	abstract public ConfQuestion getConfQuestion();
	/** -- CARDS END -- */
	
}
