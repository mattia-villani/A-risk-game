package gui.map;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import core.entities.Continent;
import core.entities.State;
import core.entities.World;

public class GraphDrawer {
	static final float margin = 0.8f;
	static final int circle_ray = 10;
	static final int thickness = 8;
	static BufferedImage graph;
	
	static public void drawStateValues(Graphics2D g, State s){
		
	}
	
	static public void drawGraph( Graphics2D comp_g, World world, int width, int height ){
		if ( graph == null || width>graph.getWidth() || height>graph.getHeight() || width<graph.getWidth()*margin || height<graph.getHeight()*margin ){
			graph = new BufferedImage ( width, height, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = (Graphics2D) graph.getGraphics();
			g.scale( ((double)width)/(double)World.VIRTUAL_WIDTH, ((double)height)/(double)World.VIRTUAL_HEIGHT);
			
			for (Continent cont:world.getContinents()){
				for (State s:cont){
					g.setColor( cont.getColor() );
					for (State adj:s.getSmallerAdjacent(world)){
						int a=s.getX(), b=s.getY();
						int c=adj.getX(), d=adj.getY();
						if ( s.getContinet() != adj.getContinet() ){
							if ( Math.abs(a-c) > world.VIRTUAL_WIDTH/2 ){
								// WORNING!!! this fix works only with the traditional map
								int sign = (int)Math.signum(a-c);
								int A = a-sign*world.VIRTUAL_WIDTH;
								int C = c+sign*world.VIRTUAL_WIDTH;
								int fixed_a = A<0? 0: (A>world.VIRTUAL_WIDTH?world.VIRTUAL_WIDTH:A);
								int fixed_c = C<0? 0: (C>world.VIRTUAL_WIDTH?world.VIRTUAL_WIDTH:C);
								g.setPaint(new GradientPaint(a,b, s.getContinet().getColor(), fixed_c, d, adj.getContinet().getColor()));
								g.drawLine( a, b, C, d );																	
								g.setPaint(new GradientPaint(fixed_a,b, s.getContinet().getColor(), c, d, adj.getContinet().getColor()));
								g.drawLine( A, b, c, d );																	
							}else{
								g.setPaint(new GradientPaint(a,b, s.getContinet().getColor(), c, d, adj.getContinet().getColor()));
								g.drawLine( a, b, c, d );
							}
							g.setColor( cont.getColor() );							
						}else
							g.drawLine( a, b, c, d );
					}
					g.fillOval(s.getX()-circle_ray, s.getY()-circle_ray, 2*circle_ray, 2*circle_ray);
					drawStateValues(g,s);
				}
			}
			
			g.dispose();
		}
		
		if ( comp_g != null )
			comp_g.drawImage(graph, 0, 0, World.VIRTUAL_WIDTH, World.VIRTUAL_HEIGHT, null );
	}
	
}
