package tests;


import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import core.Constants;
import core.WorldBuilder;
import core.entities.World;

public class MouseTest {	
		
	public MouseTest(){
		World world=WorldBuilder.Build();
		JFrame frame=new JFrame();
		frame.getContentPane().setPreferredSize(new Dimension(900, 600));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		frame.addMouseListener(new MouseListener() {			
		    @Override
			public void mouseClicked(MouseEvent e) {
			    int x=e.getX();
			    int y=e.getY();
			    System.out.println("clicked: "+x+" , "+y);
			    findState(world, x,y);
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public static void main(String[] args){
		new MouseTest();
	}
	
	public static void findState(World world, int x, int y){
		int[][] COUNTRY_COORD=Constants.COUNTRY_COORD;
		
		for (int i=0; i<42; i++){
			if ( (x-21<COUNTRY_COORD[i][0] && COUNTRY_COORD[i][0]<x+21) && (y-21<COUNTRY_COORD[i][1] && COUNTRY_COORD[i][1]<y+21) ){
				System.out.println("State Found: "+COUNTRY_COORD[i][0]+","+COUNTRY_COORD[i][1]+".");
				System.out.println("it's index is: "+i);
				System.out.println("it's name is: "+world.getState(i).getName());
			}			
		}
	}

}