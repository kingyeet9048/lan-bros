package gui.state.java;

import java.awt.Graphics2D;
import java.io.File;

import content.level.java.Level;
import gui.components.java.GuiFrame;
import main.java.Main;

public class InMultiplayerGameState extends GuiState {

    public String thisPlayerName;
    int syncCounter = 100;

    public InMultiplayerGameState(GuiFrame frame, String thisPlayerName) {
        super(frame);
        this.thisPlayerName = thisPlayerName;
    }

    @Override
    public void renderPre(Graphics2D g) {
        Level level = Main.getNetworkFactory().getCurrentClient().getCurrentLevel();
        level.updateLevel();
        level.renderLevel(g);

        --syncCounter;

        if (syncCounter <= 0) {
            syncCounter = 100;
            Main.getNetworkFactory().getCurrentClient().syncPlayerCoordinates();
        }
    }

    @Override
    public void renderPost(Graphics2D g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateLoaded() {
    	Level level = new Level(new File("resources/level/level0.data"));
        Main.getNetworkFactory().getCurrentClient().setCurrentLevel(level);
    }

    @Override
    public void stateUnloaded() {
        // TODO Auto-generated method stub

    }

}
