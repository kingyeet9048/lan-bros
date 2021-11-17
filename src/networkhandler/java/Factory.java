package networkhandler.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import gui.java.GuiFrame;
import gui.state.java.InMultiplayerGameState;
import io.java.KeyBind;
import io.java.UserInput;
import networkhandler.client.java.Client;
import networkhandler.server.java.Request;
import networkhandler.server.java.Server;
import networkhandler.server.java.ServerWorker;

public class Factory {

    private Server server;
    private Client client;
    private int port = 4321;
    private int MAX_PLAYERS = 4;
    private String serverAddress;
    private int serverPort = 4321;
    private boolean isHost;
    private InMultiplayerGameState joinedGameState;
    private ConcurrentHashMap<String, NetPacket> apiRegistry = new ConcurrentHashMap<>();
    private final LinkedList<String> supportAPIs = new LinkedList<String>(
            Arrays.asList("/api/playersync", "/api/conn/client/connection", "/api/conn/client/disconnection",
                    "/api/conn/listUpdate", "/api/game/started", "/api/game/end", "/api/movement"));

    public Factory() {
    }

    public ConcurrentHashMap<String, NetPacket> makeBaseAPIRegistry() {
        apiRegistry.put("/api/playersync", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/playersync";
            }

            @Override
            public void clientExecute(Map map) {
                client.applyPlayerSync((String) map.get("coordinates"));
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                Map<Socket, ServerWorker> clients = server.getWorkers();
                for (Map.Entry<Socket, ServerWorker> entry : clients.entrySet()) {
                    Socket currentKey = entry.getKey();

                    PrintWriter writer = new PrintWriter(currentKey.getOutputStream());

                    if (currentKey.equals(request.getReceiver())) {
                        continue;
                    }
                    Gson gson = new Gson();
                    Map<String, String> object = new HashMap<>();
                    object.put("api", "/api/playersync");
                    object.put("coordinates", request.getApi());
                    String payload = gson.toJson(object);
                    writer.write(" " + payload + "\n");
                    writer.flush();
                }
            }

        });

        apiRegistry.put("/api/conn/client/connection", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/connection";
            }

            @Override
            public void clientExecute(Map map) {
                client.addPlayerToList((String) map.get("username"));
                client.updatePlayers();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                // if the flag for game close is true, return false
                if (!server.isGameClosed()) {
                    Map<Socket, ServerWorker> clients = server.getWorkers();
                    StringBuilder builder = new StringBuilder();
                    for (Map.Entry<Socket, ServerWorker> entry : clients.entrySet()) {
                        Socket currentKey = entry.getKey();

                        PrintWriter writer = new PrintWriter(currentKey.getOutputStream());
                        if (currentKey.equals(request.getReceiver())) {
                            continue;
                        }
                        Gson gson = new Gson();
                        Map<String, String> object = new HashMap<>();
                        object.put("api", request.getApi());
                        object.put("username", request.getReceiver().getInetAddress().getHostName());
                        String payload = gson.toJson(object);

                        System.out.println(payload);

                        writer.write(" " + payload + "\n");
                        writer.flush();
                        builder.append(currentKey.getInetAddress().getHostName() + ",");
                    }
                    if (clients.size() >= 2) {
                        updateAllClientNames(builder.toString(), request);
                    }
                }
            }

        });

        apiRegistry.put("/api/conn/client/disconnection", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/disconnection";
            }

            @Override
            public void clientExecute(Map map) {
                client.removePlayerFromList((String) map.get("username"));
                client.updatePlayers();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                Map<Socket, ServerWorker> clients = server.getWorkers();
                for (Map.Entry<Socket, ServerWorker> entry : clients.entrySet()) {
                    Socket currentKey = entry.getKey();

                    PrintWriter writer = new PrintWriter(currentKey.getOutputStream());
                    // serverworker removes the connection from the list so there is no
                    // need to do it here.
                    Gson gson = new Gson();
                    Map<String, String> object = new HashMap<>();
                    object.put("api", request.getApi());
                    object.put("username", request.getReceiver().getInetAddress().getHostName());
                    String payload = gson.toJson(object);

                    System.out.println(payload);

                    writer.write(" " + payload + "\n");
                    writer.flush();
                }
            }

        });

        apiRegistry.put("/api/conn/listUpdate", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/listUpdate";
            }

            @Override
            public void clientExecute(Map map) {
                String[] players = ((String) map.get("usernames")).split(",");
                for (String player : players) {
                    if (!player.equals("") || player != null) {
                        client.addPlayerToList(player);
                    }
                }
                client.updatePlayers();
                System.out.println("update full list of current players");
            }

            @Override
            public void serverExecute(Request request) throws IOException {
            }

        });

        apiRegistry.put("/api/game/started", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/game/started";
            }

            @Override
            public void clientExecute(Map map) {
                // TODO: start game
                System.out.println("Game would start here");
                client.startGame();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                startEndGame(request);
            }

        });

        apiRegistry.put("/api/game/end", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/game/end";
            }

            @Override
            public void clientExecute(Map map) {
                // TODO: end game
                System.out.println("Game would end here");
                client.endGame();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                startEndGame(request);
            }

        });

        apiRegistry.put("/api/movement", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/movement";
            }

            @Override
            public void clientExecute(Map map) {
                String player = (String) map.get("username");
                String[] movement = ((String) map.get("movement")).split("_");
                KeyBind bind = KeyBind.values()[Integer.parseInt(movement[0])];
                boolean down = movement[1].equals("true");
                UserInput.setServerKeyPressed(player, bind, down);
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                String currentAPI = request.getApi();
                if (currentAPI.contains("_")) {
                    String[] inputActions = currentAPI.substring(currentAPI.lastIndexOf("/") + 1).split("_");
                    KeyBind keyBind = KeyBind.values()[Integer.parseInt(inputActions[0])];
                    Map<Socket, ServerWorker> clients = server.getWorkers();
                    for (Map.Entry<Socket, ServerWorker> entry : clients.entrySet()) {
                        Socket currentKey = entry.getKey();
                        if (currentKey.equals(request.getReceiver())) {
                            System.out.println("Router iterating through connection list: "
                                    + "this socket belongs to the requester. Skipping...");
                            continue;
                        }
                        PrintWriter writer = new PrintWriter(currentKey.getOutputStream());

                        Gson gson = new Gson();
                        Map<String, String> object = new HashMap<>();
                        object.put("api", request.getApi());
                        object.put("username", request.getReceiver().getInetAddress().getHostName());
                        object.put("movement", keyBind.ordinal() + "_" + inputActions[1]);
                        String payload = gson.toJson(object);

                        System.out.println(payload);

                        writer.write(" " + payload + "\n");
                        writer.flush();
                    }
                }
            }

        });

        return apiRegistry;
    }

    /**
     * update the requested connection socket with a list of all connected clients
     * 
     * @param names
     * @param request
     * @apiNote refer to the readme
     * @throws IOException
     */
    private void updateAllClientNames(String names, Request request) throws IOException {
        Gson gson = new Gson();
        Map<String, String> object = new HashMap<>();
        object.put("api", "/api/conn/client/listUpdate");
        object.put("usernames", names);
        String payload = gson.toJson(object);
        PrintWriter writer = new PrintWriter(request.getReceiver().getOutputStream());
        writer.write(" " + payload + "\n");
        writer.flush();
    }

    /**
     * Start or ends the game
     * 
     * @param request
     * @apiNote refer to the readme
     * @return
     * @throws IOException
     */
    private boolean startEndGame(Request request) throws IOException {
        String state = "";
        if (request.getApi().contains("/stared")) {
            state = "true";
            server.setGameClosed(true);
        } else if (request.getApi().contains("/end")) {
            state = "false";
            server.setGameClosed(false);
        }

        if (!state.equals("")) {
            Map<Socket, ServerWorker> clients = server.getWorkers();
            for (Map.Entry<Socket, ServerWorker> entry : clients.entrySet()) {
                Socket currentKey = entry.getKey();

                PrintWriter writer = new PrintWriter(currentKey.getOutputStream());

                if (currentKey.equals(request.getReceiver())) {
                    continue;
                }

                Gson gson = new Gson();
                Map<String, String> object = new HashMap<>();
                object.put("api", request.getApi());
                object.put("gameState", state);
                String payload = gson.toJson(object);

                System.out.println(payload);

                writer.write(" " + payload + "\n");
                writer.flush();
            }
        }
        return true;
    }

    public ConcurrentHashMap<String, NetPacket> getAPIRegistry() {
        return apiRegistry;
    }

    public Server getCurrentServer() {
        return server;
    }

    public Client getCurrentClient() {
        return client;
    }

    public Server makeServer() {
        server = new Server(port, MAX_PLAYERS, this);
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

}
