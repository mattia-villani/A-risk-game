package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class FancyFullFrameAnimation extends JFrame {
	

	
	private boolean animating = false;
	
	public FancyFullFrameAnimation(){
		super();
	}
	

	public void paint(Graphics g){
		super.paint(g);
		if ( animating == false ) return;
		
		
	}
	
	
	public void startAnimation(){
		this.setEnabled(false);
		animating = true;
	}
	
	public void endAnimation(){
		this.setEnabled(true);
		animating = false;
	}
	
}
