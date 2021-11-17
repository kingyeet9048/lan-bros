package networkhandler.java;

import gui.java.GuiFrame;
import gui.state.java.InMultiplayerGameState;
import networkhandler.client.java.Client;
import networkhandler.server.java.Server;

public class Factory {

    private Server server;
    private Client client;
    private int port = 4321;
    private int MAX_PLAYERS = 4;
    private String serverAddress;
    private int serverPort = 4321;
    private boolean isHost;
    private InMultiplayerGameState joinedGameState;

    public Factory() {

    }

    public Server getCurrentServer() {
        return server;
    }

    public Client getCurrentClient() {
        return client;
    }

    public Server makeServer() {
        server = new Server(port, MAX_PLAYERS);
        return server;
    }

    public Client makeClient() {
        client = new Client(serverAddress, serverPort, isHost, this);
        return client;
    }

    public InMultiplayerGameState makeGameState(GuiFrame frame, String thisPlayerName) {
        joinedGameState = new InMultiplayerGameState(frame, thisPlayerName);
        return joinedGameState;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

}
