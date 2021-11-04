package cs410.lanbros.networkhandler.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Router will accept apis and route them to the corresponding logic. Router
 * will also handle sending the payload simply because the router will know
 * which receipients the payload are meant for.
 * 
 * @author Sheikh Fahad, Sulaiman Bada
 * @apiNote The Router will handle routing the api string to where they need to
 *          go.
 */
public class Router {
    // TODO: make requests

    private Server server;

    public Router(Server server) {
        this.server = server;
    }

    public boolean routeRequest(Request request) throws IOException {
        // place holder for routing the request...
        // ex /api/conn/client
        String api = request.getApi();
        // String[] apiSplit = api.split("/");

        boolean result = false;
        if (api.contains("/api/conn")) {
            result = handleConnection(request);
        } else if (api.contains("/api/")) {

        }

        return result;
    }

    public boolean handleConnection(Request request) throws IOException {
        if (request.getApi().contains("/client/connection")) {
            Map<Socket, ServerWorker> clients = server.getWorkers();
            for (Map.Entry<Socket, ServerWorker> entry : clients.entrySet()) {
                Socket currentKey = entry.getKey();

                PrintWriter writer = new PrintWriter(currentKey.getOutputStream());

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
}
