package gui.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import core.entities.World;

public class MapRenderer extends JComponent {
	static private MapRenderer this_class ;
	
	private World world;
	private AffineTransform matrix;
	private AffineTransform inverted_matrix;
	private double displayed_width, displayed_height;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MapRenderer( World w ){
		super();
		this_class = this;
		world = w;
		this.addComponentListener(new ComponentListener(){
			//http://stackoverflow.com/questions/1088595/how-to-do-something-on-swing-component-resizing
			@Override
			public void componentResized(ComponentEvent e) {
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
		
	private void initMatrixs( int width, int height ){
		if (matrix == null || inverted_matrix == null ){
			matrix = new AffineTransform();
			inverted_matrix = new AffineTransform();
		}
		matrix.setToIdentity();
		inverted_matrix.setToIdentity();

		if ( width == 0 || height == 0 ) return;
		double width_ratio = ((double)width) / (double)World.VIRTUAL_WIDTH;
		double height_ratio = ((double)height) / (double)World.VIRTUAL_HEIGHT;
		double ratio = Math.min(width_ratio, height_ratio);
		assert ratio != 0 : "something wired is happening: ratio should be diff from 0";
		displayed_width = ratio * (double)World.VIRTUAL_WIDTH;
		displayed_height = ratio * (double)World.VIRTUAL_HEIGHT;
		double x_offset = (width - displayed_width)/2.0;
		double y_offset = (height - displayed_height)/2.0;
		
		matrix.translate(x_offset, y_offset);
		matrix.scale(ratio, ratio);

		inverted_matrix.scale(1/ratio, 1/ratio);
		inverted_matrix.translate( -x_offset, -y_offset);
		
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (matrix == null || inverted_matrix == null )
			initMatrixs( this.getWidth(), this.getHeight() );	
		
		Graphics2D g2 = (Graphics2D) g;
		g2.transform( matrix );
		
		GraphDrawer.drawGraph( g2, world, (int)displayed_width, (int)displayed_height );
		
		Rectangle box = new Rectangle(0,0,World.VIRTUAL_WIDTH-1,World.VIRTUAL_HEIGHT-1);
		g2.draw(box);
		
		g2.transform( inverted_matrix );
	}
	
	public static void Invalidate(){
		if ( this_class != null ) this_class.invalidate();
	}
}

