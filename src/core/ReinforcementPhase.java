package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import core.AttackManager.BreakException;
import core.AttackManager.OutOfContextException;
import core.AttackManager.Question;
import core.Constants;
import core.entities.Player;
import core.entities.State;
import core.entities.TerritoryCard;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.GUI;
import gui.Notification;
import gui.Toast;
import oracle.Oracle;
import oracle.Tree;

public class ReinforcementPhase {
	static GUI gui = null;
	static abstract public class ConfQuestion extends AttackManager.Question<String>{
		boolean skipAllowed = true;
		final public void setSkipAllowed(boolean skipAllowed){ this.skipAllowed = skipAllowed; }		
		@Override 
		public Oracle createExtendedOracle(Set<String> context, String prefix){ return super.createExtendedOracle(context, prefix, skipAllowed); }		
	}
	static public class HumanConfQuestion extends ConfQuestion{
		@Override
		public String askQuestion(Set<String> context, String title)
				throws OutOfContextException, RuntimeException, BreakException {
			new Toast(title,Toast.LONG);
			gui.setText(title);
			gui.enableOracle(super.createExtendedOracle(context, "Chose a card conf"));
			try{
				String ret = throwExceptionsIfControlsAreUsed(gui.getCommand());
				gui.disableOracle();
				return ret;
			}catch( Exception e ){
				gui.disableOracle();
				throw e;
			}
		}
	}
	
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
	
	static Set<String> getPossibleCommands(List<String[]> confs){
		Set<String> set = new HashSet<String>();
		for (String[] str: confs){
			String com = "";
			for ( String s:str) com+=s.charAt(0);
			set.add(com);
		}		
		return set;
	}
	static int getValueInArmyOf(String str){
		return 1; //TODO!
	}
	static List<String[]> getPossibleConfigurations(String[] types){
		List<String[]> result = new LinkedList<>();
		if ( types.length >= 3 ) 
			for ( String[] conf : CONFIGURATIONS ){
				int conf_i = 0;
				for ( int i=0; i<types.length && conf_i <=3 ; i++ )
					if ( types[i].equals(conf[conf_i]) ) conf_i++;
				if ( conf_i == 3 ) result.add(conf);
			}
		return result;
	}
	
	static void notify(String str){
		new Toast.ErrorToast(str, Toast.SHORT);
	}
	
	static int performChangeOfCardPhase(Player player, World world, GUI gui, Class<? extends ConfQuestion > questionClass){
		ReinforcementPhase.gui = gui;
		List<TerritoryCard> hand = player.getHand();
		String[] types = new String[hand.size()];
		int i=0;
		for ( TerritoryCard card : hand ) types[i++] = card.getCardType();
		List<String[]> possibilities = getPossibleConfigurations(types);
		Set<String> commands = getPossibleCommands(possibilities);
		if ( commands.isEmpty() ) return 0;
		String result = null;
		boolean toManyCards = hand.size()>=5;
		String title = "Chose the configuration to use";
		boolean looping = true;
		ConfQuestion choseWhatToChangeQuestion = null;
		try { choseWhatToChangeQuestion = questionClass.newInstance(); } catch (InstantiationException | IllegalAccessException e1) {e1.printStackTrace();}
		choseWhatToChangeQuestion.setSkipAllowed( ! toManyCards);
		if ( toManyCards ) 
			while ( looping ) 
				try{
					result = choseWhatToChangeQuestion.notTrivialValidatedAskQuestion(commands, title);
					looping = false;
				}catch(AttackManager.ChangeOfMindException e){ notify("Can't perform this action"); 
				}catch(AttackManager.BreakException e){ notify("Can't perform this action"); }
		else result = choseWhatToChangeQuestion.askQuestion(commands, title);
		if ( World.returnCardsToDeck (player, result) == false )
			throw new RuntimeException("This is strange...");
		return getValueInArmyOf(result);
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
		
		reinforcements += surpluss;
		
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
				
		player.setNumArmies(player.getNumArmies()+reinforcements+continentBonus);
			
		// Generate tree.
		Tree tree = null;
		if (tree == null) tree=gui.enableOracleAndReturnTree( world, player );
		String command;
		gui.toggleMouseInput();

		for (int i=0; i<reinforcements+continentBonus; i++)
		{
			gui.setText(player.getName()+" recieves "+reinforcements+" reinforcements from territories owned");
			if (continentBonus > 0) gui.addText(" and a bonus of "+continentBonus+" from continents owned");
			gui.addText(".");
			gui.addTextln("Please choose a country to reinforce. Reinforcements remaining: "+(reinforcements+continentBonus-i));
			
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
