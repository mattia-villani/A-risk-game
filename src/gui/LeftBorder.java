/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import javax.imageio.ImageIO;

public class LeftBorder extends JLabel {

	private static final long serialVersionUID = 1L;
	final BufferedImage leftWood = ImageIO.read(getClass().getResourceAsStream("/images/left.jpg"));

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(leftWood,0,0,50,700,null);
		return;
	}

	public LeftBorder() throws IOException {
		setPreferredSize(new Dimension(50, 700));
		return;
	}
}