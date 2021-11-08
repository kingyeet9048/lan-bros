package cs410.lanbros.networkhandler.Server;

import java.net.Socket;

/**
 * Request class to hold an instance of a request from a client.
 * 
 * @author Sulaiman Bada
 */
public class Request {

    // instance variables
    private Socket receiver;
    private String api;

    /**
     * Constructor for request. Needs socket that sent it and the api used.
     * 
     * @param receiver
     * @param api
     */
    public Request(Socket receiver, String api) {
        this.receiver = receiver;
        this.api = api;
    }

    /**
     * get socket
     * 
     * @return
     */
    public Socket getReceiver() {
        return receiver;
    }

    /**
     * set socket
     * 
     * @param receiver
     */
    public void setReceiver(Socket receiver) {
        this.receiver = receiver;
    }

    /**
     * get the api from the request.
     * 
     * @return
     */
    public String getApi() {
        return api;
    }

    /**
     * set the api
     * 
     * @param api
     */
    public void setApi(String api) {
        this.api = api;
    }
}
