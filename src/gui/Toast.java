package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import core.entities.Player;
import gui.Animator.FromZeroToOneIntervalHandler;

public class Toast extends FromZeroToOneIntervalHandler {
	
	static public class ErrorToast extends Toast{

		public ErrorToast(String text, int duration) {
			super(text,Color.WHITE, Color.RED, duration);
		}
		
	}	
	static public class SuperToast extends Toast{
		
		@Override
		protected Font getFont(Font currentFont){
			return currentFont.deriveFont(40f).deriveFont(Font.BOLD|Font.ITALIC);
		}
		@Override
		public float getAlphaPoint() {
			return (float) (super.getAlphaPoint()*(Math.sin(point*Math.PI*10)/4+0.5f));
		}
		@Override
		public float getYPoint() { return super.getAlphaPoint(); }
		
		public SuperToast(String text, Player player, int duration) {
			super(text,
					player.getColor().darker().darker(),
					FancyFullFrameAnimation.alphaColor(player.getColor().brighter().brighter(), 0.8f), 
					duration+1000);
		}
		
	}
	
	static final public int SHORT = 4000, LONG = 6000;
	static private LinkedList <Toast> currentToasts = new LinkedList<>();
	static final public boolean verbose = false;
	
	private String text;
	public String getText() {
		return text;
	}

	public Color getTextColor() {
		return textColor;
	}

	public Color getBackColor() {
		return backColor;
	}
	
	public float getAlphaPoint() {
		double value = Math.pow( Math.sin(Math.PI*Math.pow(1-point,1))*1.2*(2-point) , 8);
		return (float)Math.min(1.0, value );
	}
	public float getYPoint() {
		return this.getAlphaPoint();
	}

	private Color textColor, backColor;
	protected float point ;
	
	protected Font getFont(Font currentFont){
		return currentFont;
	}
	
	static public void drawToasts( Graphics2D g ){
		final int marginY = 2;
		final int paddingX = 5;
		final int paddingY = 1;
		final int strokeSize = 3;
		
		g.setFont(g.getFont().deriveFont(15.0f));
		Stroke stroke = new BasicStroke(strokeSize);
		
		int y=0;
		synchronized( currentToasts ){
			if ( verbose ) System.out.println("Drawing toasts ("+currentToasts.size()+")");
			Iterator<Toast> iterator = currentToasts.descendingIterator();
			Font savedFont = g.getFont();
			while ( iterator.hasNext() ){
				Toast toast = iterator.next();
				g.setFont( toast.getFont(savedFont) );
				Rectangle2D size = g.getFontMetrics().getStringBounds(toast.getText(), g);
				int w = (int) (size.getWidth()+2*paddingX);
				int h = (int) (size.getHeight()+2*paddingY*2);
				float dy = y - h * toast.getYPoint();
				
				g.setColor(FancyFullFrameAnimation.alphaColor(toast.getBackColor().brighter(), toast.getAlphaPoint()));
				g.fillRect(-w/2, (int)dy, w, h);
				g.setColor(FancyFullFrameAnimation.alphaColor(toast.getBackColor().darker(), toast.getAlphaPoint()));
				g.setStroke(stroke);
				g.drawRect(-w/2, (int)dy, w, h);
				g.setColor(FancyFullFrameAnimation.alphaColor(toast.getTextColor(), toast.getAlphaPoint()));
				g.drawString(toast.getText(),-w/2+paddingX, (int) (dy-paddingY+size.getHeight()));
				
				y = (int)dy - marginY;
			}
			g.setFont(savedFont);
		}
		
	}
	
	public Toast(String text, int duration) {
		this(text, Color.ORANGE, Color.ORANGE.darker().darker().darker(), duration);
	}

	public Toast(String text, Color textColor, Color backColor, int duration) {
		super(0, duration);
		this.text = text;
		this.textColor = textColor;
		this.backColor = backColor;
		synchronized(currentToasts){
			currentToasts.add(this);
		}
		Animator.add(this);
		if ( verbose )
			System.out.println("Created toast "+text);
	}

	@Override
	public void run(float point) {
		this.point = point;
	}

	@Override
	public void post() {
		synchronized(currentToasts){
			currentToasts.remove(this);
		}
		if ( FancyFullFrameAnimation.frame != null )
			FancyFullFrameAnimation.frame.repaint(); // avoids that the toast stays there.
	}

	@Override
	public void pre() {}

}
