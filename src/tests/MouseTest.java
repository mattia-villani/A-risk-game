/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package tests;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.*;
import core.Constants;
import core.WorldBuilder;
import core.entities.World;
import gui.MapPanel;
import gui.map.MapRenderer;

public class MouseTest {	
	
	private static MapPanel mapPanel;
	private static MapRenderer worldMap;
		
	public MouseTest() throws IOException{
		World world=WorldBuilder.Build();
		JFrame frame=new JFrame();
		frame.getContentPane().setPreferredSize(new Dimension(900, 600));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mapPanel = new MapPanel();
		worldMap = new MapRenderer(world);
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(900, 600));
		mapPanel.setBounds(0, 0, 900, 600);
		worldMap.setBounds(0, 0, 1000, 600);
		layeredPane.add(mapPanel,1);
		layeredPane.add(worldMap,0);	
		//layeredPane.addMouseListener(new MouseInput());
		frame.add(layeredPane);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public static void main(String[] args) throws IOException{
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