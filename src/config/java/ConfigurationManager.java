package config.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {

    // instance variables
    // current supported configs
    private final String fileName = "resources/.env-settings";
    private int SERVER_PORT;
    private int MAX_PLAYERS;
    private int GAME_COUNTDOWN;

    public void loadConfigs() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));

            String currentLine = reader.readLine();
            System.out.println("Loading configs...");
            while (currentLine != null) {
                String[] splitLine = currentLine.split("=");

                if (splitLine[0].equals("SERVER_PORT")) {
                    SERVER_PORT = Integer.parseInt(splitLine[1]);
                    System.out.println(currentLine + " " + SERVER_PORT);
                } else if (splitLine[0].equals("MAX_PLAYERS")) {
                    MAX_PLAYERS = Integer.parseInt(splitLine[1]);
                    System.out.println(currentLine + " " + MAX_PLAYERS);
                } else if (splitLine[0].equals("GAME_COUNTDOWN")) {
                    GAME_COUNTDOWN = Integer.parseInt(splitLine[1]);
                    System.out.println(currentLine + " " + GAME_COUNTDOWN);
                }
                currentLine = reader.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int getSERVER_PORT() {
        return SERVER_PORT;
    }

    public void setSERVER_PORT(int sERVER_PORT) {
        SERVER_PORT = sERVER_PORT;
    }

    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }

    public void setMAX_PLAYERS(int mAX_PLAYERS) {
        MAX_PLAYERS = mAX_PLAYERS;
    }

    public int getGAME_COUNTDOWN() {
        return GAME_COUNTDOWN;
    }

    public void setGAME_COUNTDOWN(int gAME_COUNTDOWN) {
        GAME_COUNTDOWN = gAME_COUNTDOWN;
    }
}