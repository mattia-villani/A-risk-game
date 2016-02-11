package core.entities;
/**
 * @author Mattia Villani
 */
import java.awt.Color;

public class NeutralPlayer extends Player {
	/**
	 * Represent a specific type of player: the neutral one! it is charaterized to be uncontrolled by users.
	 */
	/** 
	 * Constructor
	 * @param name of the player
	 * @param color of the player
	 */	
	public NeutralPlayer(String name, Color color) {
		super(name, color);
	}
	
	/**
	 * Constructor. Color is set to default to GRAY
	 * @param name to assign to the neutral player
	 */
	public NeutralPlayer(String name){
		super(name, Color.GRAY);
	}

}
