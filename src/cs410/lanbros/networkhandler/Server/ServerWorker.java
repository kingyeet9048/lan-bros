package cs410.lanbros.networkhandler.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Server worker will listen for new request from a speciic socket
 * 
 * @CreatedBy Sulaiman Bada
 * @AmendedBy Sheikh Fahad
 */
public class ServerWorker implements Runnable {

	// instance variables
	private Socket connectionDetail;
	private BufferedReader reader;
	private Server server;
	private boolean terminateThread = false;

	/**
	 * Constructor will need the socket to listen to and the instance of a server.
	 * 
	 * @param connectionDetail
	 * @param server
	 */
	public ServerWorker(Socket connectionDetail, Server server) {
		this.connectionDetail = connectionDetail;
		this.server = server;
		try {
			reader = new BufferedReader(new InputStreamReader(connectionDetail.getInputStream()));
		} catch (IOException e) {
			System.err.printf("ServerWorker Error: %s\n", e.getMessage());
		}
	}

	/**
	 * 
	 * @return whether the thread should be terminated
	 */
	public boolean isTerminateThread() {
		return terminateThread;
	}

	/**
	 * Sets the termination boolean
	 * 
	 * @param terminateThread
	 */
	public void setTerminateThread(boolean terminateThread) {
		this.terminateThread = terminateThread;
	}

	@Override
	public void run() {

		System.out.println("New ServerWorker Started");
		// while the socket is alive
		while (!connectionDetail.isInputShutdown() && (!connectionDetail.isClosed())
				&& connectionDetail.isConnected()) {
			try {
				// while the thread is alive and the socket can be read from
				if (terminateThread || reader.read() == -1) {
					connectionDetail.close();
					break;
				}
				// read the api call and wrap it around the request object.
				// will also add it to the queue
				String apiCall = reader.readLine();
				if (apiCall == null) {
					continue;
				}
				Request request = new Request(connectionDetail, apiCall);
				server.addToQueue(request);
			} catch (SocketException e1) {
				System.err.printf("ServerWorker Read Error: %s\n", e1.getMessage());
				break;

			} catch (IOException e) {
				System.err.printf("ServerWorker Read Error: %s\n", e.getMessage());
			}
		}
		// worker is terminating...
		// we need to let the other clients know that the client is disconnecting
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
