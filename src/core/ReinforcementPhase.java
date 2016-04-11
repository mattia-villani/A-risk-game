package core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import core.entities.Question.BreakException;
import core.entities.Question.ChangeOfMindException;
import core.entities.QuestionsForGenericPlayer.ConfQuestion;
import core.Constants;
import core.entities.Player;
import core.entities.State;
import core.entities.TerritoryCard;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.GUI;
import gui.Notification;
import gui.Toast;
import oracle.Tree;

public class ReinforcementPhase {
	static GUI gui = null;
	
	static private final String A = "ARTILLERY", C = "CAVALRY", I = "INFANTRY", W = "WILD";
	static private final String[][] CONFIGURATIONS = new String[][]{ //INFANTRY, CAVALRY, ARTILLERY or WILD
		new String[]{A,A,A}, new String[]{A,A,W}, new String[]{A,W,A}, new String[]{W,A,A}, new String[]{A,W,W}, new String[]{W,A,W}, new String[]{W,W,A}, 
		new String[]{I,I,I}, new String[]{I,I,W}, new String[]{I,W,I}, new String[]{W,I,I}, new String[]{I,W,W}, new String[]{W,I,W}, new String[]{W,W,I}, 
		new String[]{C,C,C}, new String[]{C,C,W}, new String[]{C,W,C}, new String[]{W,C,C}, new String[]{C,W,W}, new String[]{W,C,W}, new String[]{W,W,C}, 
		new String[]{A,C,I}, new String[]{W,C,I}, new String[]{A,W,I}, new String[]{A,C,W},
		new String[]{A,I,C}, new String[]{W,I,C}, new String[]{A,W,C}, new String[]{A,I,W}, 
		new String[]{I,A,C}, new String[]{W,A,C}, new String[]{I,W,C}, new String[]{I,A,W},
		new String[]{I,C,A}, new String[]{W,C,A}, new String[]{I,W,A}, new String[]{I,C,W}, 
		new String[]{C,A,I}, new String[]{W,A,I}, new String[]{C,W,I}, new String[]{C,A,W}, 
		new String[]{C,I,A}, new String[]{W,I,A}, new String[]{C,W,A}, new String[]{C,I,W}    
	};
	
	static Set<String> getPossibleCommands(Set<String[]> confs){
		Set<String> set = new HashSet<String>();
		for (String[] str: confs){
			String com = "";
			for ( String s:str) com+=s.toLowerCase().charAt(0);
			set.add(com);
		}		
		return set;
	}
	static Set<String[]> getPossibleConfigurations(String[] types){
		Set<String[]> result = new HashSet<>();
		/*
		 * OK... not that awesome, sorry!  
		 * Complexity = m*n^3 where n = types.lenght && m = CONFIGURATIONS.length
		 * Quite a lot... but n should never be higher than 5 and
		 * 		n^3 is actually the rounded value of n*(n-1)*(n-2) so in worst case scenario n^3 is actually 5*4*3 = 60.
		 * */
		if ( types.length >= 3 ) 
			for (int i=0; i<types.length; i++)
				for (int j=0; j<types.length; j++)
					if ( j!=i)
						for (int k=0; k<types.length; k++)
							if ( k!=j && k!=i )
								for ( String[] conf : CONFIGURATIONS )
									if (  conf[0].equals(types[i])
									   && conf[1].equals(types[j])
									   && conf[2].equals(types[k]))
										result.add(conf);
		return result;
	}
	
	static void notify(String str){
		new Toast.ErrorToast(str, Toast.SHORT);
	}
	
	public static int performChangeOfCardPhase(Player player, World world, GUI gui, ConfQuestion confQuestion){
		ReinforcementPhase.gui = gui;
		List<TerritoryCard> hand = player.getHand();
		String[] types = new String[hand.size()];
		int i=0;
		for ( TerritoryCard card : hand ) types[i++] = card.getCardType();
		Set<String[]> possibilities = getPossibleConfigurations(types);
		Set<String> commands = getPossibleCommands(possibilities);
		if ( commands.isEmpty() ) return 0;
		String result = null;
		boolean toManyCards = hand.size()>=5;
		String title = "";
		if (toManyCards) title = "Please select which Reinforcement cards you would like to use (no skipping)";
		else title = "Please select which Reinforcement cards you would like to use or type SKIP.";
		boolean looping = true;
		ConfQuestion choseWhatToChangeQuestion = confQuestion;
		choseWhatToChangeQuestion.setSkipAllowed( ! toManyCards);
		if ( toManyCards ) {
			while ( looping ) 
				try{
					result = choseWhatToChangeQuestion.notTrivialValidatedAskQuestion(commands, title).toLowerCase();
					looping = false;
				}catch(ChangeOfMindException e){ notify("You have 5 cards, you must choose 3 to use"); 
				}catch(BreakException e){ notify("You have 5 cards, you must choose 3 to use"); }
		}
		else try{
			result = choseWhatToChangeQuestion.askQuestion(commands, title).toLowerCase();
		}catch(ChangeOfMindException|BreakException e){ return 0; }
		int armiesGiven =  World.returnCardsToDeck (player, result);
		if ( armiesGiven == 0 ){
			String str = "This is strange... and this is the dump: \n\t";
			str += "Player("+player.getName()+"), the conf he wanna use is "+result+
					"\n\t\tCards he owns: ";
			for ( TerritoryCard card : player.getHand() )
				str+="\n\t\t\t"+card.getCardType()+",\t"+card.getStateName();
			throw new RuntimeException(str);	
		}
		gui.disableOracle();
		gui.resetText();
		gui.clearCommands();
		gui.displayPlayerHands();
		return armiesGiven;
	}
	
