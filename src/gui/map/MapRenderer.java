package gui.map;
/**
 * @author Mattia Villani
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import core.entities.World;

public class MapRenderer extends JComponent {
	/**
	 * JComponent that cares about rendering the map.
	 */

	/** copy of this reference in order to always be possible to call invalidate */
	static private MapRenderer this_class ;

	static final boolean verbose = true; // print lot of debugs messages
	
	/** world displayed */
	private World world;
	/** Matrix that rapresent the scaling and translation transofrmations to do on the content of the map */
	private AffineTransform matrix;
	/** Inverse transofrmation to gain the screen coords by the virtual ones*/
	private AffineTransform inverted_matrix;
	/** real size on the screen of the area drawn */
	private double displayed_width, displayed_height;

	/** added just to soppres wornings */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param w world to draw
	 */
	public MapRenderer( World w ){
		super();
		// save this ref
		this_class = this;
		// save the world
		world = w;
		// add a listener like embended for mananging the resize event
		this.addComponentListener(new ComponentListener(){
			//http://stackoverflow.com/questions/1088595/how-to-do-something-on-swing-component-resizing
			@Override
			public void componentResized(ComponentEvent e) {
				// re evaluates the matrix with the new dimensions
				MapRenderer.this.initMatrixs(e.getComponent().getWidth(),e.getComponent().getHeight());
			}
			@Override
			public void componentHidden(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentShown(ComponentEvent e) {}			
		});
	}

	/**
	 * Set the matrix and inverted_matrix with the proper transformation refered to the provided dimensions
	 * @param width of the component
	 * @param height of the component
	 */
	private void initMatrixs( int width, int height ){
		// if they were null, this is the first initialization and the have to be allocated
		if (matrix == null || inverted_matrix == null ){
			matrix = new AffineTransform();
			inverted_matrix = new AffineTransform();
		}
		// drop previous transformations
		matrix.setToIdentity();
		inverted_matrix.setToIdentity();

		// nothing to do if one of the dimentions is not visible
		if ( width == 0 || height == 0 ) return;
		// ratio on the X axe
		double width_ratio = ((double)width) / (double)World.VIRTUAL_WIDTH;
		// ratio on the Y axe
		double height_ratio = ((double)height) / (double)World.VIRTUAL_HEIGHT;
		// since the propotions must be kept, then the ratio is unique for both the axis and it is the smaller since it suits both
		double ratio = Math.min(width_ratio, height_ratio);
		// just a check but it shouldn't happen
		assert ratio != 0 : "something wired is happening: ratio should be diff from 0";
		// size of the biggest rectangle with the same ratio of the world'rectangle that fits in this component
		displayed_width = ratio * (double)World.VIRTUAL_WIDTH;
		displayed_height = ratio * (double)World.VIRTUAL_HEIGHT;
		// offsets to put the rectangle in the middle of the component
		double x_offset = (width - displayed_width)/2.0;
		double y_offset = (height - displayed_height)/2.0;

		// apply the needed transformations to the matrixs
		matrix.translate(x_offset, y_offset);
		matrix.scale(ratio, ratio);
		// revers
		inverted_matrix.scale(1/ratio, 1/ratio);
		inverted_matrix.translate( -x_offset, -y_offset);

		// it asks the system to redraw
		this.revalidate();
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		// call the super paintComponent(g)
		super.paintComponent(g);
		// if the matrixs are still null, then they are initialized and stored for efficiency
		if (matrix == null || inverted_matrix == null )
			initMatrixs( this.getWidth(), this.getHeight() );	

		// working on the 2D class of the graphics, it applay the matrix transformation
		Graphics2D g2 = (Graphics2D) g;
		g2.transform( matrix );

		// asks GraphDrawer to draw the world in the graphic g2 with the evaluated sizes
		GraphDrawer.drawGraph( g2, world, (int)displayed_width, (int)displayed_height );

		// draw a rectangle in the area the world will be drawn in
		Rectangle box = new Rectangle(0,0,World.VIRTUAL_WIDTH-1,World.VIRTUAL_HEIGHT-1);
		g2.draw(box);

		// "pop" the transformation to the previous one
		g2.transform( inverted_matrix );
	}

	/**
	 * call the invalidate method on the instance of this class stored in this_class attribute
	 */
	public static void Invalidate(){
		if ( this_class != null ){ 
			// this will tell graphDrawer to drop his graph
			GraphDrawer.Invalidate(); 
			this_class.revalidate();
			this_class.repaint();
			if ( verbose )System.out.println("MapRenderer was asked to invalidate: done.");
		} else if ( verbose )System.out.println("MapRenderer was asked to invalidate: failled.");
	}	

}

