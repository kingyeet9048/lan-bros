package networkhandler.shared.java;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import config.java.ConfigurationManager;
import gui.components.java.GuiFrame;
import gui.state.java.InMultiplayerGameState;
import networkhandler.client.java.Client;
import networkhandler.server.java.Server;

public class Factory {

    private Server server;
    private Client client;
    private int MAX_PLAYERS = 4;
    private String serverAddress;
    private int serverPort;
    private boolean isHost;
    private InMultiplayerGameState joinedGameState;
    private ConfigurationManager configurationManager;
    private APIRegister register;
    private ConcurrentHashMap<String, NetPacket> apiRegistry = new ConcurrentHashMap<>();
    private final LinkedList<String> supportAPIs = new LinkedList<String>(
            Arrays.asList("/api/playersync", "/api/conn/client/connection", "/api/conn/client/disconnection",
                    "/api/conn/listUpdate", "/api/game/started", "/api/game/end", "/api/movement"));

    public ConcurrentHashMap<String, NetPacket> getAPIRegistry() {
        return apiRegistry;
    }

    public void startFactory() {
        makeRegister();
        register.makeBaseAPIRegistry();
        loadConfigs();
    }

    public void loadConfigs() {
        makeConfigurationManager();
        configurationManager.loadConfigs();
        setMAX_PLAYERS(configurationManager.getMAX_PLAYERS());
        setServerPort(configurationManager.getSERVER_PORT());
    }

    public Server getCurrentServer() {
        return server;
    }

    public Client getCurrentClient() {
        return client;
    }

    public ConfigurationManager makeConfigurationManager() {
        configurationManager = new ConfigurationManager();
        return configurationManager;
    }

    public Server makeServer() {
        server = new Server(serverPort, MAX_PLAYERS, this);
        return server;
    }

    public Client makeClient() {
        client = new Client(serverAddress, serverPort, isHost, this);
        return client;
    }

    public APIRegister makeRegister() {
        register = new APIRegister(this);
        return register;
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

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public LinkedList<String> getSupportAPIs() {
        return supportAPIs;
    }

    public APIRegister getRegister() {
        return register;
    }

    public String getPathFromSupported(String input) {
        for (String string : supportAPIs) {
            if (input.contains(string)) {
                return string;
            }
        }
        return null;
    }

}
