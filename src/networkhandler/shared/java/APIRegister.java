package networkhandler.shared.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import io.java.KeyBind;
import io.java.UserInput;
import networkhandler.server.java.Request;
import networkhandler.server.java.ServerWorker;

public class APIRegister {

    private Factory factory;

    public APIRegister(Factory factory) {
        this.factory = factory;
    }

    public ConcurrentHashMap<String, NetPacket> makeBaseAPIRegistry() {
        factory.getAPIRegistry().put("/api/playersync", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/playersync";
            }

            @Override
            public void clientExecute(Map map) {
                factory.getCurrentClient().applyPlayerSync((String) map.get("coordinates"));
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                Map<Socket, ServerWorker> clients = factory.getCurrentServer().getWorkers();
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

        factory.getAPIRegistry().put("/api/conn/client/connection", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/connection";
            }

            @Override
            public void clientExecute(Map map) {
                factory.getCurrentClient().addPlayerToList((String) map.get("username"));
                factory.getCurrentClient().updatePlayers();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                // if the flag for game close is true, return false
                if (!factory.getCurrentServer().isGameClosed()) {
                    Map<Socket, ServerWorker> clients = factory.getCurrentServer().getWorkers();
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

        factory.getAPIRegistry().put("/api/conn/client/disconnection", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/disconnection";
            }

            @Override
            public void clientExecute(Map map) {
                factory.getCurrentClient().removePlayerFromList((String) map.get("username"));
                factory.getCurrentClient().updatePlayers();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                Map<Socket, ServerWorker> clients = factory.getCurrentServer().getWorkers();
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

        factory.getAPIRegistry().put("/api/conn/listUpdate", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/listUpdate";
            }

            @Override
            public void clientExecute(Map map) {
                String[] players = ((String) map.get("usernames")).split(",");
                for (String player : players) {
                    if (!player.equals("") || player != null) {
                        factory.getCurrentClient().addPlayerToList(player);
                    }
                }
                factory.getCurrentClient().updatePlayers();
                System.out.println("update full list of current players");
            }

            @Override
            public void serverExecute(Request request) throws IOException {
            }

        });

        factory.getAPIRegistry().put("/api/game/started", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/game/started";
            }

            @Override
            public void clientExecute(Map map) {
                // TODO: start game
                System.out.println("Game would start here");
                factory.getCurrentClient().startGame();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                startEndGame(request);
            }

        });

        factory.getAPIRegistry().put("/api/game/end", new NetPacket() {

            @Override
            String getCommand() {
                return "/api/game/end";
            }

            @Override
            public void clientExecute(Map map) {
                // TODO: end game
                System.out.println("Game would end here");
                factory.getCurrentClient().endGame();
            }

            @Override
            public void serverExecute(Request request) throws IOException {
                startEndGame(request);
            }

        });

        factory.getAPIRegistry().put("/api/movement", new NetPacket() {

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
                    Map<Socket, ServerWorker> clients = factory.getCurrentServer().getWorkers();
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

        return factory.getAPIRegistry();
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
            factory.getCurrentServer().setGameClosed(true);
        } else if (request.getApi().contains("/end")) {
            state = "false";
            factory.getCurrentServer().setGameClosed(false);
        }

        if (!state.equals("")) {
            Map<Socket, ServerWorker> clients = factory.getCurrentServer().getWorkers();
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
}
