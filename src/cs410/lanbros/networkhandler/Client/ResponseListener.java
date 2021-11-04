package cs410.lanbros.networkhandler.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import com.google.gson.Gson;

public class ResponseListener implements Runnable {

    private Socket response;
    private Client client;
    private BufferedReader reader;

    public ResponseListener(Socket response, Client client) {
        this.response = response;
        this.client = client;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getInputStream()));
        } catch (IOException e) {
            System.err.printf("ResponseListener Read Error: %s\n", e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!response.isInputShutdown() && (!response.isClosed()) && response.isConnected()) {
            try {
                if (reader.read() == -1) {
                    response.close();
                    System.out.println("Nobody listening. Ending listener...");
                    break;
                }
                String payload = reader.readLine();
                if (payload == null) {
                    continue;
                }
                Response response = new Response(payload);
                client.appendToQueue(response);
            } catch (SocketException e1) {
                System.err.printf("ResponseListener Error: %s\n", e1.getMessage());
                break;
            }
            
            catch (IOException e) {
                System.err.printf("ResponseListener Error: %s\n", e.getMessage());
            }
        }
    }

}
