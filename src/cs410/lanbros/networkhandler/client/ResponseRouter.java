package cs410.lanbros.networkhandler.client;

import java.util.Map;

public class ResponseRouter {

    private Client client;

    public ResponseRouter(Client client) {
        this.client = client;
    }

    public boolean handleResponse(Response response) {
        // place holder for routing the request...
        // ex /api/conn/client
        Map map = response.getMappedResponse();
        String api = (String) map.get("api");
        // String[] apiSplit = api.split("/");

        boolean result = false;
        if (api.contains("/api/conn")) {
            result = handleConnection(map, api);
        } else if (api.contains("/api/")) {

        }

        return result;
    }

    private boolean handleConnection(Map map, String api) {
        if (api.contains("/client/connection")) {
            client.addPlayerToList((String) map.get("username"));
            client.updatePlayers();
        }
        return true;
    }
}
