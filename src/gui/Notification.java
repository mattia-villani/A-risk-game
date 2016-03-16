package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import core.entities.Player;
import gui.Animator.FromZeroToOneIntervalHandler;

public class Notification extends FancyFullFrameAnimation.View {
	public Notification(FancyFullFrameAnimation fancyFullFrameAnimation, String text, Player player, int duration) {
		fancyFullFrameAnimation.super(duration);
		this.text = text;
		this.player = player;
		fancyFullFrameAnimation.startAnimation(this,true);
	}

	public static final int LONG = 3000, SHORT = 1500;
	private static final int paddingX = 20, paddingY = 40;
	private static final int fontSize = 40;
	Rectangle2D textSize;
	
	private String text;
	private Player player;


	@Override
	public int getWidth() {
		int width = (int)(textSize==null?0:textSize.getWidth());
		return width+paddingX*2;
	}

	@Override
	public int getHeight() {
		int height = (int)(textSize==null?0:textSize.getHeight());
		return height+paddingY*2;
	}

	public float movingPointAlterator(float point){
		float value = (float)Math.pow( Math.sin(Math.PI*Math.pow(point,1))*1.4 , 4);
		return Math.min(1.0f, value);
	}
	
	@Override 
	public boolean drawGrayBack(){ return false; }
	
	@Override
	public boolean paint(Graphics2D g2d, float useThisAlpha) {
		float importance = 0.2f;
		float point = movingPointAlterator( (float) (Math.pow(useThisAlpha,0.2)*(importance+Math.sin(this.getAnimationPoint()*Math.PI)/(1-importance))/2) );

		g2d.setFont( g2d.getFont().deriveFont(Font.BOLD | Font.ITALIC , fontSize) );
		textSize = g2d.getFontMetrics().getStringBounds(text, g2d);
		
		float backGroundAlpha = 0.6f;
		Color color = player!=null ? player.getColor() : Color.ORANGE;
		for ( int i=0; i<2; i++ ) color = color.darker();
		
		int x = (int) ((1-point) * 400 * ( this.getAnimationPoint()<0.5f?-1:1 )) ;
		
		g2d.setColor(FancyFullFrameAnimation.alphaColor( color , point*backGroundAlpha));
		g2d.fillRect(x, 0, getWidth(), getHeight());
		
		for ( int i=0; i<4; i++ ) color = color.brighter();
		g2d.setColor(FancyFullFrameAnimation.alphaColor( color, point));
		g2d.drawString(text, x+paddingX, (int) (paddingY+textSize.getHeight()*0.85f));
		
		g2d.setStroke(new BasicStroke(4));
		g2d.drawRect(x, 0, getWidth(), getHeight());
		return false;
	}

}
