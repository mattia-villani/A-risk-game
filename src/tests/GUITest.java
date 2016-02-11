package tests;

import java.io.IOException;

import gui.GUI;

public class GUITest {
	
	public static void main(String[] args) {
	GUI window=new GUI();
	try {
		window.createGUI();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
