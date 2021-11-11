package cs410.lanbros.gui.state;

import java.awt.Graphics2D;
import java.io.File;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.gui.GuiFrame;

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
		currentLevel.playerSet.add(new ClientPlayerNPC(3, 3, "Bob"));
		System.out.println("Joined singleplayer!");

	}

	@Override
	public void stateUnloaded() {

	}

}
