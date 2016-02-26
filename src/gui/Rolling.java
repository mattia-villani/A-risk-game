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
	public final static int littleViewHeight = 120;
	public final static int littleViewWidth = 120;
	public final static int duration = 2000;
	private final static boolean verbose = true;
	
	static private Image[] diceFrames;
	static private final int N = 72;
	static private int[] positions = new int[]{
		37, // 1
		46, // 2
		19, // 3
		55, // 4
		64, // 5
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

	public void drawNumber( Graphics2D g2d, int i, int j, float point ){
		if ( verbose ) System.out.println("Drawing number "+numbers[i][j]+" at time "+point);
				
		// http://www.animatedimages.org/img-animated-dice-image-0064-120764.htm 
		g2d.drawImage( diceFrames[ (int)(point*1562) % N] ,  0, 0, littleViewWidth, littleViewHeight, null);
	}
	
	@Override
	public void paint(Graphics2D g2d, float useThisAlpha) {
		g2d.setColor(FancyFullFrameAnimation.alphaColor(Color.blue.darker(),useThisAlpha*0.5f));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		int y = marginHeight;
		int i=0;
		for ( int[] subs: numbers ){
			int x = (width - (marginWidth+subs.length*(littleViewWidth+marginWidth)))/2 + marginWidth;
			int j=0;
			for ( int number : subs ){
				g2d.translate(x, y);
				drawNumber(g2d, i, j , getAnimationPoint() );
				g2d.translate(-x, -y);
				x += marginWidth+littleViewWidth;
				j++;
			}
			y += marginHeight+littleViewHeight;
			i++;
		}
	}

}
