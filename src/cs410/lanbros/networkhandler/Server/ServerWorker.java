package cs410.lanbros.networkhandler.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ServerWorker implements Runnable {

	private Socket connectionDetail;
	private BufferedReader reader;
	private Server server;
	private boolean terminateThread = false;

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

	public boolean isTerminateThread() {
		return terminateThread;
	}

	public void setTerminateThread(boolean terminateThread) {
		this.terminateThread = terminateThread;
	}

	@Override
	public void run() {
		System.out.println("New ServerWorker Started");
		while (!connectionDetail.isInputShutdown() && (!connectionDetail.isClosed())
				&& connectionDetail.isConnected()) {
			try {
				if (terminateThread || reader.read() == -1) {
					connectionDetail.close();
					break;
				}
				String apiCall = reader.readLine();
				if (apiCall == null) {
					continue;
				}
				Request request = new Request(connectionDetail, apiCall);
				server.addToQueue(request);
				// System.out.println("Added a request to the queue: " + apiCall);
			} catch (SocketException e1) {
				System.err.printf("ServerWorker Read Error: %s\n", e1.getMessage());
				break;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.printf("ServerWorker Read Error: %s\n", e.getMessage());
			}
		}
		server.getWorkers().remove(connectionDetail);
		Request request = new Request(connectionDetail, "/api/conn/client/disconnection");
		server.addToQueue(request);
		try {
			System.out.println("Connection with host terminated: " + connectionDetail.getInetAddress().getHostName());
			if (!connectionDetail.isClosed()) {
				connectionDetail.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.printf("ServerWorker Read Error: %s\n", e.getMessage());
		}
	}

}
