package tests;

import java.util.Arrays;
import java.util.Set;

import core.ReinforcementPhase;
import core.entities.Player;
import core.entities.QuestionsForGenericPlayer.ConfQuestion;
import core.entities.TerritoryCard;
import core.entities.World;
import gui.GUI;

public class ConfCardTester {

	private static String toExtendedString(Set<String> context){
		StringBuilder sb = new StringBuilder();
		for (String str : context)
			sb.append(str+" ");
		return sb.toString();
	}
	
	private static int handleErrors(Player player, World world, GUI gui, ConfQuestion conf){
		try{
			return ReinforcementPhase.performChangeOfCardPhase(player, world, gui, conf);
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
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
		int r = handleErrors(player, world, gui, new ConfQuestion(){
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				System.err.println("****TEST 1 FAILED. Since there are no cards, this questions shouldn't be asked.");
				throw new ChangeOfMindException();
			}
		});
		if ( r != 0 ) System.err.println("****TEST 1 FAILED at point 2");
		else System.err.println("TEST 1 maybe passed");

		
		// TEST 2 giving not the proper cards.
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(A1);
		player.addToHand(A2);
		r = handleErrors(player, world, gui, new ConfQuestion(){
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				System.err.println("****TEST 2 FAILED. Since there are no confs possibles, this questions shouldn't be asked.");
				throw new ChangeOfMindException();
			}
		});
		if ( r != 0 ) System.err.println("****TEST 2 FAILED at point 2");
		else System.err.println("TEST 2 maybe passed");

		// TEST 3 giving iii cards.
		player.getHand().clear();
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(I3);
		player.addToHand(A2);
		r = handleErrors(player, world, gui, new ConfQuestion(){
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				if ( context.contains("iii") == false || context.size() >= 3 ) // skip and break are controls 
					System.err.println("****TEST 3 FAILLED. iii and only it should be asked. context.size="+context.size());
				else System.err.print("TEST 3 maybe passed, TO PASS IT YOU HAVE TO SEE THIS MESSAGE");
				return "iii";
			}
		});
		System.err.println("Test 3 maybe (YOU HAVE TO HAVE SEEN A MEESSAGE SAING TO IT HAD TO BE SEEN) was passed with value "+r);
		
		
		// TEST 4 giving iii cards but question is trivial and due.
		player.getHand().clear();
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(I3);
		player.addToHand(A1);
		player.addToHand(A2);
		r = handleErrors(player, world, gui, new ConfQuestion(){			
			@Override
			public String notTrivialValidatedAskQuestion(Set<String> context, String title){
				String retval = super.notTrivialValidatedAskQuestion(context, title);
				if ( context.contains("iii") && context.size()==1 )
					System.err.println("TEST 4 maybe is ok; "+retval);
				else System.err.println("****TEST 4 failled.");
				return retval;
			}
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				System.err.println("****TEST 4 FAILLED.");
				return "iii";
			}
		});
		System.err.println("Test 4 maybe was passed with value "+r);

		
		// TEST 5 giving multiple options.
		player.getHand().clear();
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(I3);
		player.addToHand(A1);
		player.addToHand(A2);
		player.addToHand(C1);
		player.addToHand(W1);
		r = handleErrors(player, world, gui, new ConfQuestion(){			
			@Override
			public String askQuestion(Set<String> context, String title)
					throws core.entities.Question.OutOfContextException, RuntimeException,
					core.entities.Question.BreakException {
				String[] options="AWA CWA AWC IWA AWI IWC CWI CIA IWI AIC ICA ACI III CAI IAC WIA AIW CIW WIC WCA ACW WAA WII IIW AAW CAW WAC WCI ICW WAI IAW".toLowerCase().split(" ");
				if ( context.containsAll( Arrays.asList(options) ) == false )
					System.err.println("***TEST 5 FAILLED at point 1.");
				if ( context.size() != options.length )
					System.err.println("***TEST 5 FAILLED at point 2. options : \""+toExtendedString(context)+"\"");
				return "iii";
			}
		});
		System.err.println("Test 5 maybe was passed with value "+r);

		// keep the game test with.
		player.getHand().clear();
		World.GoldenCav=0;
		player.addToHand(I1);
		player.addToHand(I2);
		player.addToHand(I3);
		player.addToHand(A1);
		
	}
	
	
}
