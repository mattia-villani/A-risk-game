package core;

import core.Constants;
import core.entities.Player;
import core.entities.State;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.GUI;
import gui.Notification;
import oracle.Tree;

public class ReinforcementPhase {
	//static GUI gui;
	//static World world;
	
	static void performPhase( Player player, World world, GUI gui ){
	//	ReinforcementPhase.gui = gui;
		//ReinforcementPhase.world=world;
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
		
			//setReinforcements(command);
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
	
	/*public static void setReinforcements (String stateName){
		world.getStateByName(stateName).setArmy(world.getStateByName(stateName).getArmy() + 1);
		gui.refreshMap();
		return;

	}*/
	
}
