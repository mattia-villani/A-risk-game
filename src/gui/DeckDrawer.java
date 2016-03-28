package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.sun.org.apache.bcel.internal.Constants;

import core.entities.Player;
import core.entities.State;
import core.entities.TerritoryCard;
import core.entities.TerritoryDeck;
import core.entities.World;
import gui.FancyFullFrameAnimation.View;

public class DeckDrawer extends View {

	boolean done = false;
	int time = 0;
	Component componentFromWhichReadTheSize;
	
	float cardDeckXPerc = 0.5f, cardDeckYPerc = 0.5f;
	float cardRatio = 1/1.6f;
	float cardHeightPerc = 0.8f;
	
	TerritoryCard[] deck;
	int[] cardTimes;
	int singleDuration;
	int[] cardPositions;
	int[] playerCardOwning = new int[6];
	
	World world;
	
	public DeckDrawer(FancyFullFrameAnimation fancyFullFrameAnimation, World world, TerritoryCard[] deck, int duration, int singleDuration, Component comp) {
		fancyFullFrameAnimation.super(duration+500);
		componentFromWhichReadTheSize = comp;
		this.deck = deck;
		this.singleDuration = singleDuration;
		cardTimes = new int[deck.length];
		cardPositions = new int[deck.length];
		this.world = world;
	}
	
	@Override 
	public boolean checkIfExpired(int afterTime){
		for ( int i=0; i<deck.length; i++ )
			if ( deck[i] != null )
				cardTimes[i] +=afterTime;
		return super.checkIfExpired(afterTime);//done;
	}

	@Override
	public int getWidth() {
		return componentFromWhichReadTheSize.getWidth();
	}

	@Override
	public int getHeight() {
		return (int)(componentFromWhichReadTheSize.getHeight()*0.73f);
	}

	@Override
	public boolean paint(Graphics2D g2d, float useThisAlpha) {
		assert World.getPlayers().size() == 6 : "There should be 6 players";
		int i=0;
		final float gapWidth = getWidth() * 0.05f;
		final float gapHeight = getHeight() * 0.005f;
		final float smallWidth = (getWidth()-gapWidth )/ 2;
		final float smallHeight = (getHeight()-gapHeight*2 ) /3; 
		final float margin = 0.02f;
		final float innerSmallWidth = smallWidth*(1-2*margin);
		final float innerSmallHeight = smallHeight*(1-2*margin); 
		final float alphaBackGround = 0.5f;
		final float alphaBorder = 0.8f;
		final float cardWidth = innerSmallHeight*this.cardHeightPerc*this.cardRatio;
		final float cardHeight = cardWidth/this.cardRatio;
		final float cardMarginWidth = (innerSmallWidth-cardWidth)/(Math.max(core.Constants.INIT_COUNTRIES_NEUTRAL, core.Constants.INIT_COUNTRIES_PLAYER)-1);
		
		final float cardDeckX = -cardWidth/2+cardDeckXPerc*(getWidth()) ;
		final float cardDeckY = -cardHeight/2+cardDeckXPerc*(getHeight());

		
		
		for ( Player player: World.getPlayers() ){
			int row = i/2;
			int col = i%2;
			float x = col*smallWidth + col*gapWidth;
			float y = row*smallHeight + row*gapHeight;
			playerCardOwning[i] = 0;
			i++;
			
			Color color = FancyFullFrameAnimation.alphaColor(player.getColor(), alphaBackGround*useThisAlpha);
			g2d.setPaint(color);
			g2d.fillRect((int)x, (int)y, (int)smallWidth, (int)smallHeight);

			Stroke stroke = new BasicStroke(Math.min(smallWidth, smallHeight)*0.01f);
			color = FancyFullFrameAnimation.alphaColor(player.getColor(), alphaBorder*useThisAlpha);
			g2d.setPaint(color);
			g2d.setStroke(stroke);
			g2d.drawRect((int)x, (int)y, (int)smallWidth, (int)smallHeight);
		}
		Stroke stroke = new BasicStroke(2);
		for ( i=0; i<deck.length; i++ )
			if ( deck[i] != null ){
				State state = world.getState( deck[i].getIndex() );
				int player = state.getOwner().getId();
				int j = playerCardOwning[player] ++;
				int row = player/2;
				int col = player%2;
				float x = col*smallWidth + col*gapWidth + (smallWidth-innerSmallWidth)/2 + 
						(col==0?j*cardMarginWidth:(innerSmallWidth-j*cardMarginWidth-cardWidth));
				float y = row*smallHeight + row*gapHeight + (smallHeight-innerSmallHeight)/2 + (innerSmallHeight-cardHeight)/2;
				float point = Math.min(	1.0f, 0.3f*cardTimes[i]/singleDuration);
				point = (float)Math.pow(Math.sin(Math.PI*Math.pow(point,0.4)/2),2);//(float) Math.sqrt(point);
				x = cardDeckX + (x-cardDeckX)*point;
				y = cardDeckY + (y-cardDeckY)*(float)Math.sqrt(point);
				g2d.drawImage(deck[i].getImage(), (int)x, (int)y, (int)(cardWidth), (int)(cardWidth/this.cardRatio), null);
				g2d.setColor(Color.black);
				g2d.setStroke(stroke);
				g2d.drawRect((int)x, (int)y, (int)cardWidth, (int)(cardWidth/this.cardRatio));
				
			}
		if ( deck[deck.length-1] == null ){
			g2d.drawImage(TerritoryCard.getCoverImage(), (int)cardDeckX, (int)cardDeckY, (int)(cardWidth), (int)(cardWidth/this.cardRatio), null);
			g2d.setColor(Color.black);
			g2d.setStroke(stroke);
			g2d.drawRect((int)cardDeckX, (int)cardDeckY, (int)cardWidth, (int)(cardWidth/this.cardRatio));
		}
		return true;
	}

}
