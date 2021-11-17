package gui.state.java;

import java.awt.Graphics2D;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import gui.components.java.GuiFrame;

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
		currentLevel = new Level();
		currentLevel.playerSet.add(new ClientPlayerNPC(3, 3, "Bob"));
		System.out.println("Joined singleplayer!");

	}

	@Override
	public void stateUnloaded() {

	}

}
