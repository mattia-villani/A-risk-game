import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Genetic {
	static final int BOT_COUNT = 5;
	static final int WINNING_BOT = 3;
	static final double MUTATION_PROB = 0.5;
	
	
	static class Pair<T>{
		public T first, second ;
		public Pair(T first, T second){
			this.first = first;
			this.second = second;
		}
	}
	static class PointPair extends Pair<Integer> implements Comparable<PointPair>{

		public PointPair(Integer first, Integer second) {
			super(first, second);
		}

		@Override
		public int compareTo(PointPair arg0) {
			return - Integer.compare( second, arg0.second );
		}
		
	}
	
	
	static class RandomProfile extends Team42.RandomProfile{		

		static Pair<Team42.Profile> NEW_GENERATION_PROFILES( Team42.Profile p1, Team42.Profile p2 ){
			float[] f1 = p1.coefficients, f2 = p2.coefficients;
			int n = f1.length;
			int [] indexs = new int[n];
			for ( int i=0; i<n; i++ ) indexs[i] = i;
			for ( int i=0; i<n/2; i++){
				int index ;
				do{
					index = (int)(Math.random()*n);
				}while ( indexs[index] == -1 );
				
				float t = f1[ indexs[index] ];
				f1[ indexs[index] ] = f2[ indexs[index] ];
				f2[ indexs[index] ] = t;
								
				indexs[index] = -1;
			}
			if ( Math.random() < MUTATION_PROB/2 )
				f1[ (int)(Math.random()*n) ] = (float)Math.random();
			if ( Math.random() < MUTATION_PROB/2 )
				f2[ (int)(Math.random()*n) ] = (float)Math.random();

			return new Pair<Team42.Profile>(new Team42.Profile(f1), new Team42.Profile(f2));
		}
	}
	
	static Pair<List<Team42.Profile>> newSetGeneration(List<Team42.Profile> l1, List<Team42.Profile> l2){
		List<Team42.Profile> ret1 = new LinkedList<>();
		List<Team42.Profile> ret2 = new LinkedList<>();
		for ( int i=0;i<l1.size() && i<l2.size();i++ ){
			Pair<Team42.Profile> p = RandomProfile.NEW_GENERATION_PROFILES(l1.get(i), l2.get(i));
			ret1.add(p.first);
			ret2.add(p.second);
		}
		return new Pair<List<Team42.Profile>>( ret1, ret2 );
	}
	
	
	
	static class TestBot extends Team42{
		static int I = 0;
		int i;
		protected TestBot(BoardAPI inBoard, PlayerAPI inPlayer, List<Profile> profiles ) {
			super(inBoard, inPlayer, profiles.get(0), profiles.get(1), profiles.get(2) );
			this.i = I++;
		}
		
		@Override public String getName(){
			return "BOT_"+i;
		}
	}
	
	static List<Team42.Profile> new_profile_list(int n){
		List<Team42.Profile> list = new LinkedList<>();
		for ( int i=0; i<n; i++ ) list.add(new RandomProfile());
		return list;
	}
	
	
	public static void main (String args[]) throws InterruptedException {
		Team42.VERBOSE_TIME_POINT_SYSTEM = false;
		Team42.VERBOSE_BATTLE = false;
		Team42.VERBOSE_INPUT_LOCKER = false;
		Team42.VERBOSE_WAIT = true;
		List< List<Team42.Profile> > list = new LinkedList<>();
		PointPair[] points = new PointPair[ BOT_COUNT ];
		int N = 3;

		for ( int round=0; round<1000; round++){
			while ( list.size() < BOT_COUNT )
				list.add(new_profile_list(N) );

			for ( int i=0; i<BOT_COUNT; i++){
				points[i] = new PointPair ( i, 0 );
			}
			
			for ( int i=0; i<BOT_COUNT; i++ )
				for (int j=i+1;j<BOT_COUNT;j++){
					int winner = run ( list.get(i), list.get(j) );
					if ( winner == 0 ) winner = i ;
					else if (winner == 1 ) winner = j;
					if ( winner >= 0 ) // could be -1 (match unfinished)
						points[winner] = new PointPair( points[winner].first, points[winner].second+1 );
				}
			
			Arrays.sort( points );
			List< List<Team42.Profile> > old = new LinkedList<>(list);
			list.clear();
			System.out.println("Round "+round+" winners : ");
			for ( int i=0; i<Genetic.WINNING_BOT; i++ ){
				System.out.print( points[i].first + " won "+points[i].second+" matchs) ");
				dump ( old.get(points[i].first) );
				for ( int j=i+1; j<WINNING_BOT; j++ ){
					Pair<List<Team42.Profile>> p = newSetGeneration( old.get(points[i].first), old.get(points[j].first) );
					list.add( p.first );
					list.add( p.second );
				}
				System.out.println("");
			}			

		}
				
	}
	static void dump ( List<Team42.Profile> l){
		for ( Team42.Profile p : l ){
			for ( float f : p.coefficients )
				System.out.print(f+", ");
			System.out.print("; ");
		}		
	}
	
	
	static int randomRun( List<Team42.Profile> bot1Profiles, List<Team42.Profile> bot2Profiles ) throws InterruptedException{
		return (int)(Math.random()*2);
	}
	static int run( List<Team42.Profile> bot1Profiles, List<Team42.Profile> bot2Profiles ) throws InterruptedException{
		int MAX = 100;
		Board board = new Board();
		UI ui = new UI(board);
		Player[] players = new Player[GameData.NUM_PLAYERS_PLUS_NEUTRALS];
		Player currPlayer, otherPlayer, defencePlayer;
		Deck deck;
		Card card;
		ArrayList<Card> cards;
		int playerId, otherPlayerId, numUnits, numCards, attackUnits, defenceUnits;
		int countryId, attackCountryId, defenceCountryId, countriesInvaded;
		String name;
		
		ui.displayMap();
		for (playerId=0; playerId<GameData.NUM_PLAYERS_PLUS_NEUTRALS; playerId++) {
			players[playerId] = new Player (playerId);
			if (playerId == 0) {
				players[playerId].setBot(new TestBot(board,players[playerId], bot1Profiles));
			}
			if (playerId == 1) {
				players[playerId].setBot(new TestBot(board,players[playerId], bot2Profiles));
			}
			if (playerId < GameData.NUM_PLAYERS) {
				name = ui.inputName(players[playerId]);
				players[playerId].setName(name);
			} else {
				name = "Neutral " + (playerId - GameData.NUM_PLAYERS + 1);
				ui.displayName(playerId,name);
				players[playerId].setName(name);
			}
		}
		
		ui.displayString("\nDRAW TERRITORY CARDS FOR STARTING COUNTRIES");
		deck = new Deck(Deck.NO_WILD_CARD_DECK);
		for (playerId=0; playerId<GameData.NUM_PLAYERS_PLUS_NEUTRALS; playerId++) {
			currPlayer = players[playerId];
			if (playerId < GameData.NUM_PLAYERS) {
				numCards = GameData.INIT_COUNTRIES_PLAYER;
			} else {
				numCards = GameData.INIT_COUNTRIES_NEUTRAL;
			}
			for (int i=0; i<numCards; i++) {
				card = deck.getCard();
				ui.displayCardDraw(currPlayer, card);
				board.occupy(card.getCountryId(), currPlayer.getId());
				board.addUnits(card.getCountryId(), 1);
			}
		}
		ui.displayMap();
		
		ui.displayString("\nROLL DICE TO SEE WHO REINFORCES THEIR COUNTRIES FIRST");
		do {
			for (int i=0; i<GameData.NUM_PLAYERS; i++) {
				players[i].rollDice(1);
				ui.displayDice(players[i]);
			}
		} while (players[0].getDie(0) == players[1].getDie(0)); 
		if (players[0].getDie(0) > players[1].getDie(0)) {
			playerId = 0;
		} else {
			playerId = 1;
		}
		currPlayer = players[playerId];
		ui.displayRollWinner(currPlayer);
		
		ui.displayString("\nREINFORCE INITIAL COUNTRIES");
		for (int r=0; r<2*GameData.NUM_REINFORCE_ROUNDS; r++) {
			currPlayer.addUnits(3);
			ui.displayReinforcements(currPlayer);
			do {
				ui.inputReinforcement(currPlayer);
				currPlayer.subtractUnits(ui.getNumUnits());
				board.addUnits(ui.getCountryId(), ui.getNumUnits());
				ui.displayMap();
			} while (currPlayer.getNumUnits() > 0);
			ui.displayMap();
			for (int p=GameData.NUM_PLAYERS; p<GameData.NUM_PLAYERS_PLUS_NEUTRALS; p++) {
				ui.inputPlacement(currPlayer, players[p]);
				countryId = ui.getCountryId();
				board.addUnits(countryId, 1);	
				ui.displayMap();
			}
			playerId = (++playerId)%GameData.NUM_PLAYERS;
			currPlayer = players[playerId];
		}
			
		ui.displayString("\nROLL DICE TO SEE WHO TAKES THE FIRST TURN");
		do {
			for (int i=0; i<GameData.NUM_PLAYERS; i++) {
				players[i].rollDice(1);
				ui.displayDice(players[i]);
			}
		} while (players[0].getDie(0) == players[1].getDie(0)); 
		if (players[0].getDie(0) > players[1].getDie(0)) {
			playerId = 0;
		} else {
			playerId = 1;
		}
		currPlayer = players[playerId];
		ui.displayRollWinner(currPlayer);
		
		deck = new Deck(Deck.WILD_CARD_DECK);		
		
		// TEST CODE TO GIVE PLAYERS 6 CARDS TO START WITH
//		for (int i=0; i<GameData.NUM_PLAYERS; i++) {
//			for (int j=0; j<6; j++) {
//				card = deck.getCard();
//				players[i].addCard(card);
//				ui.displayCardDraw(players[i],card);
//			}
//		}
		
		int count = 0;
		ui.displayString("\nSTART TURNS");
		do {
			otherPlayerId = (playerId+1)%GameData.NUM_PLAYERS;
			otherPlayer = players[otherPlayerId];
			
			// 1. Reinforcements from occupied countries & continents
			numUnits = board.calcReinforcements(currPlayer);
			currPlayer.addUnits(numUnits);
			ui.displayReinforcements(currPlayer);
			// 1. Reinforcements from cards
			if (!currPlayer.isOptionalExchange()) {	
				ui.displayCards(currPlayer);
				ui.displayCannotExchange(currPlayer);
			} else {
				do {
					ui.displayCards(currPlayer);
					ui.inputCardExchange(currPlayer);		
					if (!ui.isTurnEnded()) {
						board.calcCardExchange(currPlayer);
						cards = currPlayer.subtractCards(ui.getInsigniaIds());
						deck.addCards(cards);
						ui.displayReinforcements(currPlayer);
					}
				} while (currPlayer.isOptionalExchange() && !ui.isTurnEnded());
				if (!currPlayer.isOptionalExchange() && !ui.isTurnEnded()) {
					ui.displayCannotExchange(currPlayer);					
				}
			} 
			do {
				ui.displayReinforcements(currPlayer);
				ui.inputReinforcement(currPlayer);
				currPlayer.subtractUnits(ui.getNumUnits());
				board.addUnits(ui.getCountryId(),ui.getNumUnits());	
				ui.displayMap();
			} while (currPlayer.getNumUnits() > 0);

			// 2. Combat
			countriesInvaded = 0;
			do {
				ui.inputBattle(currPlayer);
				if (!ui.isTurnEnded()) {
					attackUnits = ui.getNumUnits();
					attackCountryId = ui.getFromCountryId();
					defenceCountryId = ui.getToCountryId();
					defencePlayer = players[board.getOccupier(defenceCountryId)];
					if (board.getNumUnits(defenceCountryId) > 1) {
						ui.inputDefence(otherPlayer,defenceCountryId);
						defenceUnits = ui.getNumUnits();
					} else {
						defenceUnits = 1;
					}
					board.calcBattle(currPlayer,defencePlayer,attackCountryId,defenceCountryId,attackUnits,defenceUnits);
					ui.displayBattle(currPlayer,defencePlayer);
					ui.displayMap();
					if (board.isInvasionSuccess()) {
						countriesInvaded++;						
					}
					if ( board.isInvasionSuccess() && (board.getNumUnits(attackCountryId) > 1) ) {
						ui.inputMoveIn(currPlayer,attackCountryId);
						board.subtractUnits(attackCountryId, ui.getNumUnits());
						board.addUnits(defenceCountryId, ui.getNumUnits());
						ui.displayMap();
					}
					if ( board.isInvasionSuccess() && (board.isEliminated(defencePlayer.getId())) ) {
						cards = defencePlayer.removeCards();
						currPlayer.addCards(cards);
						ui.displayCardsWon(currPlayer,defencePlayer,cards);  // No cards received from passive neutrals
						while (currPlayer.isForcedExchange()) {
							ui.displayCards(currPlayer);
							ui.inputCardExchange(currPlayer);
							board.calcCardExchange(currPlayer);
							cards = currPlayer.subtractCards(ui.getInsigniaIds());
							deck.addCards(cards);
							ui.displayReinforcements(currPlayer);
						}
					}
				} 
				
			} while (!ui.isTurnEnded() && !board.isGameOver());

			// 3. Fortify
			if (!board.isGameOver()) {
				ui.inputFortify(currPlayer);
				if (!ui.isTurnEnded()) {
					board.subtractUnits(ui.getFromCountryId(), ui.getNumUnits());
					board.addUnits(ui.getToCountryId(), ui.getNumUnits());
					ui.displayMap();
				}
			}			
			
			// 4. Territory Card
			if (countriesInvaded > 0) {
				card = deck.getCard();
				currPlayer.addCard(card);
				ui.displayCardDraw(currPlayer,card);
			}

			int old = currPlayer.getId();
			playerId = (playerId+1)%GameData.NUM_PLAYERS;
			currPlayer = players[playerId];			

			if ( (count++) == MAX ) return -1;
			boolean thereIsACountry = false;
			for ( int i=0;!thereIsACountry && i<GameData.NUM_COUNTRIES; i++)
				thereIsACountry = currPlayer.getId() == board.getOccupier(i);
			if ( ! thereIsACountry ) return old;

		} while (!board.isGameOver() );
		
		ui.displayWinner(players[board.getWinner()]);
		ui.displayString("GAME OVER");
	
		ui = null;

		java.awt.Window win[] = java.awt.Window.getWindows(); 
		for(int i=0;i<win.length;i++){ 
			win[i].dispose(); 
		} 
		
		return board.getWinner();
	}
	
	
}
