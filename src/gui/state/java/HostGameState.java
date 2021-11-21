package gui.state.java;

import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import gui.components.java.GuiInput;
import main.java.Main;
import networkhandler.shared.java.Factory;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.io.IOException;

public class HostGameState extends GuiState {

    private Rectangle screenSize;
    public Factory factory;

    public HostGameState(GuiFrame frame, Factory factory) {
        super(frame);
        Main.startServer();
        this.factory = factory;
        inputs = new GuiInput[] { new GuiInput(factory.getCurrentServer().getIpAddress()) {
            @Override
            public void focusGained(FocusEvent e) {

            }
        } };
        inputs[0].setEditable(false);
        buttons = new GuiButton[] { new GuiButton("Start Game") {

            @Override
            public void onClick(boolean pressed) {
                if (pressed) {
                    if (Main.startClient("localhost")) {
                        factory.setHost(true);
                        factory.getCurrentClient().setHost(true);
                        factory.getCurrentClient().tellClientsToStart();
                    }
                }
            }

        }, new GuiButton("Go Back to Title") {

            @Override
            public void onClick(boolean pressed) {
                try {
                    factory.getCurrentServer().getServer().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                frame.addActiveState(new TitleState(frame, factory));
                frame.removeActiveState(HostGameState.this);
            }

        } };
    }

    @Override
    public void renderPre(Graphics2D g) {
        g.setColor(new Color(0, 0, 20, 100));
        g.fillRect(0, 0, screenSize.width, screenSize.height);
    }

    @Override
    public void renderPost(Graphics2D g) {
        Font font = g.getFont();
        g.setColor(Color.black);
        this.drawCentered(g, font.deriveFont(50.0f), "LAN Bros!", screenSize.width / 2, 100);
        this.drawCentered(g, font.deriveFont(30.0f), "This is your ip address for clients to join",
                screenSize.width / 2, 200);
        this.drawCentered(g, font.deriveFont(20.0f),
                "Players that have joined: " + factory.getCurrentServer().getPlayerList(), screenSize.width / 2, 225);

    }

    @Override
    public void stateLoaded() {
        // TODO Auto-generated method stub
        screenSize = frame.getBounds();
        addInputWithMargin(screenSize.width / 2, 250, 15);
        addButtonsWithMargin(screenSize.width / 2, 250 + 100, 15);

    }

    @Override
    public void stateUnloaded() {
        removeButtons();
        removeInputs();
    }

}
