package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import gui.Animator.FromZeroToOneIntervalHandler;

public class Toast extends FromZeroToOneIntervalHandler {
	static final public int SHORT = 3000, LONG = 4000;
	static private List <Toast> currentToasts = new LinkedList<>();
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
		double value = Math.pow( Math.sin(Math.PI*Math.pow(point,1))*1.4 , 8);
		return (float)Math.min(1.0, value );
	}
	public float getYPoint() {
		return this.getAlphaPoint();
	}

	private Color textColor, backColor;
	private float point ;
	
	static public void drawToasts( Graphics2D g ){
		final int marginY = 2;
		final int paddingX = 5;
		final int paddingY = 1;
		final int strokeSize = 3;
		
		Stroke stroke = new BasicStroke(strokeSize);
		
		int y=0;
		synchronized( currentToasts ){
			if ( verbose ) System.out.println("Drawing toasts ("+currentToasts.size()+")");
			for ( Toast toast : currentToasts ){
				Rectangle2D size = g.getFontMetrics().getStringBounds(toast.getText(), g);
				int w = (int) (size.getWidth()+2*paddingX);
				int h = (int) (size.getHeight()+2*paddingY);
				float dy = y - h * toast.getYPoint();
				
				g.setColor(FancyFullFrameAnimation.alphaColor(toast.getBackColor(), toast.getAlphaPoint()));
				g.fillRect(-w/2, (int)dy, w, h);
				g.setColor(FancyFullFrameAnimation.alphaColor(Color.BLACK, toast.getAlphaPoint()));
				g.setStroke(stroke);
				g.drawRect(-w/2, (int)dy, w, h);
				g.setColor(FancyFullFrameAnimation.alphaColor(toast.getTextColor(), toast.getAlphaPoint()));
				g.drawString(toast.getText(),-w/2+paddingX, (int) (dy-paddingY+size.getHeight()));
				
				y = (int)dy - marginY;
			}
		}
		
	}
	
	public Toast(String text, int duration) {
		this(text, Color.ORANGE.brighter().brighter(), Color.ORANGE.darker().darker(), duration);
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
