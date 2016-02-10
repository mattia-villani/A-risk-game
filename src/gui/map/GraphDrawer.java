package gui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import core.entities.State;
import core.entities.World;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GraphDrawer {
	static final float margin = 0.9f;
	static final int circle_ray = 12;
	static final int thickness = 9;
	static final int font_size_name = 12;
	static final int font_type_name = Font.PLAIN;
	static final int font_size_army = 15;
	static final int font_type_army = Font.BOLD;
	static final int x_text_army_fixer = 1;
	static final int y_text_army_fixer = 4;
	static final int size_state_view_width = 100;
	static final int size_state_view_height = 45;
	static final boolean clip_state_view = false;
	static final boolean paint_black_the_name = false;
	static final boolean paint_black_the_army = true;
	static final boolean show_state_view_bounds = false;
	
	final static BasicStroke stroke = new BasicStroke(3);
	
	/* not working yet */
	static double alpha_archs = 1.0;
	
	static BufferedImage graph;
	
	static public void stateView(Graphics2D g, State s){
		
		g.setFont(new Font(g.getFont().getFontName(), font_type_name, font_size_name));
		int off_x = size_state_view_width - (int)g.getFontMetrics().getStringBounds(s.getName(),g).getWidth();
		off_x/=2;
		g.setColor(paint_black_the_name?Color.BLACK:s.getContinet().getColor());
		g.drawString(s.getName(), off_x, size_state_view_height);

		g.setFont(new Font(g.getFont().getFontName(), font_type_army, font_size_army) );
		Rectangle2D size = g.getFontMetrics().getStringBounds(s.getArmy()+"",g);
		g.setColor(paint_black_the_army?Color.BLACK:s.getContinet().getColor());
		g.drawString(s.getArmy()+"", 
				(int)( (size_state_view_width-size.getWidth())/2 + x_text_army_fixer ), 
				(int)( size_state_view_height - (size_state_view_height-size.getHeight())/2 ) - y_text_army_fixer);
		
	}
	
	static public void drawStateValues(Graphics2D g, State s){
		int x0 = s.getX()-size_state_view_width/2;
		int y0 = s.getY()-size_state_view_height/2;
				
		Shape clipSave = g.getClip();
		g.translate( x0, y0 );
		if (clip_state_view)
			g.setClip(0, 0, size_state_view_width, size_state_view_height);
		if (show_state_view_bounds){
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, size_state_view_width, size_state_view_height);
		}
			
		stateView(g,s);
		
		g.translate( -x0, -y0 );
		g.setClip(clipSave);
	}
	
	static public void drawGraph( Graphics2D comp_g, World world, int width, int height ){
		boolean world_invalidate = world.invalidated();
		world.reset_invalidate();
		
		if ( world_invalidate
				|| graph == null 
				|| width>graph.getWidth()*(1.0f-margin) 
				|| height>graph.getHeight()*(1.0f-margin) 
				|| width<graph.getWidth()*margin 
				|| height<graph.getHeight()*margin ){
			
			if ( graph!=null && width==graph.getWidth() && height==graph.getHeight() )
				graph.flush();
			else
				graph = new BufferedImage ( width, height, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = (Graphics2D) graph.getGraphics();
			g.scale( ((double)width)/(double)World.VIRTUAL_WIDTH, ((double)height)/(double)World.VIRTUAL_HEIGHT);
			
			for (State s:world.getStates()){
				g.setColor( s.getContinet().getColor() );
				for (State adj:s.getSmallerAdjacent(world)){
					int a=s.getX(), b=s.getY();
					int c=adj.getX(), d=adj.getY();
					if ( s.getContinet() != adj.getContinet() ){
						if ( Math.abs(a-c) > World.VIRTUAL_WIDTH/2 ){
							// WORNING!!! this fix works only with the traditional map
							int sign = (int)Math.signum(a-c);
							int A = a-sign*World.VIRTUAL_WIDTH;
							int C = c+sign*World.VIRTUAL_WIDTH;
							int fixed_a = A<0? 0: (A>World.VIRTUAL_WIDTH?World.VIRTUAL_WIDTH:A);
							int fixed_c = C<0? 0: (C>World.VIRTUAL_WIDTH?World.VIRTUAL_WIDTH:C);
							g.setPaint(new GradientPaint(a,b, s.getContinet().getColor(), fixed_c, d, adj.getContinet().getColor()));
							g.drawLine( a, b, C, d );																	
							g.setPaint(new GradientPaint(fixed_a,b, s.getContinet().getColor(), c, d, adj.getContinet().getColor()));
							g.drawLine( A, b, c, d );																	
						}else{
							g.setPaint(new GradientPaint(a,b, s.getContinet().getColor(), c, d, adj.getContinet().getColor()));
							g.drawLine( a, b, c, d );
						}
						g.setColor( s.getContinet().getColor() );							
					}else
						g.drawLine( a, b, c, d );
				}
			}
			
			for ( State s : world.getStates() ){
				
				g.setColor( s.getOwner().getColor() );				
				g.fillOval(s.getX()-circle_ray, s.getY()-circle_ray, 2*circle_ray, 2*circle_ray);
				
				g.setStroke(stroke);
				g.setColor( s.getContinet().getColor() );				
				g.drawOval(s.getX()-circle_ray, s.getY()-circle_ray, 2*circle_ray, 2*circle_ray);
				drawStateValues(g,s);
			}

			g.dispose();
		}
		
		if ( comp_g != null )
			comp_g.drawImage(graph, 0, 0, World.VIRTUAL_WIDTH, World.VIRTUAL_HEIGHT, null );
	}
	
}
