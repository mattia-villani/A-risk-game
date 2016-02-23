/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package tests;

import java.io.IOException;

import core.WorldBuilder;
import gui.GUI;

// Just a tester class for running a GUI standalone
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
