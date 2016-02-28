package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import gui.FancyFullFrameAnimation.View;

public class Rolling extends View {
	public final static int marginHeight = 30;
	public final static int marginWidth = 25;
	public final static int littleViewHeight = 80;
	public final static int littleViewWidth = 80;
	public final static int duration = 1500;
	private final static boolean verbose = false;
	
	static private Image[] diceFrames;
	static private final int N = 72;
	static private int[] positions = new int[]{
		36, // 1
		45, // 2
		18, // 3
		54, // 4
		63, // 5
		0,  // 6
	};
	
	static void loadImages (Class clazz) {
		if ( diceFrames != null ) return;
		diceFrames = new Image[N];
		for ( int i=0; i<N; i++ )
			try {
				diceFrames[i] = ImageIO.read(clazz.getResourceAsStream("/images/dice_frame/frame_"+i+"_delay-0.1s.gif"));
		    } catch (Exception e) {
		        e.printStackTrace();
		    }		
	}
	
	private int width, height;
	int [][] numbers ;
	
	public Rolling(FancyFullFrameAnimation fancyFullFrameAnimation, int[][] numbers) {
		fancyFullFrameAnimation.super(duration);
		assert numbers != null : "i need data to show";
		this.numbers = numbers;
		height = numbers.length*littleViewHeight+(numbers.length+1)*marginHeight;
		int lenght = 0;
		for ( int[] sub : numbers )
			lenght = Math.max(lenght, sub.length );
		assert lenght != 0 : "sub arrays empty";
		width = lenght*littleViewWidth+(lenght+1)*marginWidth;
		
		loadImages(this.getClass());
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void drawNumber( Graphics2D g2d, int number, float point ){
		if ( verbose ) System.out.println("Drawing number "+number+" at time "+point);
		float topPoint = 0.5f;
		float fixedPoint = Math.min(topPoint, point);
		float ratio = fixedPoint / topPoint;
		ratio = (float) Math.sqrt( Math.sqrt(ratio) );
		float index = ratio * ( N )  ;
		index += positions[number-1];
		// http://www.animatedimages.org/img-animated-dice-image-0064-120764.htm 
		g2d.drawImage( diceFrames[ (int)(index) % N] ,  0, 0, littleViewWidth, littleViewHeight, null);
	}
	
	@Override
	public void paint(Graphics2D g2d, float useThisAlpha) {
		g2d.setColor(FancyFullFrameAnimation.alphaColor(Color.blue.darker(),useThisAlpha*0.5f));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		int y = marginHeight;
		for ( int[] subs: numbers ){
			int x = (width - (marginWidth+subs.length*(littleViewWidth+marginWidth)))/2 + marginWidth;
			for ( int number : subs ){
				g2d.translate(x, y);
				drawNumber(g2d, number , getAnimationPoint() );
				g2d.translate(-x, -y);
				x += marginWidth+littleViewWidth;
			}
			y += marginHeight+littleViewHeight;
		}
	}

}
