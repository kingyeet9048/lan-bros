package networkhandler.server.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import io.java.KeyBind;
import networkhandler.java.Movements;

/**
 * Router will accept apis and route them to the corresponding logic. Router
 * will also handle sending the payload simply because the router will know
 * which receipients the payload are meant for.
 * 
 * @CreatedBy Sulaiman Bada
 * @AmendedBy Sheikh Fahad
 * @apiNote The Router will handle routing the api string to where they need to
 *          go.
 */
public class Router {

    // instance variables
    private Server server;

    /**
     * Constructor for the router. Needs the server instance.
     * 
     * @param server
     */
    public Router(Server server) {
        this.server = server;
    }

    /**
     * routes the request to where it need to go and returns true or false depending
     * on if the request was mapped successfully.
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public boolean routeRequest(Request request) throws IOException {
        String api = request.getApi();

        boolean result = false;
        if (api.contains("/api/conn")) {
            result = handleConn(request);
        } else if (api.contains("/api/game")) {
            result = startEndGame(request);
        } else if (api.contains("/api/movement/")) {
            result = handleMovement(request);
        } else if (api.contains("/api/playersync/")) {
            result = syncPlayers(request);
        }

        return result;
    }

    /**
     * Handles the connection api
     * 
     * @param request
     * @apiNote refer to the readme
     * @return
     * @throws IOException
     */
    private boolean handleConn(Request request) throws IOException {
        if (request.getApi().contains("/client/connection")) {
            // if the flag for game close is true, return false
            if (server.isGameClosed()) {
                return false;
            }
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
        if (request.getApi().contains("/client/disconnection")) {
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
        return true;
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
     * update the requested connection socket with a list of all connected clients
     * 
     * @param request
     * @apiNote refer to the readme
     * @throws IOException
     */
    private boolean syncPlayers(Request request) throws IOException {
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
        return true;
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

    /**
     * Moves a player
     * 
     * @param request
     * @apiNote refer to the readme
     * @return
     * @throws IOException
     */
    private boolean handleMovement(Request request) throws IOException {
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

            return true;
        }

        return false;
    }
}