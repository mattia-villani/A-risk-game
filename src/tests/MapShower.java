/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package tests;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

import core.WorldBuilder;
import gui.map.MapRenderer;

public class MapShower {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(300,400);
		frame.setTitle("Just visualize the map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		JPanel pannel = new JPanel( new BorderLayout());
		frame.add(pannel);
		JComponent component = new MapRenderer( WorldBuilder.Build() );
		frame.setPreferredSize(new Dimension(1200, 700));
		component.setPreferredSize(frame.getPreferredSize());
		pannel.add(component, BorderLayout.CENTER);
		frame.pack();
		
		
	}

}
