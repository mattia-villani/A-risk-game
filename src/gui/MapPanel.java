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

public class MapPanel extends JLabel {

	private static final long serialVersionUID = 1L;
	final BufferedImage bkimage = ImageIO.read(getClass().getResourceAsStream("/images/mappng.png"));

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(bkimage,0,0,900,600,null);
		return;
	}

	public MapPanel() throws IOException {
		setPreferredSize(new Dimension(900, 600));
		return;
	}
}