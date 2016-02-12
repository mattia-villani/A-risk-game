package tests;

import java.io.IOException;

import gui.GUI;

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
