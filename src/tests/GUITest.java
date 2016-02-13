package tests;

import java.io.IOException;

import core.WorldBuilder;
import gui.GUI;

public class GUITest {
	
	public static void main(String[] args) {
		try {
			GUI window=new GUI(WorldBuilder.Build());
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
