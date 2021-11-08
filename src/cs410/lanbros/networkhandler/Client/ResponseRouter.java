package cs410.lanbros.networkhandler.client;

import java.util.Map;

/**
 * Handles routing the reponses to the proper logic that will take care of the
 * response
 * 
 * @author Sulaiman Bada
 */
public class ResponseRouter {

    // instance variables
    private Client client;

    /**
     * router constructor
     * 
     * @param client
     */
    public ResponseRouter(Client client) {
        this.client = client;
    }

    public boolean handleResponse(Response response) {
        // get the mapped response
        Map map = response.getMappedResponse();
        String api = (String) map.get("api");

        // routes the api request.
        boolean result = false;
        if (api.contains("/api/conn")) {
            result = handleConn(map, api);
        } else if (api.contains("/api/game")) {
            result = handleGameState(map, api);
        } else if (api.contains("/api/movement")) {
            result = handleMovement(map, api);
        }

        return result;
    }

    /**
     * Connection type api
     * 
     * @param map
     * @param api
     * @apiNote Refer to readme for more info
     * @return
     */
    private boolean handleConn(Map map, String api) {
        if (api.contains("/client/connection")) {
            client.addPlayerToList((String) map.get("username"));
            client.updatePlayers();
        }
        if (api.contains("/client/disconnection")) {
            client.removePlayerFromList((String) map.get("username"));
            client.updatePlayers();
        }
        if (api.contains("/client/listUpdate")) {
            String[] players = ((String) map.get("usernames")).split(",");
            for (String player : players) {
                if (!player.equals("") || player != null) {
                    client.addPlayerToList(player);
                }
            }
            client.updatePlayers();
            System.out.println("update full list of current players");
        }
        return true;
    }

    /**
     * Game started or ending api
     * 
     * @param map
     * @param api
     * @apiNote Refer to readme for more info
     * 
     * @return
     */
    private boolean handleGameState(Map map, String api) {
        if (api.contains("/started")) {
            // TODO: start game
            System.out.println("Game would start here");
            client.startGame();
        } else if (api.contains("/end")) {
            // TODO: end game
            System.out.println("Game would end here");
            client.endGame();
        }
        return true;
    }

    /**
     * Player movement api
     * 
     * @param map
     * @param api
     * @apiNote Refer to readme for more info
     * @return
     */
    private boolean handleMovement(Map map, String api) {
        String player = (String) map.get("username");
        String movement = (String) map.get("movement");
        client.movePlayer(movement, player);
        return true;
    }
}
