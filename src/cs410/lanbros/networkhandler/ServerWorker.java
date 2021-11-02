package cs410.lanbros.networkhandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerWorker implements Runnable {

	private Socket connectionDetail;
	private BufferedReader reader;
	private Server server;

	public ServerWorker(Socket connectionDetail, Server server) {
		// TODO Auto-generated constructor stub
		this.connectionDetail = connectionDetail;
		this.server = server;
		try {
			reader = new BufferedReader(new InputStreamReader(connectionDetail.getInputStream()));
		} catch (IOException e) {
			System.err.printf("ServerWorker Error: %s\n", e.getMessage());
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while ((!connectionDetail.isClosed()) && connectionDetail.isConnected()) {
			try {
				String apiCall = reader.readLine();
				Request request = new Request(connectionDetail, apiCall);
				server.addToQueue(request);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.printf("ServerWorker Read Error: %s\n", e.getMessage());
			}
		}
		// TODO: Create new request to let all other players know that this connection
		// has been disconnected.
		server.getWorkers().remove(connectionDetail);
		// TODO: Fahad make routes
		Request request = new Request(connectionDetail, "/api/that/will/disconnect/client/from/game");
		server.addToQueue(request);
		// reduce the number of connected sockets by 1
		Listener list = server.getListener();
		list.setNumberOfConnections(list.getNumberOfConnections() - 1);
	}

}
