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

public class RightBorder extends JLabel {

	private static final long serialVersionUID = 1L;
	final BufferedImage rightWood = ImageIO.read(getClass().getResourceAsStream("/images/right.jpg"));

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(rightWood,0,0,50,700,null);
		return;
	}

	public RightBorder() throws IOException {
		setPreferredSize(new Dimension(50, 700));
		return;
	}
}