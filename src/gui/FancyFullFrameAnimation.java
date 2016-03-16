package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class FancyFullFrameAnimation extends JFrame {
	
	static private class Holder {
		boolean holding = false;
		public boolean getHolding (){ return holding; }
		public void setHolding ( boolean holding ){ this.holding = holding ; }
	}
	
	public abstract class View extends Animator.FromZeroToOneIntervalHandler{
		private float point = 0;
		abstract public int getWidth();
		abstract public int getHeight();
		/* returns true if the border has to be draw, false otherwise */
		abstract public boolean paint( Graphics2D g2d, float useThisAlpha );
		public float pointAlterator(float point){ return (float)Math.pow(point, 4); }
		public boolean drawGrayBack(){ return true; }
		public View(int duration) {
			super(0, duration);
		}
		public float getAnimationPoint(){ return point; }
		@Override
		public void run(float point) {
			this.point = point;
			FancyFullFrameAnimation.this.repaint();
		}
		@Override
		public void post() {
			FancyFullFrameAnimation.this.endAnimation();
		}
		@Override
		public void pre() {}
	}
	

	static final private boolean verbose = false;
	static public FancyFullFrameAnimation frame;
	static final private Holder lock = new Holder();
	
	private boolean animating = false;
	private View view;
	private float backAlpha;
	private BufferedImage copyOfTheBack;
	private int transitionTime = 350;
	private JTextField inputToDisable ;
	
	public int getTransitionTime(){
		return transitionTime;
	}
	
	public void setInputToDisable(JTextField inputTextField){
		inputToDisable = inputTextField;
	}
	
	public FancyFullFrameAnimation(){
		super();
		frame = this;
		if ( verbose ) System.out.println("FullFrame initialized");
	}

	static public Color alphaColor(Color color, float alpha){
		alpha = alpha>1.0f ? alpha : ( alpha<0? 0 : alpha );
		return new Color(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				(int)(alpha*(float)color.getAlpha()));
	}
	
	
	@Override	
	public void paint(Graphics g){
		if ( copyOfTheBack == null || copyOfTheBack.getWidth()!=getWidth() || copyOfTheBack.getHeight()!=getHeight() ) 
			copyOfTheBack = new BufferedImage ( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
		else 
			copyOfTheBack.flush();

		Graphics2D g2d = (Graphics2D) copyOfTheBack.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		super.paint( g2d );
		
		Font font = g2d.getFont(); // back up
		if ( animating )
			animatingPaint( g2d );

		g2d.setFont(font);
		// toasts
		float x = this.getWidth()/2;
		float y = this.getHeight()*0.8f;
		g2d.translate(x,y);
		Toast.drawToasts(g2d);
		g2d.translate(-x,-y);		

		
		g.drawImage(copyOfTheBack, 0, 0, null);
		g2d.dispose();
	}

	public void animatingPaint(Graphics2D g2d){

		float powered = view.pointAlterator(backAlpha);
		
		float w = powered*(float)view.getWidth();
		float h = powered*(float)view.getHeight();
		
		if ( w!=0 && h!=0 ){
			AffineTransform state = g2d.getTransform();
			float x0 = (getWidth()-w)/2;
			float y0 = (getHeight()-h)/2;
			
			if (view.drawGrayBack()){
				g2d.setColor(alphaColor(alphaColor(Color.GRAY, 0.6f), backAlpha));
				g2d.fillRect((int)x0, (int)y0, (int)w, (int)h);
			}
				
			g2d.translate( x0 , y0 );
			g2d.scale( w/(float)view.getWidth() , h/(float)view.getHeight() );
			
			boolean drawBorder = view.paint( g2d, powered );

			g2d.setTransform(state);
			if ( drawBorder ){
				g2d.setStroke(new BasicStroke(2));
				g2d.setColor(alphaColor(Color.black, powered));
				g2d.drawRect((int)x0, (int)y0, (int)w, (int)h);
			}
		}
	}
	
	
	public void startAnimation(View view, boolean blockThread){
		if ( verbose ) System.out.println("FullFrame starting animation");		
		
		synchronized ( lock ){
			while ( lock.getHolding() )
				try {
					lock.wait();
				} catch (InterruptedException e) {e.printStackTrace();}
			lock.setHolding(true);
		}
		
		if ( inputToDisable != null )
			inputToDisable.setEnabled(false);
		
		animating = true;
		assert view != null : "the view should be something to be displaied";
		this.view = view;

		Animator.add( new Animator.FromZeroToOneIntervalHandler(0,transitionTime) {
			@Override
			public void run(float point) {
				if ( verbose ) System.out.println("FullFrame updatting animation : "+point);
				backAlpha = point;
				FancyFullFrameAnimation.this.repaint();
			}
			@Override
			public void pre() { 		
				if ( verbose ) System.out.println("FullFrame animation started");
			}
			@Override
			public void post() { Animator.add(view); }
		});
		
		if ( blockThread )
			synchronized(lock){
				while ( lock.getHolding() )
					try {
						lock.wait();
					} catch (InterruptedException e) {e.printStackTrace();}
				lock.notify();
			}
	}
	
	public void endAnimation(){
		if ( verbose ) System.out.println("FullFrame ending animation");
		
		Animator.add( new Animator.FromZeroToOneIntervalHandler(0,transitionTime) {
			@Override
			public void run(float point) {
				if ( verbose ) System.out.println("FullFrame updatting animation : "+point);
				backAlpha = 1.0f-point;
				FancyFullFrameAnimation.this.repaint();
			}
			@Override
			public void pre() {}
			@Override
			public void post() {
				animating = false;
				FancyFullFrameAnimation.this.view = null;
				FancyFullFrameAnimation.this.repaint();
				FancyFullFrameAnimation.this.invalidate();
				if ( inputToDisable != null )
					inputToDisable.setEnabled(true);
				synchronized( lock ){
					lock.setHolding(false);
					lock.notify();
				}
				if ( verbose ) System.out.println("FullFrame animation ended");
			}
		});
	}
	
}
