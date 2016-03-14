package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
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
	
	public abstract class View extends Animator.FromZeroToOneIntervalHandler{
		private float point = 0;
		abstract public int getWidth();
		abstract public int getHeight();
		abstract public void paint( Graphics2D g2d, float useThisAlpha );
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
		return new Color(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				(int)(alpha*(float)color.getAlpha()));
	}
	
	public void paintComponents(Graphics g){
		if ( animating == false ){
			super.paintComponents(g);
		}else {
			if ( copyOfTheBack == null || copyOfTheBack.getWidth()!=getWidth() || copyOfTheBack.getHeight()!=getHeight() ) 
				copyOfTheBack = new BufferedImage ( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
			super.paintComponents( copyOfTheBack.getGraphics() );
		}
	}
	
	@Override	
	public void paint(Graphics g){
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if ( animating == false ){
			super.paint(g);
			// draw toasts ( in case of not animating... there is another at the end of the function
			float x = this.getWidth()/2;
			float y = this.getHeight()*0.9f;
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(x,y);
			Toast.drawToasts(g2d);
			g2d.translate(-x,-y);		

			return;
		}
		if ( copyOfTheBack == null || copyOfTheBack.getWidth()!=getWidth() || copyOfTheBack.getHeight()!=getHeight() ) 
			copyOfTheBack = new BufferedImage ( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
		else 
			copyOfTheBack.flush();
		super.paint( copyOfTheBack.getGraphics() );
		Graphics2D g2d = (Graphics2D) copyOfTheBack.getGraphics();

		float powered = backAlpha*backAlpha*backAlpha*backAlpha;
		
		float w = powered*(float)view.getWidth();
		float h = powered*(float)view.getHeight();
		
		if ( w!=0 && h!=0 ){
			AffineTransform state = g2d.getTransform();
			float x0 = (getWidth()-w)/2;
			float y0 = (getHeight()-h)/2;

			g2d.setColor(alphaColor(alphaColor(Color.GRAY, 0.6f), backAlpha));
			g2d.fillRect((int)x0, (int)y0, (int)w, (int)h);
	
			g2d.translate( x0 , y0 );
			g2d.scale( w/(float)view.getWidth() , h/(float)view.getHeight() );
			
			view.paint( g2d, powered );

			g2d.setTransform(state);
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(alphaColor(Color.black, powered));
			g2d.drawRect((int)x0, (int)y0, (int)w+1, (int)h+1);
		}
		// draw toasts
		float x = this.getWidth()/2;
		float y = this.getHeight()*0.9f;
		g2d.translate(x,y);
		Toast.drawToasts(g2d);
		g2d.translate(-x,-y);		
		// actually draw on screen
		g.drawImage(copyOfTheBack, 0, 0, null);
		g2d.dispose();
	}
	
	
	public void startAnimation(View view){
		if ( verbose ) System.out.println("FullFrame starting animation");		
		
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
				if ( verbose ) System.out.println("FullFrame animation ended");
			}
		});
	}
	
}
