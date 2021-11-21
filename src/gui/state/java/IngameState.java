package gui.state.java;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import gui.components.java.GuiFrame;

import java.awt.*;
import java.io.File;

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
		ClientPlayerNPC playerNPC = new ClientPlayerNPC(currentLevel, 128, 128, "Bob");
		playerNPC.canMove = true;
		currentLevel.playerSet.add(playerNPC);
		System.out.println("Joined singleplayer!");
	}

	@Override
	public void stateUnloaded() {

	}

}
