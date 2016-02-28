/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import core.entities.State;
import core.entities.World;

import java.awt.geom.Rectangle2D;

public class GraphDrawer {
	/**
	 * Class that actually cares about drawing the graph. It saves it in a bitmap and redraw for efficiency
	 */
	static final boolean verbose = false; // print lot of debugs messages
	
	static final float margin = 0.9f; // margin in the size after which the store image is dropped
	static final int circle_ray = 12; // size of the circle of a country
	static final int thickness = 9; // size of the archs 
	static final int font_size_name = 12; // size of the font that is used to show the name of the coutry
	static final int font_type_name = Font.BOLD;// type of the font that is used to show the name of the coutry
	static final int font_size_army = 13;// size of the font that is used to show the numerosity of the army
	static final int font_type_army = Font.PLAIN;// type of the font that is used to show the numerosity of the army
	static final int x_text_army_fixer = 1; // fixer to display the number of the army in the center
	static final int y_text_army_fixer = 4; // fixer to display the number of the army in the center
	static final int size_state_view_width = 100; // of the "view" (the rectangle) in which the info are drawn
	static final int size_state_view_height = 45; // of the "view" (the rectangle) in which the info are drawn
	static final boolean clip_state_view = false; // flag to choice if to clip outside that boundry
	static final boolean paint_black_the_name = true; // flag to write in black the name
	static final boolean paint_black_the_army = true; // flag to write in black the army
	static final boolean show_state_view_bounds = false; // to show the rectangle of the view around a country
	
	final static BasicStroke stroke = new BasicStroke(2); // to draw the oval around the circle of the state 
	
	static boolean world_invalidate = false; // flag to say if the world was invalidated
	
	/* not working yet */
	static double alpha_archs = 1.0;
	
	/**
	 * since lot of the paint may be done over the same map, it is store to more efficiently redraw that
	 */
	static BufferedImage graph;
	
	/**
	 * Draws the states information assuming it is in coords 0,0
	 * @param g graphich to draw in
	 * @param s state to draw about
	 */
	static public void stateView(Graphics2D g, State s){
		
		// sets the font.... g.getFont().getFontName() was the problem.
		g.setFont(new Font(null, font_type_name, font_size_name));
		// it evaluates the off_x to make it appear in the center
		int off_x = size_state_view_width - (int)g.getFontMetrics().getStringBounds(s.getName(),g).getWidth();
		off_x/=2;
		// sets the color
		g.setColor(paint_black_the_name?Color.BLACK:s.getContinet().getColor());
		// draw the name
		g.drawString(s.getName(), off_x, size_state_view_height);

		// sets the font 
		g.setFont(new Font(null, font_type_army, font_size_army) );
		// evaluates the size of the text
		Rectangle2D size = g.getFontMetrics().getStringBounds(s.getArmy()+"",g);
		// set the color
		g.setColor(paint_black_the_army?Color.WHITE:s.getContinet().getColor());
		// draws the text making it appear centerly in the rect ( fixing it with the values provided )
		g.drawString(s.getArmy()+"", 
				(int)( (size_state_view_width-size.getWidth())/2 + x_text_army_fixer ), 
				(int)( size_state_view_height - (size_state_view_height-size.getHeight())/2 ) - y_text_army_fixer);
	}
	
	/**
	 * Set up the graphic g to easly draw the view of the state
	 * @param g the graphic to draw in
	 * @param s the state to draw
	 */
	static public void drawStateValues(Graphics2D g, State s){
		// calculates the corner of the rect view ( the center is in the state position )
		int x0 = s.getX()-size_state_view_width/2;
		int y0 = s.getY()-size_state_view_height/2;
		
		// since it may be aske to clip the view, the previus one is saved to be restored later
		Shape clipSave = g.getClip();
		// applay the tranformation to put the rect in coords 0,0 
		g.translate( x0, y0 );
		// if ascked it apply the clip to the view rect area
		if (clip_state_view)
			g.setClip(0, 0, size_state_view_width, size_state_view_height);
		// if asked it draws a black rectangle over that area
		if (show_state_view_bounds){
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, size_state_view_width, size_state_view_height);
		}
			
		// draw the view 
		stateView(g,s);
		
