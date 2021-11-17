package gui.state.java;

import java.awt.Graphics2D;
import java.io.File;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import gui.java.GuiFrame;

public class IngameState extends GuiState {
	public Level currentLevel;

	public IngameState(GuiFrame frame) {
		super(frame);
	}

	@Override
	public void renderPre(Graphics2D g) {
		currentLevel.updateLevel();
		currentLevel.renderLevel(g);
	}

	@Override
	public void renderPost(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateLoaded() {
		currentLevel = new Level(new File("resources/level/level0.data"));
		currentLevel.playerSet.add(new ClientPlayerNPC(128, 128, "Bob"));
		System.out.println("Joined singleplayer!");

	}

	@Override
	public void stateUnloaded() {

	}

}
