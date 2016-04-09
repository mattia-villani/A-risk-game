/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.awt.Color;

import gui.GUI;

public class NeutralPlayer extends Player {
	/**
	 * Represent a specific type of player: the neutral one! it is charaterized to be uncontrolled by users.
	 */
	/** 
	 * Constructor
	 * @param name of the player
	 * @param color of the player
	 */	
	public NeutralPlayer(int id,String name, Color color) {
		super(id,name, color);
	}
	
	/**
	 * Constructor. Color is set to default to GRAY
	 * @param name to assign to the neutral player
	 */
	public NeutralPlayer(int id, String name){
		super(id, name, Color.GRAY);
	}

	@Override
	public QuestionsForGenericPlayer getQuestions(GUI gui) {
		return null;
	}

}
