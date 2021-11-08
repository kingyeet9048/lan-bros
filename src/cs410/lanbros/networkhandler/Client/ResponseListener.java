package cs410.lanbros.networkhandler.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * This class is responible for listening for new responses. Once it receives a
 * new response, it will wrap the reponse in an object and add it to the end of
 * the queue.
 * 
 * @author Sulaiman Bada
 */
public class ResponseListener implements Runnable {

    // instance variables
    private Socket response;
    private Client client;
    private BufferedReader reader;

    /**
     * Needs the socket and client instance in order to listen and add.
     * 
     * @param response
     * @param client
     */
    public ResponseListener(Socket response, Client client) {
        this.response = response;
        this.client = client;
        try {
            // tries to make a new reader to listen with
            reader = new BufferedReader(new InputStreamReader(response.getInputStream()));
        } catch (IOException e) {
            System.err.printf("ResponseListener Read Error: %s\n", e.getMessage());
        }
    }

    @Override
    public void run() {
        // while the socket is still alive
        while (!response.isInputShutdown() && (!response.isClosed()) && response.isConnected()) {
            try {
                // tries to read the first space, if it doesnt error out, the pipe is still
                // alive.
                if (reader.read() == -1) {
                    // if we see a -1, the pipe is not alive.
                    // we need to close everything and shut this worker down.
                    response.close();
                    System.out.println("Nobody listening. Ending listener...");
                    break;
                }
                // received something good.
                String payload = reader.readLine();
                if (payload == null) {
                    continue;
                }
                // wrapping the response and adding to the queue.
                Response response = new Response(payload);
                client.appendToQueue(response);
            } catch (SocketException e1) {
                // pipe closed ending
                System.err.printf("ResponseListener Error: %s\n", e1.getMessage());
                break;
            }

            catch (IOException e) {
                System.err.printf("ResponseListener Error: %s\n", e.getMessage());
            }
        }
    }

}
