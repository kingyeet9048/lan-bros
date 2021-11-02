package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	
	// instance variables
	private ServerSocket server;
	
	private List<Socket> connectionSockets;
	
	public Server(int port) {
		// TODO Auto-generated constructor stub
		try {
			server = new ServerSocket(port);
			connectionSockets = new ArrayList<>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.printf("Server Error: %s\n", e.getMessage());
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