	static void performPhase( Player player, World world, GUI gui, int surpluss ){
		int reinforcements=0;
		int continentBonus=0;
		int continentNA=0;
		int continentEU=0;
		int continentAS=0;
		int continentAU=0;
		int continentSA=0;
		int continentAF=0;
		
		
		new Notification(FancyFullFrameAnimation.frame, "Reinforcement Phase", player, Notification.SHORT);
		
		
		// Assign reinforcements for total states owned.
		for ( State state : world.getStates() ){
			if (state.getOwner().equals(player)) reinforcements++;
		}
		reinforcements=reinforcements/3; //integer division	
		if (reinforcements < 3) reinforcements=3;
		
		
		// Assign bonus reinforcements for continents owned
		for (int i=0; i <Constants.NUM_COUNTRIES; i++){
			if (Constants.CONTINENT_IDS[i]==0 && world.getState(i).getOwner().equals(player)){
				continentNA++;
				if (continentNA==9) continentBonus+=Constants.CONTINENT_VALUES[0];
			}
			if (Constants.CONTINENT_IDS[i]==1 && world.getState(i).getOwner().equals(player)){
				continentEU++;
				if (continentEU==7) continentBonus+=Constants.CONTINENT_VALUES[1];
			}
			if (Constants.CONTINENT_IDS[i]==2 && world.getState(i).getOwner().equals(player)){
				continentAS++;
				if (continentAS==12) continentBonus+=Constants.CONTINENT_VALUES[2];
			}
			if (Constants.CONTINENT_IDS[i]==3 && world.getState(i).getOwner().equals(player)){
				continentAU++;
				if (continentAU==4) continentBonus+=Constants.CONTINENT_VALUES[3];
			}
			if (Constants.CONTINENT_IDS[i]==4 && world.getState(i).getOwner().equals(player)){
				continentSA++;
				if (continentSA==4) continentBonus+=Constants.CONTINENT_VALUES[4];
			}
			if (Constants.CONTINENT_IDS[i]==5 && world.getState(i).getOwner().equals(player)){
				continentAF++;
				if (continentAF==6) continentBonus+=Constants.CONTINENT_VALUES[5];
			}
		}
		
		// Assign reinforcements for cards used (not done yet) - Sprint 4
				
		player.setNumArmies(player.getNumArmies()+reinforcements+continentBonus+surpluss);
			
		// Generate tree.
		Tree tree = null;
		if (tree == null) tree=gui.enableOracleAndReturnTree( world, player );
		String command;
		gui.toggleMouseInput();
		String glue="and";

		for (int i=0; i<reinforcements+continentBonus+surpluss; i++)
		{
			gui.setText(player.getName()+" recieves "+reinforcements+" reinforcements from territories owned ");
			if (surpluss > 0) glue=",";
			if (continentBonus > 0) gui.addText(glue+" a bonus of "+continentBonus+" from continents owned ");
			glue ="and";
			if (surpluss > 0) gui.addText(glue+" a bonus of "+surpluss+" from cards");

			gui.addText(".");
			gui.addTextln("Please choose a country to reinforce. Reinforcements remaining: "+(reinforcements+continentBonus+surpluss-i));
			
			// Handling bad text or mouse inputs
			String badText=gui.getText()+"\nPlease make sure input is unambiguous and not blank.";
			String badClick=gui.getText()+"\nPlease select one of your own countires.";

			while ( ((command = gui.getCommand()).length() < 4) || (world.getStateByName(command).getOwner()!=player) ) {				
				if(command.length()<4){
					gui.setText(badText);
				}
				else
				{
					gui.setText(badClick);
				}
			}
			world.getStateByName(command).setArmy(world.getStateByName(command).getArmy() + 1);
			gui.refreshMap();
		}
		
		gui.disableOracle();
		gui.resetText();
		gui.toggleMouseInput();
		gui.clearCommands();
		
		new Notification(FancyFullFrameAnimation.frame, "Reinforcement Phase ended", player, Notification.SHORT);

		return;
	}	
}
