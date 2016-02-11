package core.entities;
/**
 * @author Mattia Villani
 */

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
	
	/**
	 * Constructor
	 * @param name of the player
	 * @param color to show in the map
	 */
	public Player(String name, Color color){
		this.name=name;
		this.color=color;
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
