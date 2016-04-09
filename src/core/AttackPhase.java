package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.entities.Player;
import core.entities.Question;
import core.entities.State;
import core.entities.World;
import gui.FancyFullFrameAnimation;
import gui.GUI;
import gui.Notification;
import gui.Toast;
import oracle.Oracle;

public class AttackPhase {
	static GUI gui;	
	
	// returns the list of players defeded.
	static List<Player> performPhase( Player player, World world, GUI gui ){
		AttackPhase.gui = gui;
		gui.toggleMouseInput();
		new Toast.SuperToast("Attack Phase", player, Notification.LONG);
//		new Notification(FancyFullFrameAnimation.frame, "Attack Phase ended", player, Notification.SHORT);
		List<Player> losers = new AttackManager(player, world).attackLoop( player.getQuestions(gui) );
		new Toast.SuperToast("Attack Phase ended", player, Notification.LONG);
		gui.disableOracle();
		gui.resetText();
		gui.toggleMouseInput();
		gui.clearCommands();
		return losers;
	}
	
}
