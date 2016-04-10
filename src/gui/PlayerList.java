/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import javax.swing.*;
import core.entities.Player;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerList extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Player> players;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;

		Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
		fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

		Font f = new Font("Serif", Font.ITALIC, 15).deriveFont(fontAttributes);
		g2.setFont(f);

		String text="Player List";

		FontMetrics fm = g2.getFontMetrics(f);
		Rectangle2D rect = fm.getStringBounds(text, g2);
		int textWidth = (int)(rect.getWidth());

		int x = (this.getWidth() - textWidth)/ 2;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawString(text, x, 20);
		if ( players != null ) 
			drawList( g2 );
		return;
	}

	public PlayerList() {			
		setPreferredSize(new Dimension(350, 63));
		setBackground(new Color(244, 239, 202));
		return;
	}
	
	public void requestToDrawList ( ArrayList<Player> players ){
		this.players = players;
		this.repaint();
	}

	public void drawList(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;

		Font f = new Font("Serif", Font.PLAIN, 16);
		g2.setFont(f);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		FontMetrics fm = g2.getFontMetrics(f);

		int y = 20;
		int x = 0;

		for (int i=0; i<6; i++) {
			g2.setColor(players.get(i).getColor());
			String name = players.get(i).getName();

			Rectangle2D rect = fm.getStringBounds(name, g2);
			int textWidth = (int)(rect.getWidth());

			if (textWidth > 106) {
				int end = name.length();
				while (textWidth > 96){
					name = name.substring(0, end);
					rect = fm.getStringBounds(name, g2);
					textWidth = (int)(rect.getWidth());
					end--;
				}
			name = name+"...";
			}

			if ( (i & 1) == 0 ) {
				x = (this.getWidth()/4) - (textWidth/2);
				y = y+23;
			}
			else {
				x = (this.getWidth()/4)*3 - (textWidth/2);
			}
			g2.drawString(name, x, y);
		}
		return;
	}
}