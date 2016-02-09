package tests;

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
		frame.add(new MapRenderer( WorldBuilder.Build() ));
	}

}
