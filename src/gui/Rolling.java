package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import gui.FancyFullFrameAnimation.View;

public class Rolling extends View {
	public final static int marginHeight = 30;
	public final static int marginWidth = 25;
	public final static int littleViewHeight = 120;
	public final static int littleViewWidth = 120;
	public final static int duration = 2000;
	
	private Image dice;
	
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
		
		try {
	        Toolkit tk = Toolkit.getDefaultToolkit();
	        dice = tk.createImage("dice.gif");
	        tk.prepareImage(dice, littleViewWidth, littleViewHeight, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void drawNumber( Graphics2D g2d, int n, float point ){
		// http://www.animatedimages.org/img-animated-dice-image-0064-120764.htm 
		// http://stackoverflow.com/questions/20924230/java-draw-a-gif
		g2d.drawImage((Image) dice,  0, 0, littleViewWidth, littleViewHeight, null);
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
				drawNumber(g2d, number, getAnimationPoint() );
				g2d.translate(-x, -y);
				x += marginWidth+littleViewWidth;
			}
			y += marginHeight+littleViewHeight;
		}
	}

}
