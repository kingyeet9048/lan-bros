package networkhandler.shared.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import content.npc.java.ClientPlayerNPC;
import content.npc.java.NPC;
import content.tile.java.TileFace;
import io.java.KeyBind;
import io.java.UserInput;
import main.java.Main;
import networkhandler.server.java.Request;
import networkhandler.server.java.ServerWorker;

public class APIRegister {

    private Factory factory;

    public APIRegister(Factory factory) {
        this.factory = factory;
    }

    public ConcurrentHashMap<String, NetPacket> makeBaseAPIRegistry() {
        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/playersync";
            }

            @Override
			@SuppressWarnings("rawtypes")
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

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/connection";
            }

            @Override
			@SuppressWarnings("rawtypes")
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
                        String[] splitString = request.getApi().split("/");
                        if (currentKey.equals(request.getReceiver())) {
                            entry.getValue().setPlayersUsername(splitString[splitString.length - 1]);
                            continue;
                        }
                        Gson gson = new Gson();
                        Map<String, String> object = new HashMap<>();
                        object.put("api", request.getApi());
                        object.put("username", splitString[splitString.length - 1]);
                        String payload = gson.toJson(object);

                        System.out.println(payload);

                        writer.write(" " + payload + "\n");
                        writer.flush();
                        builder.append(entry.getValue().getPlayersUsername() + ",");
                    }
                    if (clients.size() >= 2) {
                        updateAllClientNames(builder.toString(), request);
                    }
                }
            }

        });

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/disconnection";
            }

            @Override
			@SuppressWarnings("rawtypes")
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
                    // no need to remove because server worker already removed...
                    Gson gson = new Gson();
                    Map<String, String> object = new HashMap<>();
                    object.put("api", request.getApi());
                    // request player name is only set when serverworker disconnects
                    // we neeed this because the serverworker holds the playersname for the
                    // associated socket
                    // but when the serverworker disconnects from the client, it need to tear
                    // down/end so it sets the
                    // requests playerName so the api can be sent out that this player has
                    // disconnected.
                    object.put("username", request.playerName);
                    String payload = gson.toJson(object);

                    System.out.println(payload);

                    writer.write(" " + payload + "\n");
                    writer.flush();
                }
            }

        });

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/conn/client/listUpdate";
            }

            @Override
			@SuppressWarnings("rawtypes")
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

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/game/started";
            }

            @Override
			@SuppressWarnings("rawtypes")
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

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/game/end";
            }

            @Override
			@SuppressWarnings("rawtypes")
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

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/movement";
            }

            @Override
			@SuppressWarnings("rawtypes")
            public void clientExecute(Map map) {
                String player = (String) map.get("username");
                String[] movement = ((String) map.get("movement")).split("_");
                KeyBind bind = KeyBind.values()[Integer.parseInt(movement[0])];
                boolean down = movement[1].equals("true");
                if (bind.keyCodes == KeyBind.PAUSE.keyCodes) {
                    if (down) {
                        Main.pauseGame(player);
                    } else {
                        Main.unPauseGame();
                    }
                }
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
                        object.put("username", clients.get(request.getReceiver()).getPlayersUsername());
                        object.put("movement", keyBind.ordinal() + "_" + inputActions[1]);
                        String payload = gson.toJson(object);

                        System.out.println(payload);

                        writer.write(" " + payload + "\n");
                        writer.flush();
                    }
                }
            }

        });

        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/setposmotion";
            }

            @Override
			@SuppressWarnings("rawtypes")
            public void clientExecute(Map map) {
                if (factory.getCurrentClient().getCurrentLevel() != null) {
                    String username = (String) map.get("username");
                    String[] posStrings = ((String) map.get("position")).split("_");
                    String[] motionStrings = ((String) map.get("position")).split("_");
                    TileFace face = null;

                    if (map.containsKey("wall")) {
                        face = TileFace.values()[Integer.parseInt((String) map.get("wall"))];
                    }

                    for (ClientPlayerNPC player : factory.getCurrentClient().getCurrentLevel().playerSet) {
                        if (player.playerName.equals(username)) {
                            player.npcX = Float.parseFloat(posStrings[0]);
                            player.npcY = Float.parseFloat(posStrings[1]);
                            player.motionX = Float.parseFloat(motionStrings[0]);
                            player.motionY = Float.parseFloat(motionStrings[1]);
                            player.wallHit = face;
                        }
                    }
                }
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
                    String[] parameters = request.getApi().substring(request.getApi().lastIndexOf("/") + 1).split("_");
                    object.put("api", request.getApi());
                    object.put("username", clients.get(request.getReceiver()).getPlayersUsername());
                    object.put("position", parameters[0] + "_" + parameters[1]);
                    object.put("motion", parameters[2] + "_" + parameters[3]);

                    if (parameters.length == 5) {
                        object.put("wall", parameters[4] + "");
                    }

                    String payload = gson.toJson(object);

                    System.out.println(payload);

                    writer.write(" " + payload + "\n");
                    writer.flush();
                }
            }

        });
        
        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/healthsync";
            }

            @Override
			@SuppressWarnings("rawtypes")
            public void clientExecute(Map map) {
            	if(factory.getCurrentClient().getCurrentLevel() != null)
            	{
                    int index = Integer.parseInt((String) map.get("index"));
                    String[] posStrings = ((String) map.get("position")).split("_");
                    int health = Integer.parseInt((String)map.get("life"));
                    float[] pos = {Float.parseFloat(posStrings[0]),Float.parseFloat(posStrings[1])};
                    
                    ArrayList<NPC> set = factory.getCurrentClient().getCurrentLevel().npcSet;
                    boolean flag = false;
                    if(set.size() > index)
                    {
                        NPC foundNPC = set.get(index);
                        
                        if(foundNPC.npcX == pos[0] && foundNPC.npcY == pos[1])
                        {
                        	foundNPC.setLife(health);
                        }                    	
                        else
                        {
                        	flag = true;
                        }
                    }
                    else
                    {
                    	flag = true;
                    }
                    
                    if(flag)
                    {
                    	for (NPC npc : set) {
                        	if(npc.npcX == pos[0] && npc.npcY == pos[1]) 
                        	{
                            	npc.setLife(health);
                        	}
                        }   
                    }
            	}
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
                    String[] parameters = request.getApi().substring(request.getApi().lastIndexOf("/")+1).split("_");
                    object.put("api", request.getApi());
                    object.put("index", parameters[0]);
                    object.put("position", parameters[1] + "_" + parameters[2]);
                    object.put("life", parameters[3]);
                    String payload = gson.toJson(object);

                    System.out.println(payload);

                    writer.write(" " + payload + "\n");
                    writer.flush();
                }
            }

        });
        
        addSupportedCommand(new NetPacket() {

            @Override
            String getCommand() {
                return "/api/cleanup/npcremove";
            }

            @Override
			@SuppressWarnings("rawtypes")
            public void clientExecute(Map map) {
            	if(factory.getCurrentClient().getCurrentLevel() != null)
            	{
                    int index = Integer.parseInt((String) map.get("index"));
                    String[] posStrings = ((String) map.get("position")).split("_");
                    float[] pos = {Float.parseFloat(posStrings[0]),Float.parseFloat(posStrings[1])};
                    
                    ArrayList<NPC> set = factory.getCurrentClient().getCurrentLevel().npcSet;
                    
                    boolean flag = false;
                    if(set.size() > index)
                    {
                        NPC foundNPC = set.get(index);
                        
                        if(foundNPC.npcX == pos[0] && foundNPC.npcY == pos[1])
                        {
                        	factory.getCurrentClient().getCurrentLevel().queueForRemoval(foundNPC);
                        }                    	
                        else
                        {
                        	flag = true;
                        }
                    }
                    else
                    {
                    	flag = true;
                    }
                    
                    if(flag)
                    {
                    	for (NPC npc : set) {
                        	if(npc.npcX == pos[0] && npc.npcY == pos[1]) 
                        	{
                            	factory.getCurrentClient().getCurrentLevel().queueForRemoval(npc);
                        	}
                        }   
                    }
            	}
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
                    String[] parameters = request.getApi().substring(request.getApi().lastIndexOf("/")+1).split("_");
                    object.put("api", request.getApi().substring(0,request.getApi().lastIndexOf("/")+1));
                    object.put("index", parameters[0]);
                    object.put("position", parameters[1] + "_" + parameters[2]);
                    String payload = gson.toJson(object);

                    System.out.println(payload);

                    writer.write(" " + payload + "\n");
                    writer.flush();
                }
            }

        });

        return factory.getAPIRegistry();
    }

    public void addSupportedCommand(NetPacket packet) {
        factory.getSupportAPIs().add(packet.getCommand());
        factory.getAPIRegistry().put(packet.getCommand(), packet);
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
        if (request.getApi().contains("/started")) {
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
