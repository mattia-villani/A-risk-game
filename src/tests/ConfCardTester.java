package tests;

import java.util.Set;

import core.ReinforcementPhase;
import core.entities.Player;
import core.entities.QuestionsForGenericPlayer.ConfQuestion;
import core.entities.TerritoryCard;
import core.entities.World;
import gui.GUI;

public class ConfCardTester {

	public static void testCards( World world, GUI gui, Player player ){
		TerritoryCard 
			I1 = new TerritoryCard(37),
			I2 = new TerritoryCard(39),
			I3 = new TerritoryCard(41),
			I4 = new TerritoryCard(3),
			C1 = new TerritoryCard(36),
			A1 = new TerritoryCard(40),
			A2 = new TerritoryCard(18),
			W1 = new TerritoryCard(42,true);
		
		
		// TEST 1 giving no cards.
		player.getHand().clear();
		int r = ReinforcementPhase.performChangeOfCardPhase(player, world, gui, new ConfQuestion(){
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				System.err.println("TEST 1 FAILED. Since there are no cards, this questions shouldn't be asked.");
				throw new ChangeOfMindException();
			}
		});
		if ( r != 0 ) System.err.println("TEST 1 FAILED at point 2");
		else System.out.println("TEST 1 maybe passed");

		
		// TEST 2 giving not the proper cards.
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(A1);
		player.addToHand(A2);
		r = ReinforcementPhase.performChangeOfCardPhase(player, world, gui, new ConfQuestion(){
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				System.err.println("TEST 2 FAILED. Since there are no confs possibles, this questions shouldn't be asked.");
				throw new ChangeOfMindException();
			}
		});
		if ( r != 0 ) System.err.println("TEST 2 FAILED at point 2");
		else System.out.println("TEST 2 maybe passed");

		// TEST 3 giving III cards.
		player.getHand().clear();
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(I3);
		player.addToHand(A1);
		player.addToHand(A2);
		r = ReinforcementPhase.performChangeOfCardPhase(player, world, gui, new ConfQuestion(){
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				if ( context.contains("III") == false || context.size() >= 3 ) // skip and break are controls 
					System.err.println("TEST 3 FAILED. III and only it should be asked.");
				else System.out.print("Test 3, TO PASS IT YOU HAVE TO SEE THIS MESSAGE");
				return "III";
			}
		});
		System.out.println("Test 3 maybe was passed with value "+r);

		
	}
	
	
}
