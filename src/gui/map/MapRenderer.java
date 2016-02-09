package gui.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import core.entities.World;

public class MapRenderer extends JComponent {
	
	private World world;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MapRenderer( World w ){
		world = w;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle box = new Rectangle(5,10,20,30);
		g2.draw(box);
	}
	
}
