package gui.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.entities.Continent;
import core.entities.State;
import core.entities.World;

public class GraphDrawer {
	static final int circle_ray = 4;
	static BufferedImage graph;
	
	static public void drawGraph( Graphics2D comp_g, World world, int width, int height ){
		if ( graph == null || width>graph.getWidth() || height>graph.getHeight() ){
			graph = new BufferedImage ( width, height, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = (Graphics2D) graph.getGraphics();
			
			g.setColor( Color.RED );
			for (Continent c:world.getContinents())
				for (State s:c){
					g.fillOval(s.getX()-circle_ray, s.getY()-circle_ray, 2*circle_ray, 2*circle_ray);
				}
			
			g.dispose();
		}
		if ( comp_g != null )
			comp_g.drawImage(graph, 0, 0, World.VIRTUAL_WIDTH, World.VIRTUAL_HEIGHT, null );
	}
	
}