		// restore the transformation
		g.translate( -x0, -y0 );
		// restore the clip
		g.setClip(clipSave);
	}
	
	/**
	 * actually draw the graph
	 * @param comp_g is the graphic where to draw in (in null it just stores the image)
	 * @param world is the one to draw
	 * @param width of the view
	 * @param height of the view
	 */
	static public void drawGraph( Graphics2D comp_g, World world, int width, int height ){
		if ( verbose ) System.out.println("Drawing graph");
		// check if it is asked to invalidate the previus stored image 
		
		if ( verbose & world_invalidate ) System.out.println("World was invalidated");
		if ( verbose & ! world_invalidate ) System.out.println("World wasn't invalidated");
		
		// situation for which the stored image is dropped 
		if ( world_invalidate // asked by some class
				|| graph == null  // not stored yet
				|| width>graph.getWidth()*(1.0+1.0f-margin)  // dimentions have changed too much so the scaling would be ugly
				|| height>graph.getHeight()*(1.0+1.0f-margin) 
				|| width<graph.getWidth()*margin 
				|| height<graph.getHeight()*margin ){

			world_invalidate = false; // reset of the invalidate 
			
			// if the stored image has the right dimention, then it is just initialized instead that allocated
			if ( graph!=null && width==graph.getWidth() && height==graph.getHeight() ){
				graph.flush();
				if ( verbose ) System.out.println("graph was good, flushed");
			} else {// otherwise simple allocates one
				graph = new BufferedImage ( width, height, BufferedImage.TYPE_INT_ARGB );
				if ( verbose ) System.out.println("graph wasn't good, instantiated");
			}
			// from now on, the drawing is on the image
			Graphics2D g = (Graphics2D) graph.getGraphics();
			// the drawing will suit propertly the dimension of the image
			g.scale( ((double)width)/(double)World.VIRTUAL_WIDTH, ((double)height)/(double)World.VIRTUAL_HEIGHT);
			
			for (State s:world.getStates()){
				// for each state in the world
				// the color used is the one of the continet
				g.setColor( s.getContinet().getColor() );
				// it first draws the arch ( a subset in order not to draw all of them twice )
				for (State adj:s.getSmallerAdjacent(world)){
					// aliases for the (x0,y0) -> (x1,y1) of the arch
					int a=s.getX(), b=s.getY();
					int c=adj.getX(), d=adj.getY();
					// if the arch has not the both sides in the same continent, then a special drawn is done
					if ( s.getContinet() != adj.getContinet() ){
						// two situations are possible: or the arch connect them directly or it has to go in the other side of the map
						if ( Math.abs(a-c) > World.VIRTUAL_WIDTH/2 ){
							// if this happen, the the arch should exit from one side and appear in the other.
							// WORNING!!! this fix works only with the traditional map
							// evaluate the sign ( if the first node is right or left )
							int sign = (int)Math.signum(a-c);
							// created two new points which are out of window
							int A = a-sign*World.VIRTUAL_WIDTH;
							int C = c+sign*World.VIRTUAL_WIDTH;
							// and other two which are a limitation of the previus one to the border of the window
							int fixed_a = A<0? 0: (A>World.VIRTUAL_WIDTH?World.VIRTUAL_WIDTH:A);
							int fixed_c = C<0? 0: (C>World.VIRTUAL_WIDTH?World.VIRTUAL_WIDTH:C);
							// draws two lines going out of the mapwith the gradient limitated to the border
							g.setPaint(new GradientPaint(a,b, s.getContinet().getColor(), fixed_c, d, adj.getContinet().getColor()));
							g.drawLine( a, b, C, d );																	
							g.setPaint(new GradientPaint(fixed_a,b, s.getContinet().getColor(), c, d, adj.getContinet().getColor()));
							g.drawLine( A, b, c, d );																	
						}else{
							// otherwise it the states are close, so just a fancy gradiented line is drawn
							g.setPaint(new GradientPaint(a,b, s.getContinet().getColor(), c, d, adj.getContinet().getColor()));
							g.drawLine( a, b, c, d );
						}
						// set back the color to the one of the continent
						g.setColor( s.getContinet().getColor() );							
					}else
						// both country are in the same continent, so it just draws the line (the color is the one of the continent)
						g.drawLine( a, b, c, d );
				}
			}
			
			// after that it drows the nodes on the archs
			for ( State s : world.getStates() ){
				// for each state, the internal color is of the player
				if ( verbose ) System.out.println(s.getName()+" is owned by "+s.getOwner()+" which has color "+s.getOwner().getColor() );
				g.setColor( s.getOwner().getColor() );				
				g.fillOval(s.getX()-circle_ray, s.getY()-circle_ray, 2*circle_ray, 2*circle_ray);
				
				// and surrond it with the conntinent color
				g.setStroke(stroke);
				g.setColor( s.getContinet().getColor() );				
				g.drawOval(s.getX()-circle_ray, s.getY()-circle_ray, 2*circle_ray, 2*circle_ray);
				
				// then it draws the view of the state (like name ecc..)
				drawStateValues(g,s);
			}

			if ( verbose ){
				System.out.print("Ownerships: ");
				for(State s: world.getStates())
					System.out.print(s.getName()+" belongs to "+s.getOwner().getName()+", " );
				System.out.println("");
			}

			// force to free the memory
			g.dispose();
		}
		
		
		// it draws only if the comp_g is valid and it draws in the bitmap.
		if ( comp_g != null )
			comp_g.drawImage(graph, 0, 0, World.VIRTUAL_WIDTH, World.VIRTUAL_HEIGHT, null );
	}

	/**
	 * call the invalidate method on the instance of this class stored in this_class attribute
	 */
	public static void Invalidate(){
		world_invalidate = true;
	}	

	
}
