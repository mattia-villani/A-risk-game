package core.entities;

import java.awt.Color;

import gui.GUI;

public class HumanPlayer extends Player {

	public HumanPlayer(int id, String name, Color color) {
		super(id, name, color);
	}

	@Override
	public QuestionsForGenericPlayer getQuestions(GUI gui) {
		return new QuestionsForHuman(gui);
	}

}
