package cs410.lanbros.networkhandler.Server;

import java.net.Socket;

public class Request {

    private Socket receiver;
    private String api;

    public Request(Socket receiver, String api) {
        this.receiver = receiver;
        this.api = api;
    }

    public Socket getReceiver() {
        return receiver;
    }

    public void setReceiver(Socket receiver) {
        this.receiver = receiver;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
