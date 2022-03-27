package networkhandler.shared.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import config.java.ConfigurationManager;
import content.npc.java.ClientPlayerNPC;
import content.npc.java.NPC;
import content.tile.java.TileFace;
import config.java.ConfigurationManager;
import gui.components.java.GuiFrame;
import gui.state.java.InMultiplayerGameState;
import gui.state.java.PauseState;
import networkhandler.client.java.Client;
import networkhandler.server.java.Server;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Factory {

    private Server server;
    private Client client;
    private int MAX_PLAYERS = 4;
    private int GAME_COUNTDOWN;
    private String serverAddress;
    private int serverPort;
    private boolean isHost;
    private InMultiplayerGameState joinedGameState;
    private ConcurrentHashMap<String, NetPacket> apiRegistry = new ConcurrentHashMap<>();
    private final LinkedList<String> supportAPIs = new LinkedList<String>();
    private String username;
    private ConfigurationManager configurationManager;
    private APIRegister register;
    private PauseState pauseState;

    public ConcurrentHashMap<String, NetPacket> getAPIRegistry() {
        return apiRegistry;
    }

    public Server getCurrentServer() {
        return server;
    }

    public Client getCurrentClient() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    public Server makeServer() {
        server = new Server(serverPort, MAX_PLAYERS, this);
        return server;
    }

    public Client makeClient() {
        client = new Client(serverAddress, serverPort, isHost, username, this);
        return client;
    }

    public PauseState makePauseState(GuiFrame frame) {
        if (pauseState == null) {
            pauseState = new PauseState(frame, this);
        }
        return pauseState;
    }

    public void setPauseState(PauseState state) {
        pauseState = state;
    }

    public InMultiplayerGameState makeGameState(GuiFrame frame, String thisPlayerName) {
        joinedGameState = new InMultiplayerGameState(frame, thisPlayerName);
        return joinedGameState;
    }

    public int getMAX_PLAYERS() {
        return MAX_PLAYERS;
    }

    public InMultiplayerGameState getJoinedGameState() {
        return joinedGameState;
    }

    public void setMAX_PLAYERS(int mAX_PLAYERS) {
        MAX_PLAYERS = mAX_PLAYERS;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getGAME_COUNTDOWN() {
        return GAME_COUNTDOWN;
    }

    public void setGAME_COUNTDOWN(int gAME_COUNTDOWN) {
        GAME_COUNTDOWN = gAME_COUNTDOWN;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public LinkedList<String> getSupportAPIs() {
        return supportAPIs;
    }

    public String getPathFromSupported(String input) {
        for (String string : supportAPIs) {
            if (input.contains(string)) {
                return string;
            }
        }
        return null;
    }

    public void setPlayerUsername(String username) {
        this.username = username;
    }

    public ConfigurationManager makeConfigurationManager() {
        configurationManager = new ConfigurationManager();
        return configurationManager;
    }

    public void loadConfigs() {
        makeConfigurationManager();
        configurationManager.loadConfigs();
        setMAX_PLAYERS(configurationManager.getMAX_PLAYERS());
        setServerPort(configurationManager.getSERVER_PORT());
        setGAME_COUNTDOWN(configurationManager.getGAME_COUNTDOWN());
    }

    public APIRegister makeRegister() {
        register = new APIRegister(this);
        return register;
    }

    public APIRegister getRegister() {
        return register;
    }

    public void startFactory() {
        makeRegister().makeBaseAPIRegistry();
        loadConfigs();
    }

}
