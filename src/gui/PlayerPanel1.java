/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import javax.swing.*;
import core.entities.Player;
import core.entities.TerritoryCard;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerPanel1 extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Player> players;
	private Player player;
	//private TerritoryCard[] displaycards=new TerritoryCard[5];

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;

		if ( players != null )
			try {
				drawList( g2 );
			} catch (IOException e) {
				e.printStackTrace();
			}
		return;
	}

	public PlayerPanel1() throws IOException{
		setBackground(new Color(244, 239, 202));
		return;
	}
	
	public void requestToDrawHand ( ArrayList<Player> players ){
		this.players = players;
		this.player = players.get(0);
		this.repaint();
	}

	public void drawList(Graphics g) throws IOException {

		Graphics2D g2 = (Graphics2D)g;

		Font f = new Font("Serif", Font.BOLD, 16);

		g2.setFont(f);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		FontMetrics fm = g2.getFontMetrics(f);

		g2.setColor(player.getColor());
		String name = player.getName()+"'s Cards";
		Rectangle2D rect = fm.getStringBounds(name, g2);
		
		int titlewidth = (this.getWidth() - (int)rect.getWidth())/ 2;

		g2.drawString(name, titlewidth, 20);
		
		TerritoryCard[] displaycards=new TerritoryCard[5];
		for (int i=0; i<player.getHand().size(); i++){
			displaycards[i]=player.getHand().get(i);
		}
		
		for (int i=0; i<5; i++){
			int y=30;
			int x=15+(i*95);
			if (i>2) {
				y=160;
				x=62+((i-3)*95);
			}
			
			int index;
			if (displaycards[i]==null) index=44;
			else index=displaycards[i].getIndex();
			
			g.drawImage(TerritoryCard.getImage(index),x,y,80,120,null);
		}
		return;
	}
}