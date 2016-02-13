package tests;
/**
 * @author Andrew Kilbride
 */

import java.io.IOException;

import gui.GUI;

// Just a tester class for running a GUI standalone
public class GUITest {
	
	public static void main(String[] args) {
		try {
			GUI window=new GUI();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
