package gui.state.java;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.io.IOException;

import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import gui.components.java.GuiInput;
import main.java.Main;
import networkhandler.shared.java.Factory;
import java.util.concurrent.TimeUnit;

public class HostGameState extends GuiState {

    private Rectangle screenSize;
    public Factory factory;
    private boolean isGameStarted = false;
    private int currentTimer;

    public HostGameState(GuiFrame frame, Factory factory) {
        super(frame);
        Main.startServer();
        this.factory = factory;
        currentTimer = factory.getGAME_COUNTDOWN();
        inputs = new GuiInput[] { new GuiInput(factory.getCurrentServer().getIpAddress()) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void focusGained(FocusEvent e) {

            }
        } };
        inputs[0].setEditable(false);
        buttons = new GuiButton[] { new GuiButton("Start Game") {

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick(boolean pressed) {
                if (pressed) {
                    if (Main.startClient("localhost")) {
                        isGameStarted = true;
                        new Thread(() -> {
                            while (true) {
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (currentTimer == 0) {
                                    break;
                                }
                                currentTimer--;
                            }
                            factory.setHost(true);
                            factory.getCurrentClient().setHost(true);
                            factory.getCurrentClient().tellClientsToStart();
                        }).start();
                    }
                }
            }

        }, new GuiButton("Go Back to Title") {

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
                this.drawCentered(g, font.deriveFont(20.0f),isGameStarted ? "Starting game in " + currentTimer: "", screenSize.width / 2, 246);

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
