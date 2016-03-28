package core;

import java.util.ArrayList;

import core.entities.Player;
import core.entities.State;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.GUI;
import gui.Notification;
import gui.Toast;
import oracle.Oracle;
import oracle.Tree;

public class MovePhase {

static void moveArmies(Player player, World world, GUI window) throws InterruptedException{
	new Notification(FancyFullFrameAnimation.frame, "Move Phase", player, Notification.SHORT);

	boolean done = false;
	boolean skipped = false;
	String stringNumMoved = null;
	String giverString = null;
	String getterString = null;
	int numMoved = 0;
	Tree tree = null;
	if (tree == null) tree = window.enableOracleAndReturnTreeForMove( world, (player));

	while (!done){
		window.setText(player.getName() + ", please choose a country to move armies from, or type skip to end turn");
		giverString = window.getCommand();
		State giver = world.getStateByName(giverString);
		if (giverString.equals("skip")){
			skipped = true;
			done = true;
		}
		else if(giverString.length() < 4){
			window.setText("Ambiguous input, try again");
			Thread.sleep(1000);
		}
		else if (giver.getArmy() == 1) {
			window.setText("Giving State needs to have more than one army.");
			Thread.sleep(1000);
		}
		else
		{

			window.setText("Now choose a connected country to move armies to");
			getterString = window.getCommand();

			State getter = world.getStateByName(getterString);
			if (isConnected(giver, getter, player, new ArrayList<State>(), world)){

				done = true;
				window.disableOracle();

			}
			else {
				window.setText("Both countries need to be a country of yours that is connected. Try again.");
				Thread.sleep(1000);
			}

		}	
	}		
	boolean doneInner = false;


	while (!doneInner && !skipped){
		State giver = world.getStateByName(giverString);
		State getter = world.getStateByName(getterString);

		window.setText("And how many countries do you like moved?");
		stringNumMoved = window.getCommand();
		try
		{
			numMoved = Integer.parseInt(stringNumMoved);
			if (numMoved >= giver.getArmy()){
				window.setText("Needs to be a number less than " + giver.getArmy());
				Thread.sleep(1000);
			}
			else{
				doneInner = true;
			}
		} catch (NumberFormatException ex)
		{
			window.setText("Needs to be a number!");
			Thread.sleep(1000);

		}

		if (doneInner){
			getter.setArmy(getter.getArmy() + numMoved);
			giver.setArmy(giver.getArmy() - numMoved);
			window.refreshMap();
		}
	}
		new Notification(FancyFullFrameAnimation.frame, "Move Phase ended", player, Notification.SHORT);


	}

	public static boolean isConnected(State start, State end, Player owner, ArrayList<State> visited, World world){
		if (start.getOwner() != owner) return false;
		for (int state : start.getAdjacent()){
			visited.add(start);
			if (end == world.getState(state) && end.getOwner() == owner) return true;
			else if (world.getState(state).getOwner() == owner && !visited.contains(world.getState(state))){
				return isConnected (world.getState(state), end, owner, visited, world);
			}
		}
		return false;
	}
}