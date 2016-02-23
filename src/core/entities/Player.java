/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core.entities;

import java.awt.Color;

public class Player {
	/**
	 * This class represent a player.
	 */

	/**
	 * Unmodificable attribute name, it is just initialized and represent the name of the player
	 */
	private String name;
	/**
	 * Unmodificable attribute color, it is just initialized and represent the color of the player to be shown in the map
	 */
	private Color color;
	
	private int numArmies;
	
	/**
	 * Constructor
	 * @param name of the player
	 * @param color to show in the map
	 */
	public Player(String name, Color color){
		this.name=name;
		this.color=color;
		this.numArmies = 0;
	}

	public int getNumArmies() {
		return numArmies;
	}

	public void setNumArmies(int numArmies) {
		this.numArmies = numArmies;
	}

	/**
	 * Getter
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter
	 * @return the color of the player
	 */
	public Color getColor() {
		return color;
	}
	
	
}
