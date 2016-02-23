package gui.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import core.entities.State;

public class StateView {

	private State state ;
	
	public StateView( State state ){
		this.state = state;
	}
	
	// this is a sort of "background" more static
	public void paint(Graphics2D g, int width, int height){
		
	}

	// this is meant to show information that change often
	public void paintVolitile(Graphics2D g, int width, int height){
		
		// sets the font 
		g.setFont(new Font(g.getFont().getFontName(), GraphDrawer.font_type_name, GraphDrawer.font_size_name));
		// it evaluates the off_x to make it appear in the center
		int off_x = width - (int)g.getFontMetrics().getStringBounds(state.getName(),g).getWidth();
		off_x/=2;
		// sets the color
		g.setColor(GraphDrawer.paint_black_the_name?Color.BLACK:state.getContinet().getColor());
		// draw the name
		g.drawString(state.getName(), off_x, height);
			// sets the font
		g.setFont(new Font(g.getFont().getFontName(), GraphDrawer.font_type_army, GraphDrawer.font_size_army) );
		// evaluates the size of the text
		Rectangle2D size = g.getFontMetrics().getStringBounds(state.getArmy()+"",g);
		// set the color
		g.setColor(GraphDrawer.paint_black_the_army?Color.BLACK:state.getContinet().getColor());
		// draws the text making it appear centerly in the rect ( fixing it with the values provided )
		g.drawString(state.getArmy()+"", 
				(int)( (width-size.getWidth())/2 + GraphDrawer.x_text_army_fixer ), 
				(int)( height - (height-size.getHeight())/2 ) - GraphDrawer.y_text_army_fixer);
		
	}
	
}
	
